package com.jakekinsella.hierarchy_server.models

case class HierarchyConfig(
  port: Int,
  clientAddress: String,
  neo4j: Neo4jConfig)

case class Neo4jConfig(
  address: String,
  username: String,
  password: String)
