package com.jakekinsella.hierarchy_server.api

import com.jakekinsella.hierarchy_server.models._
import com.twitter.util.Future
import io.finch.{Ok, Output}

object TreeApi {
  def getAllTrees: Future[Output[ListOfTreesResponse]] = {
    Future.value(Ok(ListOfTreesResponse(List(ITree.rootTree))))
  }

  def getTree(uuid: String): Future[Output[TreeResponse]] = {
    Future.value(Ok(TreeResponse(ITree.rootTree)))
  }

  def createLeaf(createLeafRequest: CreateLeafRequest): Future[Output[TreeSuccess]] = {
    Future.value(Ok(TreeSuccess("leaf created")))
  }

  def updateLeaf(uuid: String, updateLeafRequest: UpdateLeafRequest): Future[Output[TreeSuccess]] = {
    Future.value(Ok(TreeSuccess(s"leaf with uuid: $uuid updated")))
  }

  def removeLeaf(uuid: String): Future[Output[TreeSuccess]] = {
    Future.value(Ok(TreeSuccess(s"leaf with uuid: $uuid removed")))
  }
}