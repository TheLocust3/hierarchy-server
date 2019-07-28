package com.jakekinsella.hierarchy_server.routes.v1

import com.jakekinsella.hierarchy_server.api.TreeApi
import io.finch._
import io.finch.syntax._
import com.jakekinsella.hierarchy_server.models.TreeResponse

object TreeRoutes {
  val treeBase = base :: path("tree")

  val getTree: Endpoint[TreeResponse] =
    get(treeBase :: path[String])(TreeApi.getTree _)

  val treeRoutes = getTree
}
