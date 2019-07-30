package com.jakekinsella.hierarchy_server

import com.twitter.finagle.Service
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.{Request, Response}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.config.syntax._
import io.finch.circe._
import io.finch.{Application, BadRequest, InternalServerError}

object AppLoader {
  lazy val config: HierarchyConfig = ConfigFactory.load().as[HierarchyConfig]("hierarchy").fold(throw _, identity)

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"), // TODO: will want to restrict this in the future
    allowsMethods = _ => Some(Seq("GET", "POST", "PUT", "DELETE")),
    allowsHeaders = _ => Some(Seq("Accept"))
  )

  lazy val api: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(routes.v1.routes.handle {
    case e: io.finch.Error => BadRequest(e)
    case e: io.circe.Error => BadRequest(e)
    case t: Throwable => InternalServerError(new Exception(t.getCause))
  }.toServiceAs[Application.Json])
}
