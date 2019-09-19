package com.jakekinsella.hierarchy_server.models.graph

case class Data(title: String, body: String, dueOn: Option[Long], `type`: String, color: Option[String])
