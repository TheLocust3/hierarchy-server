package com.jakekinsella.hierarchy_server.models.tree

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Encoder

sealed trait ITree {
  val data: Data
  val `type`: String
}

object ITree {
  implicit val encodeFoo: Encoder[ITree] = {
    case tree: Tree => tree.asJson
    case leaf: Leaf => leaf.asJson
  }
}

case class Tree(uuid: String, data: Data, nodes: List[ITree], `type`: String = "tree") extends ITree

case class Leaf(uuid: String, data: Data, `type`: String = "leaf") extends ITree
