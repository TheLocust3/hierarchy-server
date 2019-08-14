package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch._
import com.jakekinsella.hierarchy_server.service.TreeService
import com.twitter.util.Future
import io.finch._
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._

class TreeEndpoints(treeService: TreeService) {
  val base = path("tree")

  val routes =
    get(base)(getAllTrees) :+:
    get(base :: path[Int])(getTree _) :+:
    post(base :: jsonBody[CreateLeaf])(createLeaf _) :+:
    patch(base :: path[String] :: jsonBody[UpdateLeaf])(updateTree _) :+:
    delete(base :: path[String])(removeTree _)

  private def getAllTrees: Future[Output[ListOfTrees]] =
    treeService.allTrees.map {
      case Left(_) => NotFound(new Exception("yikes"))
      case Right(trees) => Ok(ListOfTrees(trees))
    }

  private def getTree(id: Int): Future[Output[OneTree]] =
    treeService.getTree(id).map {
      case Left(_) => NotFound(new Exception("tree not found"))
      case Right(tree) => Ok(OneTree(tree))
    }

  private def createLeaf(createLeafRequest: CreateLeaf): Future[Output[Success]] =
    treeService.createLeaf(createLeafRequest).map {
      case Left(_) => BadRequest(new Exception("failed to create leaf")) // TODO: be more specific on error
      case Right(_) => Ok(Success("leaf created"))
    }

  private def updateTree(id: String, updateLeafRequest: UpdateLeaf): Future[Output[Success]] =
    treeService.updateTree(id, updateLeafRequest).map {
      case Left(_) => BadRequest(new Exception(s"failed to update tree with id: $id")) // TODO: be more specific on error
      case Right(_) => Ok(Success(s"tree with id: $id updated"))
    }

  private def removeTree(id: String): Future[Output[Success]] =
    treeService.removeTree(id).map {
      case Left(_) => BadRequest(new Exception(s"failed to remove tree with id: $id")) // TODO: be more specific on error
      case Right(_) => Ok(Success(s"tree with id: $id removed"))
    }
}
