package com.jakekinsella.hierarchy_server.api

import io.finch._
import com.jakekinsella.hierarchy_server.models.TestResponse
import io.finch.{Endpoint, Ok}
import io.finch.syntax.get

package object v1 {
  val test: Endpoint[TestResponse] =
    get("test") { () =>
      Ok(TestResponse("ok"))
    }

  val routes = "api" :: "v1" :: test
}
