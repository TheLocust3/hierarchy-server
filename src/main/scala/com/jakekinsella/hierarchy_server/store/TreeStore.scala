package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, ITree, Leaf, Tree}
import org.slf4j.LoggerFactory

class TreeStore(store: GraphStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def matchAllRootTrees(): List[Leaf] =
    store.getNodesWhere("NOT (r)<-[:PARENT_OF]-()", Map()).map(n => nodeToLeaf(n))

  def matchAllSpecialTrees: List[ITree] = {
    matchAllLabelTrees ++ matchAllStatusTrees
  }

  def matchTreeById(id: Int): ITree = {
    try {
      val (rootNodes, parent2Children) =
        store.getTreesWhere("Id(r)=$id", Map("id" -> id))

      nodeToTree(rootNodes.head, parent2Children)
    } catch {
      case _: RecordNotFound => nodeToLeaf(getNodeById(id))
    }
  }

  def createRelationship(parentId: Int, childId: Int): Leaf =
    nodeToLeaf(store.createRelationship(parentId, childId))

  def createLeaf(data: Data, parentId: Int): Leaf =
    nodeToLeaf(store.createNodeByParentId(parentId, dataToMap(data)))

  def createRootLeaf(data: Data): Leaf =
    nodeToLeaf(store.createRootNode(dataToMap(data)))

  def updateTree(id: Int, data: Data): Leaf =
    nodeToLeaf(store.updateNodeById(id, dataToMap(data)))

  def removeTree(id: Int): Boolean = {
    store.deleteTreeById(id)

    true
  }

  def removeRelationship(parentId: Int, childId: Int): Boolean = {
    store.deleteRelationship(parentId, childId)

    true
  }

  private def matchAllLabelTrees: List[ITree] = {
    try {
      val (rootNodes, parent2Children) =
        store.getTreesWhere("r.type=\"label\"", Map.empty)

      val trees = rootNodes.map(root => nodeToTree(root, parent2Children)).toList
      val rootLeaves = getLabelNodes.map(nodeToLeaf)

      rootLeaves.map(leaf =>
        trees.find(tree => tree.id == leaf.id) match {
          case Some(tree) => tree
          case None => leaf
        }
      )
    } catch {
      case _: RecordNotFound =>
        getLabelNodes.map(nodeToLeaf)
    }
  }

  private def getLabelNodes: List[GraphNode] =
    store.getNodesWhere("r.type=\"label\" AND NOT (r)<-[:PARENT_OF]-()", Map.empty)

  private def matchAllStatusTrees: List[ITree] = {
    try {
      val (rootNodes, parent2Children) =
        store.getTreesWhere("r.type=\"status\"", Map.empty)

      val trees = rootNodes.map(root => nodeToTree(root, parent2Children)).toList
      val rootLeaves = getStatusNodes.map(nodeToLeaf)

      rootLeaves.map(leaf =>
        trees.find(tree => tree.id == leaf.id) match {
          case Some(tree) => tree
          case None => leaf
        }
      )
    } catch {
      case _: RecordNotFound =>
        getStatusNodes.map(nodeToLeaf)
    }
  }

  private def getStatusNodes: List[GraphNode] =
    store.getNodesWhere("r.type=\"status\" AND NOT (r)<-[:PARENT_OF]-()", Map.empty)

  private def getNodeById(id: Int): GraphNode = {
    val nodes = store.getNodesWhere("Id(r)=$id", Map("id" -> new Integer(id)))

    if (nodes.isEmpty) throw RecordNotFound(s"id: $id")
    nodes.head
  }

  private def dataToMap(data: Data): Map[String, Any] =
    Map("title" -> data.title, "body" -> data.body, "type" -> data.`type`)

  private def nodeToLeaf(node: GraphNode): Leaf =
    Leaf(node.id, nodeToData(node), nodeToCreatedAt(node))

  private def nodeToCreatedAt(node: GraphNode): Long = {
    node.data.get("createdAt") match {
      case Some(t) => t.toString.toLong
      case None => throw MalformedData(node.toString)
    }
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

    val `type`: String = node.data.get("type") match {
      case Some(t) => t.toString
      case None => throw MalformedData(node.toString)
    }

    Data(title, body, `type`)
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
        nodes,
        nodeToCreatedAt(rootNode)
      )
    }
  }
}
