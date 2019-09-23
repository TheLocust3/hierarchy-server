package com.jakekinsella.hierarchy_server.service

import com.jakekinsella.hierarchy_server.models.user.User
import com.twitter.util.Future

class UserService {
  def getUser: Future[User] = {
    Future {
      User("jake.kinsella@gmail.com", "Jake Kinsella")
    }
  }
}
