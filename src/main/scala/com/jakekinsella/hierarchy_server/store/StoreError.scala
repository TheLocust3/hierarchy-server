package com.jakekinsella.hierarchy_server.store

sealed abstract class StoreError(val message: String) extends Exception

case class MalformedData(received: String) extends StoreError(s"Received malformed data from db: $received")
