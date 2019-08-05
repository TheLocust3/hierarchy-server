package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.AppLoader
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.filter.Cors
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

trait Api { self: AppLoader =>
  private val endpoints = path("api") :: path("v1") :: treeEndpoints.routes

  private val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some(this.config.clientAddress),
    allowsMethods = _ => Some(Seq("GET", "POST", "PATCH", "DELETE")),
    allowsHeaders = _ => Some(Seq("Accept", "Content-Type"))
  )

  lazy val api: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(endpoints.handle {
    case e: io.finch.Error => BadRequest(e)
    case e: io.circe.Error => BadRequest(e)
    case t: Throwable => InternalServerError(new Exception(t.getCause))
  }.toServiceAs[Application.Json])
}
