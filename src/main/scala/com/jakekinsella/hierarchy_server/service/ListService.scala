package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.graph.{Node, Tree}
import com.jakekinsella.hierarchy_server.models.list.{Card, Column, Label, Status}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future

class ListService(treeStore: TreeStore) {
  def getList(rootId: Int): Future[List[Column]] =
    Future {
      val tree = treeStore.matchTreeById(rootId)
      val leaves = tree.nodes.filter(tree.parent2Children.get(_) == Set.empty).toList

      val statusTrees = tree.nodes.filter(_.data.`type` == "status").toList
      val labelTrees = tree.nodes.filter(_.data.`type` == "label").toList

      (statusTrees ++ labelTrees).map(statusTree =>
        Column(
          statusTree.id,
          statusTree.data.title,
          leaves
            .filter(tree.parent2Children.get(statusTree).contains)
            .map(node =>
              Card(
                node.id,
                node.data,
                node.createdAt,
                generateLabelsForNode(node, tree, labelTrees),
                generateStatusForNode(node, tree, statusTrees)
              )
            ),
          statusTree.createdAt
        )
      )
    }

  private def generateLabelsForNode(node: Node, tree: Tree, labelNodes: List[Node]): List[Label] = {
    labelNodes
      .filter(labelTree => tree.parent2Children.get(labelTree) match {
        case Some(_) => true
        case None => false
      })
      .map(node => Label(node.id, node.data.title, node.createdAt))
  }

  private def generateStatusForNode(node: Node, tree: Tree, statusNodes: List[Node]): Option[Status] = {
    statusNodes
      .find(status => tree.parent2Children.get(status) match {
        case Some(_) => true
        case None => false
      })
      .map(node => Status(node.id, node.data.title, node.createdAt))
  }
}
