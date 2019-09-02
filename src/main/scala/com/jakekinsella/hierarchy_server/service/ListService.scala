package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.list.{Card, Column}
import com.jakekinsella.hierarchy_server.models.tree.{ITree, Leaf, Tree}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future

class ListService(treeStore: TreeStore) {

  def getList(rootId: Int): Future[List[Column]] =
    Future {
      // test column for now
      List(
        Column(
          "id",
          "none",
          getLeaves(treeStore.matchTreeById(rootId).sort()).map(leaf => Card(leaf.id, leaf.data))))
    }

  private def getLeaves(tree: ITree): List[Leaf] =
    tree match {
      case l: Leaf => List(l)
      case t: Tree => t.nodes.flatMap(getLeaves)
    }
}
