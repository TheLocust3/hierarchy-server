package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, ITree, Leaf, Tree}

class TreeStore(store: GraphStore) {
  def matchAllRootTrees(): List[Leaf] =
    store
      .getTreesWhere("NOT (r)<-[:PARENT_OF]-()", Map())
      ._1
      .map(n => nodeToLeaf(n))
      .toList

  def matchAllSpecialTrees: List[ITree] = {
    matchAllLabelTrees ++ matchAllStatusTrees
  }

  def matchTreeById(id: Int): ITree = {
    val (rootNodes, parent2Children) =
      store.getTreesWhere("Id(r)=$id", Map("id" -> id))

    nodeToTree(rootNodes.head, parent2Children)
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
    val (rootNodes, parent2Children) =
      store.getTreesWhere("r.type=\"label\"", Map.empty)

    rootNodes.map(root => nodeToTree(root, parent2Children)).toList
  }

  private def matchAllStatusTrees: List[ITree] = {
    val (rootNodes, parent2Children) =
      store.getTreesWhere("r.type=\"status\"", Map.empty)

    rootNodes.map(root => nodeToTree(root, parent2Children)).toList
  }

  private def dataToMap(data: Data): Map[String, Any] = {
    val dataMap = Map("title" -> data.title, "body" -> data.body, "type" -> data.`type`)

    data.dueOn match {
      case Some(dueOn) => dataMap ++ Map("dueOn" -> dueOn)
      case None => dataMap
    }
  }

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

    Data(title, body, node.data.get("dueOn").map(_.toString.toLong), `type`)
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
