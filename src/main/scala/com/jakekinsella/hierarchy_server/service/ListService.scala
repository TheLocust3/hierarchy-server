package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.list.{Card, Column}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future

class ListService(treeStore: TreeStore) {
  def getList(rootId: Int): Future[List[Column]] =
    Future {
      val tree = treeStore.matchTreeById(rootId)
      val leaves = tree.nodes.filter(tree.parent2Children.get(_) == Set.empty).toList
      val statusTrees = tree.nodes.filter(_.data.`type` != "card").toList

      statusTrees.map(statusTree => Column(
        statusTree.id,
        statusTree.data.title,
        leaves
          .filter(tree.parent2Children.get(statusTree).contains)
          .map(node => Card(node.id, node.data, node.createdAt)),
        statusTree.createdAt
      ))
    }
}
