package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, ITree, Leaf, Tree}
import org.slf4j.LoggerFactory

class TreeStore(store: GraphStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def matchAllRootTrees(): List[Leaf] =
    store.getNodesWhere("NOT (r)<-[:PARENT_OF]-()", Map()).map(n => nodeToLeaf(n))

  def matchTreeById(id: Int): ITree = {
    try {
      val (rootNodes, parent2Children) =
        store.getTreesWhere("Id(r)=$id", Map("id" -> id))

      nodeToTree(rootNodes.head, parent2Children)
    } catch {
      case _: RecordNotFound => nodeToLeaf(getNodeById(id))
    }
  }

  def createLeaf(data: Data, parentId: Int): Leaf = {
    nodeToLeaf(store.createNodeByParentId(parentId, dataToMap(data)))
  }

  def updateTree(id: Int, data: Data): Leaf = {
    nodeToLeaf(store.updateNodeById(id, dataToMap(data)))
  }

  def removeTree(id: Int): Boolean = {
    store.deleteTreeById(id)

    true
  }

  private def getNodeById(id: Int): GraphNode = {
    val nodes = store.getNodesWhere("Id(r)=$id", Map("id" -> new Integer(id)))

    if (nodes.isEmpty) throw RecordNotFound(s"id: $id")
    nodes.head
  }

  private def dataToMap(data: Data): Map[String, Any] = {
    Map("title" -> data.title, "body" -> data.body)
  }

  private def nodeToLeaf(node: GraphNode): Leaf = {
    Leaf(node.id, nodeToData(node))
  }

  private def nodeToData(node: GraphNode): Data = {
    val title: String = node.data.get("title") match {
      case Some(t) => t.toString
      case None => throw MalformedData(node.toString)
    }

    val body: String = node.data.get("body") match {
      case Some(t) => t.toString
      case None => throw MalformedData(node.toString)
    }

    Data(title, body)
  }

  private def nodeToTree(rootNode: GraphNode, parent2Children: Map[GraphNode, Set[GraphNode]]): ITree = {
    val nodes = parent2Children
      .get(rootNode)
      .fold(List.empty[ITree])(_.map(node => nodeToTree(node, parent2Children)).toList)

    nodes match {
      case Nil => nodeToLeaf(rootNode)
      case _ => Tree(
        rootNode.id.toString,
        nodeToData(rootNode),
        nodes
      )
    }
  }
}
