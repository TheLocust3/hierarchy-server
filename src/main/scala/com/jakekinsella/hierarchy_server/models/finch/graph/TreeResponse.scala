package com.jakekinsella.hierarchy_server.models.finch.graph

import com.jakekinsella.hierarchy_server.models.graph.Node

case class AdjacencyEntry(parentId: String, childIds: Set[String])

case class TreeResponse(rootNodeId: String, nodes: Set[Node], adjacencyList: Set[AdjacencyEntry])
