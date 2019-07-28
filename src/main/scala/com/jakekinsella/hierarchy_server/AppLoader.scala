package com.jakekinsella.hierarchy_server

import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.config.syntax._

object AppLoader {
  lazy val config: HierarchyConfig = ConfigFactory.load().as[HierarchyConfig]("hierarchy").fold(throw _, identity)
}
