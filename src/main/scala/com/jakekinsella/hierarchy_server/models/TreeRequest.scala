package com.jakekinsella.hierarchy_server.models

case class CreateLeafRequest(data: Data, parentUuid: String)

case class UpdateLeafRequest(data: Data)
