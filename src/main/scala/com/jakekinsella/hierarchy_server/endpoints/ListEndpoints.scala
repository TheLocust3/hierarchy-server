package com.jakekinsella.hierarchy_server.endpoints

import com.jakekinsella.hierarchy_server.models.finch.list.OneList
import com.jakekinsella.hierarchy_server.service.ListService
import com.jakekinsella.hierarchy_server.store.RecordNotFound
import com.twitter.util.Future
import io.finch._
import io.finch.syntax._

class ListEndpoints(listService: ListService) {
  val base = path("list")

  val routes = get(base :: path[Int])(getTreeList _)

  private def getTreeList(rootId: Int): Future[Output[OneList]] =
    listService
      .getList(rootId)
      .map(list => Ok(OneList(list)))
      .handle {
        case e: RecordNotFound => NotFound(e)
        case e: Throwable => throw e
      }
}
