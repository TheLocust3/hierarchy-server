package com.jakekinsella.hierarchy_server.models.tree

import com.jakekinsella.hierarchy_server.store.MalformedData
import org.neo4j.driver.v1.types.Node

import collection.JavaConverters._

case class Data(title: String, body: String)

object Data {
  def fromNode(node: Node): Data = {
    val nodeMap = node.asMap().asScala.toMap

    val title: String = nodeMap.get("title") match {
      case Some(t) => t.toString
      case None => throw MalformedData(nodeMap.toString())
    }

    val body: String = nodeMap.get("body") match {
      case Some(t) => t.toString
      case None => throw MalformedData(nodeMap.toString())
    }

    Data(title, body)
  }
}
