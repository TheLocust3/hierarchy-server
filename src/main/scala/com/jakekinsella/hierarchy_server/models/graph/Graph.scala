package com.jakekinsella.hierarchy_server.models.graph

abstract class Graph {
  val nodes: Set[Node]
  val parent2Children: Map[Node, Set[Node]]
}
