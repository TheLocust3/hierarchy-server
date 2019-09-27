package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.user.User
import com.jakekinsella.hierarchy_server.store.UserStore
import com.twitter.util.Future

class UserService(userStore: UserStore) {
  def getUser: Future[User] = {
    Future {
      User("jake.kinsella@gmail.com", "Jake Kinsella")
    }
  }

  def changePassword(newPassword: String, newPasswordConfirmation: String): Future[User] = {
    Future {
      User("jake.kinsella@gmail.com", "Jake Kinsella")
    }
  }

  def updateUser(email: String): Future[User] = {
    Future {
      User(email, "Jake Kinsella")
    }
  }

  def signIn(email: String, password: String): Future[User] = {
    Future {
      User(email, "Jake Kinsella")
    }
  }

  def signOut: Future[Boolean] = {
    Future {
      true
    }
  }
}
