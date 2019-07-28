package com.jakekinsella.hierarchy_server.models

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Encoder

case class Data(title: String, body: String)

sealed trait ITree {
  val data: Data
  val `type`: String
}

case class Tree(uuid: String, data: Data, nodes: List[ITree], `type`: String = "tree") extends ITree

case class Leaf(uuid: String, data: Data, `type`: String = "leaf") extends ITree

// TEST DATA
object ITree {
  val rootTree =
    Tree("root", Data("root", "body"),
      List(
        Tree("tree-1", Data("tree 1", "body"),
          List(
            Leaf("leaf-1", Data("leaf 1", "body")),
            Leaf("leaf-2", Data("leaf 2", "body"))
          )),
        Tree("tree-2", Data("tree 2", "body"),
          List(
            Tree("tree-4", Data("tree 4", "body"),
              List(
                Leaf("leaf-3", Data("leaf 3", "body")),
                Leaf("leaf-4", Data("leaf 4", "body"))))
          )),
        Tree("tree-3", Data("tree 3", "body"),
          List(
            Leaf("leaf-5", Data("leaf 5", "body"))
          ))))

  implicit val encodeFoo: Encoder[ITree] = {
    case tree: Tree => tree.asJson
    case leaf: Leaf => leaf.asJson
  }
}
