package com.jakekinsella.hierarchy_server.models.list

case class Column(id: String, name: String, cards: List[Card], createdAt: Long)
