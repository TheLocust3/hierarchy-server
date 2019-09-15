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
}
