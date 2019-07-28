package com.jakekinsella.hierarchy_server

import com.twitter.finagle.Http
import com.twitter.finagle.param.Stats
import com.twitter.server.TwitterServer
import com.twitter.util.Await

object Main extends TwitterServer {
  val config = AppLoader.config
  val api = AppLoader.service

  def main(): Unit = {
    val server = Http.server
      .configured(Stats(statsReceiver))
      .serve(s":${config.port}", api)

    onExit { server.close() }

    Await.ready(adminHttpServer)
  }
}
