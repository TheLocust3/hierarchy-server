package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, ITree, Leaf, Tree}
import org.neo4j.driver.internal.value.NullValue
import org.neo4j.driver.v1.Values.parameters
import org.slf4j.LoggerFactory

class TreeStore(store: GraphStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def matchAllRootTrees(): List[Leaf] =
    store.getNodesWhere("NOT (r)<-[:PARENT_OF]-()", NullValue.NULL).map(n => nodeToLeaf(n))

  def matchTreeById(id: Int): ITree = {
    try {
      val (rootNodes, parent2Children) =
        store.getTreesWhere("Id(r)=$id", parameters("id", new Integer(id)))

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
    val nodes = store.getNodesWhere("Id(r)=$id", parameters("id", new Integer(id)))

    if (nodes.isEmpty) throw RecordNotFound(s"id: $id")
    nodes.head
  }

  private def dataToMap(data: Data): Map[String, Any] = {
    Map("title" -> data.title, "body" -> data.body)
  }

  private def nodeToLeaf(node: GraphNode): Leaf = {
    Leaf(node.id, Data.fromNode(node))
  }

  private def nodeToTree(rootNode: GraphNode, parent2Children: Map[GraphNode, Set[GraphNode]]): Tree = {
    Tree(
      rootNode.id.toString,
      Data.fromNode(rootNode),
      parent2Children
        .get(rootNode)
        .fold(List.empty[ITree])(_.map(node => nodeToTree(node, parent2Children)).toList)
    )
  }
}
