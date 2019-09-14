package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch._
import com.jakekinsella.hierarchy_server.models.finch.graph.{AdjacencyEntry, CreateNode, ListOfNodes, OneNode, OneTree, TreeResponse, UpdateNode}
import com.jakekinsella.hierarchy_server.models.graph.{Node, Tree}
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
    get(base)(getAllRootNode) :+:
    get(base :: path[Int])(getTree _) :+:
    post(base :: jsonBody[CreateNode])(createLeaf _) :+:
    patch(base :: path[Int] :: jsonBody[UpdateNode])(updateTree _) :+:
    delete(base :: path[Int])(removeTree _) :+:
    post(base :: path[Int] :: path[Int])(createRelationship _) :+:
    delete(base :: path[Int] :: path[Int])(removeRelationship _)

  private def getAllRootNode: Future[Output[ListOfNodes]] =
    treeService.allTreesShallow()
      .map(nodes => Ok(ListOfNodes(nodes)))
      .handle {
        case e: Throwable => throw e
      }

  private def getTree(id: Int): Future[Output[OneTree]] =
    treeService.getTree(id)
      .map(tree => Ok(OneTree(toTreeResponse(tree))))
      .handle {
        case e: RecordNotFound => NotFound(e)
        case e: Throwable => throw e
      }

  private def createRelationship(parentId: Int, childId: Int): Future[Output[OneNode]] =
    treeService.createRelationship(parentId, childId)
      .map(node => Ok(OneNode(node)))
      .handle {
        case e: Throwable => throw e
      }

  private def createLeaf(createNodeRequest: CreateNode): Future[Output[OneNode]] =
    treeService.createLeaf(createNodeRequest)
      .map(node => Ok(OneNode(node)))
      .handle {
        case e: Throwable => throw e
      }

  private def updateTree(id: Int, updateNodeRequest: UpdateNode): Future[Output[OneNode]] =
    treeService.updateTree(id, updateNodeRequest)
      .map(node => Ok(OneNode(node)))
      .handle {
        case e: Throwable => throw e
      }

  private def removeTree(id: Int): Future[Output[Success]] =
    treeService.removeTree(id)
      .map(_ => Ok(Success(s"tree with id: $id removed")))
      .handle {
        case e: Throwable => throw e
      }

  private def removeRelationship(parentId: Int, childId: Int): Future[Output[Success]] =
    treeService.removeRelationship(parentId, childId)
      .map(_ => Ok(Success(s"relationship with parentId: $parentId and childId: $childId removed")))
      .handle {
        case e: Throwable => throw e
      }

  private def toTreeResponse(tree: Tree): TreeResponse =
    TreeResponse(
      tree.rootNode.id,
      tree.nodes,
      tree.parent2Children
        .map { case (parent: Node, children: Set[Node]) =>
          AdjacencyEntry(parent.id, children.map(_.id))
        }
        .toSet
    )
}
