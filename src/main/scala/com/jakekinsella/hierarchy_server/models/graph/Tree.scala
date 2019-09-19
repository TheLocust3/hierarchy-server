package com.jakekinsella.hierarchy_server.models.graph

import org.slf4j.LoggerFactory

case class Tree(
  rootNode: Node,
  nodes: Set[Node],
  parent2Children: Map[Node, Set[Node]]
) extends Graph {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def nodeHasChild(root: Node, child: Node): Boolean = {
    parent2Children
      .get(root) match {
      case Some(children) =>
        children
          .foldLeft(false)((acc: Boolean, n: Node) => n.id == child.id || nodeHasChild(n, child) || acc)
      case None => false
    }
  }

  def getParents(node: Node): Set[Node] = {
    parent2Children
      .filter { case (_, children) => children.contains(node) }
      .keys
      .toSet
  }
}
