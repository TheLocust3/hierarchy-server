package com.jakekinsella.hierarchy_server.models.tree

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Encoder
import org.slf4j.LoggerFactory

sealed trait ITree {
  val data: Data
  val `type`: String

  def sort(): ITree
}

object ITree {
  implicit val encodeFoo: Encoder[ITree] = {
    case tree: Tree => tree.asJson
    case leaf: Leaf => leaf.asJson
  }
}

case class Tree(id: String, data: Data, nodes: List[ITree], `type`: String = "tree") extends ITree {
  def sort(): ITree = {
    Tree(id, data, nodes.map(_.sort()).sortBy(_.data.title))
  }
}

case class Leaf(id: String, data: Data, `type`: String = "leaf") extends ITree {
  def sort(): ITree = {
    this
  }
}
