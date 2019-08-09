package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.finch.{CreateLeaf, UpdateLeaf}
import com.jakekinsella.hierarchy_server.models.tree.Tree
import com.jakekinsella.hierarchy_server.store.{StoreError, TreeStore}
import com.twitter.util.Future

class TreeService(treeStore: TreeStore) {
  def allTrees: Future[Either[StoreError, List[Tree]]] = {
    Future.value(Right(List(treeStore.rootTree)))
  }

  def getTree(id: String): Future[Either[StoreError, Tree]] = {
    Future.value(Right(treeStore.rootTree))
  }

  def createLeaf(createLeaf: CreateLeaf): Future[Either[StoreError, Boolean]] = {
    Future.value(Right(true))
  }

  def updateTree(id: String, updateLeaf: UpdateLeaf): Future[Either[StoreError, Boolean]] = {
    Future.value(Right(true))
  }

  def removeTree(id: String): Future[Either[StoreError, Boolean]] = {
    Future.value(Right(true))
  }
}
