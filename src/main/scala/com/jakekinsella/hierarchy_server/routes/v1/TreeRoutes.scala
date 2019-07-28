package com.jakekinsella.hierarchy_server.routes.v1

import com.jakekinsella.hierarchy_server.api.TreeApi
import io.finch._
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._
import com.jakekinsella.hierarchy_server.models._

object TreeRoutes {
  val treeBase = base :: path("tree")

  val getAllTrees: Endpoint[ListOfTreesResponse] =
    get(treeBase)(TreeApi.getAllTrees _)

  val getTree: Endpoint[TreeResponse] =
    get(treeBase :: path[String])(TreeApi.getTree _)

  val createLeaf: Endpoint[TreeSuccess] =
    post(treeBase :: jsonBody[CreateLeafRequest])(TreeApi.createLeaf _)

  val updateTree: Endpoint[TreeSuccess] =
    put(treeBase :: path[String] :: jsonBody[UpdateLeafRequest])(TreeApi.updateTree _)

  val removeTree: Endpoint[TreeSuccess] =
    delete(treeBase :: path[String])(TreeApi.removeTree _)

  val treeRoutes = getAllTrees :+: getTree :+: createLeaf :+: updateTree :+: removeTree
}
