package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch._
import com.jakekinsella.hierarchy_server.models.finch.tree._
import com.jakekinsella.hierarchy_server.service.TreeService
import com.jakekinsella.hierarchy_server.store.RecordNotFound
import com.twitter.util.Future
import io.finch._
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._

class TreeEndpoints(treeService: TreeService) {
  val base = path("tree")

  val routes =
    get(base)(getAllTreesShallow) :+:
    get(base :: "labels")(getAllLabelTrees) :+:
    get(base :: path[Int])(getTree _) :+:
    post(base :: path[Int] :: path[Int])(createRelationship _) :+:
    post(base :: jsonBody[CreateLeaf])(createLeaf _) :+:
    patch(base :: path[String] :: jsonBody[UpdateLeaf])(updateTree _) :+:
    delete(base :: path[String])(removeTree _)

  private def getAllTreesShallow: Future[Output[ListOfTrees]] =
    treeService.allTreesShallow()
      .map(trees => Ok(ListOfTrees(trees)))
      .handle {
        case e: Throwable => throw e
      }

  private def getAllLabelTrees: Future[Output[ListOfTrees]] =
    treeService.allLabelTrees()
      .map(trees => Ok(ListOfTrees(trees)))
      .handle {
        case e: Throwable => throw e
      }

  private def getTree(id: Int): Future[Output[OneTree]] =
    treeService.getTree(id)
      .map(tree => Ok(OneTree(tree)))
      .handle {
        case e: RecordNotFound => NotFound(e)
        case e: Throwable => throw e
      }

  private def createRelationship(parentId: Int, childId: Int): Future[Output[OneTree]] =
    treeService.createRelationship(parentId, childId)
      .map(tree => Ok(OneTree(tree)))
      .handle {
        case e: Throwable => throw e
      }

  private def createLeaf(createLeafRequest: CreateLeaf): Future[Output[OneTree]] =
    treeService.createLeaf(createLeafRequest)
      .map(leaf => Ok(OneTree(leaf)))
      .handle {
        case e: Throwable => throw e
      }

  private def updateTree(id: String, updateLeafRequest: UpdateLeaf): Future[Output[OneTree]] =
    treeService.updateTree(id, updateLeafRequest)
      .map(leaf => Ok(OneTree(leaf)))
      .handle {
        case e: Throwable => throw e
      }

  private def removeTree(id: String): Future[Output[Success]] =
    treeService.removeTree(id)
      .map(_ => Ok(Success(s"tree with id: $id removed")))
      .handle {
        case e: Throwable => throw e
      }
}
