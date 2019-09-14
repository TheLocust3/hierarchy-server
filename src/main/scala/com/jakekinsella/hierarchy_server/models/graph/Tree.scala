package com.jakekinsella.hierarchy_server.models.graph

case class Tree(
  rootNode: Node,
  nodes: Set[Node],
  parent2Children: Map[Node, Set[Node]]
) extends Graph;
