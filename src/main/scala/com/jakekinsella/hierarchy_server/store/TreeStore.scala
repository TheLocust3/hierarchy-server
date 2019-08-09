package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, Leaf, Tree}

class TreeStore(driver: GraphDriver) {
  val rootTree =
    Tree("root", Data("root", "body"),
      List(
        Tree("tree-1", Data("tree 1", "body"),
          List(
            Leaf("leaf-1", Data("leaf 1", "body")),
            Leaf("leaf-2", Data("leaf 2", "body"))
          )),
        Tree("tree-2", Data("tree 2", "body"),
          List(
            Tree("tree-4", Data("tree 4", "body"),
              List(
                Leaf("leaf-3", Data("leaf 3", "body")),
                Leaf("leaf-4", Data("leaf 4", "body"))))
          )),
        Tree("tree-3", Data("tree 3", "body"),
          List(
            Leaf("leaf-5", Data("leaf 5", "body"))
          ))))

  // everything
  // MATCH (r: Tree)-[*]->(t) RETURN r, t

  // by id=237
  // MATCH (r: Tree)-[*]->(t) WHERE Id(r)=237 RETURN r, t
}
