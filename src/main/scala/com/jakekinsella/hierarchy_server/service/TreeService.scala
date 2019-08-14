package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.finch.{CreateLeaf, UpdateLeaf}
import com.jakekinsella.hierarchy_server.models.tree.Tree
import com.jakekinsella.hierarchy_server.store.{StoreError, TreeStore}
import com.twitter.util.Future
import org.slf4j.LoggerFactory

class TreeService(treeStore: TreeStore) {
  val logger = LoggerFactory.getLogger(this.getClass)

  def allTrees: Future[Either[StoreError, List[Tree]]] = {
    Future.value(Right(List(treeStore.rootTree)))
  }

  def getTree(id: Int): Future[Either[StoreError, Tree]] = {
    Future {
      Right(treeStore.matchTreeById(id))
    }
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
