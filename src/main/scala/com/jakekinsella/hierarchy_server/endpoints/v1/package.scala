package com.jakekinsella.hierarchy_server.endpoints

import io.finch._
import com.jakekinsella.hierarchy_server.models.TestResponse
import io.finch.{Endpoint, Ok}
import io.finch.syntax.get

package object v1 {
  val base = path("api") :: path("v1")

  val test: Endpoint[TestResponse] =
    get(base :: "test") { () =>
      Ok(TestResponse("ok"))
    }

  val routes = test
}
