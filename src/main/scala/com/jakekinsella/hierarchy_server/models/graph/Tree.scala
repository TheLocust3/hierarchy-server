package com.jakekinsella.hierarchy_server.models.graph

case class Tree(
  rootNode: Node,
  nodes: Set[Node],
  parent2Children: Map[Node, Set[Node]]
) extends Graph {
  def nodeHasChild(root: Node, child: Node): Boolean = {
    parent2Children
      .get(root) match {
      case Some(children) =>
        children
          .foldLeft(false)((acc: Boolean, n: Node) => n.id == child.id || nodeHasChild(n, child) || acc)
      case None => false
    }
  }

  def getParentOfType(child: Node, `type`: String): Node = {
    parent2Children.values.flatten.toSet.map((parent: Node) => {
      parent.
      parent2Children
        .get(parent) match {
        case Some(children) =>
          children.find(child)
        case None => false
      }
    })
    }
  }

  def inheritColor(child: Node, `type`: String = "card"): String = {
    child.data.color match {
      case Some(c) => c
      case None =>
    }
  }
}
