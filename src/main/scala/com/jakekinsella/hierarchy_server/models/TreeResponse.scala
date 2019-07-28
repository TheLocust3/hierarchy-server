package com.jakekinsella.hierarchy_server.models

case class TreeResponse(tree: Tree)

case class ListOfTreesResponse(trees: List[Tree])

case class TreeSuccess(success: String)
