package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch.user.OneUser
import com.jakekinsella.hierarchy_server.service.UserService
import com.jakekinsella.hierarchy_server.store.RecordNotFound
import com.twitter.util.Future
import io.finch._
import io.finch.syntax._

class UserEndpoints(userService: UserService) {
  val base = path("user")

  val routes = get(base)(getUser _)

  private def getUser: Future[Output[OneUser]] =
    userService
      .getUser
      .map(user => Ok(OneUser(user)))
      .handle {
        case e: RecordNotFound => Unauthorized(e)
        case e: Throwable => throw e
      }
}
