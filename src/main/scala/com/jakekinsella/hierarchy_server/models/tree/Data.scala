package com.jakekinsella.hierarchy_server.models.tree

import com.jakekinsella.hierarchy_server.store.{GraphNode, MalformedData}

case class Data(title: String, body: String)

object Data {
  def fromNode(node: GraphNode): Data = {
    val title: String = node.data.get("title") match {
      case Some(t) => t.toString
      case None => throw MalformedData(node.toString)
    }

    val body: String = node.data.get("body") match {
      case Some(t) => t.toString
      case None => throw MalformedData(node.toString)
    }

    Data(title, body)
  }
}
