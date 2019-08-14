package com.jakekinsella.hierarchy_server.models.tree

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.Encoder
import org.neo4j.driver.v1.types.Node

sealed trait ITree {
  val data: Data
  val `type`: String
}

object ITree {
  implicit val encodeFoo: Encoder[ITree] = {
    case tree: Tree => tree.asJson
    case leaf: Leaf => leaf.asJson
  }

  def constructTree(rootNode: Node, parent2Children: Map[Long, Set[Node]]): Tree = {
    Tree(
      rootNode.id().toString,
      Data.fromNode(rootNode),
      parent2Children
        .get(rootNode.id())
        .fold(List.empty[ITree])(_.map(node => {
          constructTree(node, parent2Children)
        }).toList)
    )
  }
}

case class Tree(id: String, data: Data, nodes: List[ITree], `type`: String = "tree") extends ITree

case class Leaf(id: String, data: Data, `type`: String = "leaf") extends ITree
