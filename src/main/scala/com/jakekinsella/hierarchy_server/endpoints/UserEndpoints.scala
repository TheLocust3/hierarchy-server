package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch.Success
import com.jakekinsella.hierarchy_server.models.finch.user.{ChangePassword, OneUser, SignIn, UpdateUser}
import com.jakekinsella.hierarchy_server.service.UserService
import com.jakekinsella.hierarchy_server.store.RecordNotFound
import com.twitter.util.Future
import io.finch._
import io.finch.syntax._
import io.finch.circe._
import io.circe.generic.auto._

class UserEndpoints(userService: UserService) {
  val base = path("user")

  val routes =
    get(base)(getUser _) :+:
    patch(base :: "password" :: jsonBody[ChangePassword])(changePassword _) :+:
    patch(base :: jsonBody[UpdateUser])(updateUser _) :+:
    post(base :: "sign_in" :: jsonBody[SignIn])(signIn _) :+:
    delete(base :: "sign_out")(signOut _)

  private def getUser: Future[Output[OneUser]] =
    userService
      .getUser
      .map(user => Ok(OneUser(user)))
      .handle {
        case e: RecordNotFound => Unauthorized(e)
        case e: Throwable => throw e
      }

  private def changePassword(changePassword: ChangePassword): Future[Output[OneUser]] =
    userService
      .changePassword(changePassword.newPassword, changePassword.newPasswordConfirmation)
      .map(user => Ok(OneUser(user)))
      .handle {
        case e: RecordNotFound => Unauthorized(e)
        case e: Throwable => throw e
      }

  private def updateUser(updateUser: UpdateUser): Future[Output[OneUser]] =
    userService
      .updateUser(updateUser.email)
      .map(user => Ok(OneUser(user)))
      .handle {
        case e: RecordNotFound => Unauthorized(e)
        case e: Throwable => throw e
      }

  private def signIn(signIn: SignIn): Future[Output[OneUser]] =
    userService
      .signIn(signIn.email, signIn.password)
      .map(user => Ok(OneUser(user)))
      .handle {
        case e: RecordNotFound => Unauthorized(e)
        case e: Throwable => throw e
      }

  private def signOut(): Future[Output[Success]] =
    userService
      .signOut
      .map(_ => Ok(Success(s"signed out")))
      .handle {
        case e: Throwable => throw e
      }
}
