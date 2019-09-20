package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.graph.{Node, Tree, Data}

class TreeStore(store: GraphStore) {
  def matchAllRootTrees(): List[Node] =
    store
      .getTreesWhere("NOT (r)<-[:PARENT_OF]-()", Map())
      ._1
      .map(n => toNode(n))
      .toList

  def matchTreeById(id: Int): Tree = {
    val (rootNodes, nodes, parent2Children) =
      store.getTreesWhere("Id(r)=$id", Map("id" -> id))

    toTree(rootNodes.head, nodes, parent2Children)
  }

  def createRelationship(parentId: Int, childId: Int): Node =
    toNode(store.createRelationship(parentId, childId))

  def createLeaf(data: Data, parentId: Int): Node =
    toNode(store.createNodeByParentId(parentId, dataToMap(data)))

  def createRootLeaf(data: Data): Node =
    toNode(store.createRootNode(dataToMap(data)))

  def updateTree(id: Int, data: Data): Node =
    toNode(store.updateNodeById(id, dataToMap(data)))

  def removeTree(id: Int): Boolean = {
    store.deleteTreeById(id)
    true
  }

  def removeRelationship(parentId: Int, childId: Int): Boolean = {
    store.deleteRelationship(parentId, childId)
    true
  }

  private def dataToMap(data: Data): Map[String, Any] = {
    var dataMap: Map[String, Any] = Map("title" -> data.title, "body" -> data.body, "type" -> data.`type`)

    dataMap = data.dueOn match {
      case Some(dueOn) => dataMap ++ Map("dueOn" -> dueOn)
      case None => dataMap
    }

    data.color match {
      case Some(color) => color match {
        case "" => dataMap
        case _ =>  dataMap ++ Map("color" -> color)
      }
      case None => dataMap
    }
  }

  private def toNode(node: GraphNode): Node =
    Node(node.id, toData(node), toCreatedAt(node))

  private def toCreatedAt(node: GraphNode): Long = {
    node.data.get("createdAt") match {
      case Some(t) => t.toString.toLong
      case None => throw MalformedData(node.toString)
    }
  }

  private def toData(node: GraphNode): Data = {
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

    Data(title, body, node.data.get("dueOn").map(_.toString.toLong), `type`,
      node.data.get("color").map(_.toString))
  }

  private def toTree(rootNode: GraphNode, nodes: Set[GraphNode], parent2Children: Map[GraphNode, Set[GraphNode]]): Tree = {
    Tree(toNode(rootNode),
      nodes.map(toNode),
      parent2Children.map { case (parent: GraphNode, children: Set[GraphNode]) =>
          toNode(parent) -> children.map(toNode)
      })
  }
}
