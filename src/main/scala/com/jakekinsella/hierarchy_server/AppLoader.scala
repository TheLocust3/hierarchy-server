package com.jakekinsella.hierarchy_server

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.config.syntax._
import io.finch.circe._
import io.finch.{Application, BadRequest, InternalServerError}

object AppLoader {
  lazy val config: HierarchyConfig = ConfigFactory.load().as[HierarchyConfig]("hierarchy").fold(throw _, identity)

  lazy val service: Service[Request, Response] = api.v1.routes.handle {
    case e: io.finch.Error => BadRequest(e)
    case e: io.circe.Error => BadRequest(e)
    case t: Throwable => InternalServerError(new Exception(t.getCause))
  }.toServiceAs[Application.Json]
}
