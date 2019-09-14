package com.jakekinsella.hierarchy_server.models.finch.graph

import com.jakekinsella.hierarchy_server.models.graph.Data

case class CreateNode(data: Data, parentId: Option[String])
