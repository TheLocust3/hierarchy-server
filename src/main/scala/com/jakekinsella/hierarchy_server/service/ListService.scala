package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.graph.{Data, Node, Tree}
import com.jakekinsella.hierarchy_server.models.list.{Card, Column, Label, Status}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future
import org.slf4j.LoggerFactory

class ListService(treeStore: TreeStore) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getList(rootId: Int): Future[List[Column]] =
    Future {
      val tree = treeStore.matchTreeById(rootId)
      val allNodes = treeStore.matchAllRootTrees()
      val leaves = tree.nodes.filter(tree.parent2Children.get(_).isEmpty).toList

      val statusTrees = allNodes.filter(_.data.`type` == "status")
      val labelTrees = allNodes.filter(_.data.`type` == "label")

      statusTrees.map(statusTree =>
        Column(
          statusTree.id,
          statusTree.data.title,
          leaves
            .filter(tree.nodeHasChild(statusTree, _))
            .map(node =>
              Card(
                node.id,
                Data(
                  node.data.title,
                  node.data.body,
                  node.data.dueOn,
                  node.data.`type`,
                  this.getNodeColor(node, tree)
                ),
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
      .filter(tree.nodeHasChild(_, node))
      .map(n => Label(n.id, n.data.title, n.createdAt, getNodeColor(n, tree)))
  }

  private def generateStatusForNode(node: Node, tree: Tree, statusNodes: List[Node]): Option[Status] = {
    statusNodes
      .find(tree.nodeHasChild(_, node))
      .map(n => Status(n.id, n.data.title, n.createdAt, getNodeColor(n, tree)))
  }

  private def getNodeColor(node: Node, tree: Tree): Option[String] = {
    node.data.color match {
      case Some(_) => node.data.color
      case None =>
        tree
          .getParents(node)
          .filter(_.data.`type` == "card")
          .toList
          .flatMap(parent => getNodeColor(parent, tree))
          .headOption
    }
  }
}
