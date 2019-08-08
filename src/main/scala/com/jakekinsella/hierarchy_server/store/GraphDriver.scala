package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.HierarchyConfig
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

class GraphDriver(config: HierarchyConfig) extends AutoCloseable {
  lazy val neo4jConfig = config.neo4j

  lazy val driver = GraphDatabase.driver(neo4jConfig.address, AuthTokens.basic(neo4jConfig.username, neo4jConfig.password))

  override def close(): Unit = {
    driver.close()
  }
}
