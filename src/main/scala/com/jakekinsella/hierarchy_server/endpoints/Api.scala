package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.AppLoader
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.filter.Cors
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import org.slf4j.LoggerFactory

trait Api { self: AppLoader =>
  private val apiLogger = LoggerFactory.getLogger(this.getClass)

  private val endpoints =
    (path("api") :: path("v1") :: treeEndpoints.routes) :+:
    (path("api") :: path("v1") :: listEndpoints.routes)

  private val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some(this.config.clientAddress),
    allowsMethods = _ => Some(Seq("GET", "POST", "PATCH", "DELETE")),
    allowsHeaders = _ => Some(Seq("Accept", "Content-Type"))
  )

  lazy val api: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(endpoints.handle {
    case e: io.finch.Error =>
      apiLogger.error(e.toString)
      BadRequest(e)
    case e: io.circe.Error => BadRequest(e)
    case t: Throwable =>
      t.printStackTrace()
      InternalServerError(new Exception("Internal server error"))
  }.toServiceAs[Application.Json])
}
