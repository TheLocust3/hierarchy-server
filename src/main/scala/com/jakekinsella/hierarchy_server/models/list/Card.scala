package com.jakekinsella.hierarchy_server.models.list

import com.jakekinsella.hierarchy_server.models.graph.Data

case class Card(
  id: String,
  data: Data,
  createdAt: Long,
  labels: List[Label],
  status: Option[Status]
)
