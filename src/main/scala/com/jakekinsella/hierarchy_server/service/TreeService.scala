package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.finch.tree.{CreateLeaf, UpdateLeaf}
import com.jakekinsella.hierarchy_server.models.tree.{ITree, Leaf}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future
import org.slf4j.LoggerFactory

class TreeService(treeStore: TreeStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def allTreesShallow(): Future[List[Leaf]] =
    Future {
      treeStore.matchAllRootTrees()
    }

  def getTree(id: Int): Future[ITree] =
    Future {
      treeStore.matchTreeById(id).sort()
    }

  def createLeaf(createLeaf: CreateLeaf): Future[Leaf] =
    Future {
      treeStore.createLeaf(createLeaf.data, createLeaf.parentId.toInt)
    }

  def updateTree(id: String, updateLeaf: UpdateLeaf): Future[Leaf] =
    Future {
      treeStore.updateTree(id.toInt, updateLeaf.data)
    }

  def removeTree(id: String): Future[Boolean] =
    Future {
      treeStore.removeTree(id.toInt)
    }
}
