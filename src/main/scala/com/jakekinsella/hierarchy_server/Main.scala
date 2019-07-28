package com.jakekinsella.hierarchy_server

import com.twitter.finagle.Http
import com.twitter.util.Await

import io.finch._
import io.finch.circe._
import io.finch.syntax._
import io.circe.generic.auto._

case class TestResponse(success: String)

object Main extends App {
  val config = AppLoader.config

  val test: Endpoint[TestResponse] =
    get("test") { () =>
      Ok(TestResponse("ok"))
    }

  Await.ready(Http.server.serve(s":${config.port}", test.toService))
}
