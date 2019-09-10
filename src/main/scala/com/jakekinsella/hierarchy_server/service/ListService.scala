package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.list.{Card, Column}
import com.jakekinsella.hierarchy_server.models.tree.{ITree, Leaf, Tree}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future

class ListService(treeStore: TreeStore) {

  def getList(rootId: Int): Future[List[Column]] =
    Future {
      val leaves = getLeaves(treeStore.matchTreeById(rootId).sort())
      val statusTrees = treeStore.matchAllSpecialTrees.filter(tree => tree.data.`type` == "status")

      statusTrees.map(statusTree => Column(
        statusTree.id,
        statusTree.data.title,
        leaves
          .filter(statusTree.contains)
          .map(leaf => Card(leaf.id, leaf.data))))
    }

  private def getLeaves(tree: ITree): List[Leaf] =
    tree match {
      case l: Leaf => List(l)
      case t: Tree => t.nodes.flatMap(getLeaves)
    }
}
