package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.finch.{CreateLeaf, UpdateLeaf}
import com.jakekinsella.hierarchy_server.models.tree.{ITree, Leaf}
import com.jakekinsella.hierarchy_server.store.{StoreError, TreeStore}
import com.twitter.util.Future
import org.slf4j.LoggerFactory

class TreeService(treeStore: TreeStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  // TODO: CATCH ERRORS AND PUT THEM IN STOREERROR
  def allTreesShallow(): Future[Either[StoreError, List[Leaf]]] = {
    Future.value(Right(treeStore.matchAllRootTrees()))
  }

  def getTree(id: Int): Future[Either[StoreError, ITree]] = {
    Future {
      Right(treeStore.matchTreeById(id))
    }
  }

  def createLeaf(createLeaf: CreateLeaf): Future[Either[StoreError, Leaf]] = {
    Future {
      Right(treeStore.createLeaf(createLeaf.data, createLeaf.parentId.toInt))
    }
  }

  def updateTree(id: String, updateLeaf: UpdateLeaf): Future[Either[StoreError, Leaf]] = {
    Future {
      Right(treeStore.updateTree(id.toInt, updateLeaf.data))
    }
  }

  def removeTree(id: String): Future[Either[StoreError, Boolean]] = {
    Future {
      Right(treeStore.removeTree(id.toInt))
    }
  }
}
