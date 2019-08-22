package com.jakekinsella.hierarchy_server.store

import org.neo4j.driver.v1.types.Node

import collection.JavaConverters._

case class GraphNode(id: String, data: Map[String, Any])

object GraphNode {
  def fromNode(node: Node): GraphNode = {
    GraphNode(node.id().toString, node.asMap().asScala.toMap)
  }
}
