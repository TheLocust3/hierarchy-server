package com.jakekinsella.hierarchy_server.models.tree

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Encoder

sealed trait ITree {
  val id: String
  val data: Data
  val `type`: String
  val createdAt: Long

  def sort(): ITree
  def contains(tree: ITree): Boolean
}

object ITree {
  implicit val encodeFoo: Encoder[ITree] = {
    case tree: Tree => tree.asJson
    case leaf: Leaf => leaf.asJson
  }
}

case class Tree(id: String, data: Data, nodes: List[ITree], createdAt: Long, `type`: String = "tree") extends ITree {
  def sort(): ITree = {
    Tree(id, data, nodes.map(_.sort()).sortBy(_.createdAt), createdAt)
  }

  def contains(tree: ITree): Boolean = {
    id == tree.id || nodes.foldRight(false)((tree: ITree, acc: Boolean) => tree.contains(tree) || acc)
  }
}

case class Leaf(id: String, data: Data, createdAt: Long, `type`: String = "leaf") extends ITree {
  def sort(): ITree = {
    this
  }

  def contains(tree: ITree): Boolean = {
    id == tree.id
  }
}
