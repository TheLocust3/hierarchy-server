package com.jakekinsella.hierarchy_server.api

import com.jakekinsella.hierarchy_server.models.{ITree, TreeResponse}
import com.twitter.util.Future
import io.finch.{Ok, Output}

object TreeApi {
  def getTree(uuid: String): Future[Output[TreeResponse]] = {
    Future.value(Ok(TreeResponse(ITree.rootTree)))
  }
}
