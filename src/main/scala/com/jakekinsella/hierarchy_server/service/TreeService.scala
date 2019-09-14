package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.finch.graph.{CreateNode, UpdateNode}
import com.jakekinsella.hierarchy_server.models.graph.{Node, Tree}
import com.jakekinsella.hierarchy_server.store.TreeStore
import com.twitter.util.Future

class TreeService(treeStore: TreeStore) {
  def allTreesShallow(): Future[List[Node]] =
    Future {
      treeStore.matchAllRootTrees()
    }

  def getTree(id: Int): Future[Tree] =
    Future {
      treeStore.matchTreeById(id)
    }

  def createRelationship(parentId: Int, childId: Int): Future[Node] =
    Future {
      treeStore.createRelationship(parentId, childId)
    }

  def createLeaf(createNode: CreateNode): Future[Node] =
    Future {
      createNode.parentId match {
        case Some(id) => treeStore.createLeaf(createNode.data, id.toInt)
        case None => treeStore.createRootLeaf(createNode.data)
      }
    }

  def updateTree(id: Int, updateNode: UpdateNode): Future[Node] =
    Future {
      treeStore.updateTree(id, updateNode.data)
    }

  def removeTree(id: Int): Future[Boolean] =
    Future {
      treeStore.removeTree(id)
    }

  def removeRelationship(parentId: Int, childId: Int): Future[Boolean] =
    Future {
      treeStore.removeRelationship(parentId, childId)
    }
}
