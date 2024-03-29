package com.jakekinsella.hierarchy_server

import com.jakekinsella.hierarchy_server.endpoints.{ListEndpoints, TreeEndpoints}
import com.jakekinsella.hierarchy_server.models.HierarchyConfig
import com.jakekinsella.hierarchy_server.service.{ListService, TreeService}
import com.jakekinsella.hierarchy_server.store.{GraphStore, TreeStore}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.config.syntax._
import com.softwaremill.macwire._

trait AppLoader {
  lazy val config: HierarchyConfig = ConfigFactory.load().as[HierarchyConfig]("hierarchy").fold(throw _, identity)

  // endpoints
  lazy val treeEndpoints = wire[TreeEndpoints]
  lazy val listEndpoints = wire[ListEndpoints]

  // services
  lazy val treeService = wire[TreeService]
  lazy val listService = wire[ListService]

  // stores
  lazy val treeStore = wire[TreeStore]

  // db drivers
  lazy val graphDriver = wire[GraphStore]
}
