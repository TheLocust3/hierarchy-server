package com.jakekinsella.hierarchy_server.models.finch.tree

import com.jakekinsella.hierarchy_server.models.tree.Data

case class CreateLeaf(data: Data, parentId: Option[String])
