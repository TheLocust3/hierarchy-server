package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.tree.{Data, ITree, Leaf, Tree}
import org.neo4j.driver.v1.{Record, Session, Transaction}
import org.neo4j.driver.v1.Values.parameters
import org.neo4j.driver.v1.types.{Node, Relationship}
import org.slf4j.LoggerFactory

import collection.JavaConverters._

class TreeStore(driver: GraphDriver) {
  val logger = LoggerFactory.getLogger(this.getClass)

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

  def matchTreeById(id: Int): Tree = {
    try {
      val session: Session = driver.driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("MATCH (r: Tree)-[p *]->(t) WHERE Id(r)=$id RETURN r, t, p",
          parameters("id", new Integer(id)))

        val results = result.list().asScala.toList

        val rootNode: Node = results.head.get("r").asNode()
        val nodes: List[Node] = results.map(_.get("t").asNode())
        val parent2Children: Map[Long, Set[Node]] = results
          .map(_.get("p"))
          .flatMap(_.asList(_.asRelationship()).asScala)
          .toSet
          .groupBy((p: Relationship) => p.startNodeId())
          .map { case (l: Long, relationships: Set[Relationship]) => l -> relationships.map(_.endNodeId()) }
          .map { case (l: Long, relationships: Set[Long]) => l -> relationships.flatMap(r => nodes.find(p => p.id() == r)) }

        ITree.constructTree(rootNode, parent2Children)
      })
    } catch {
      case t: Throwable => throw t
    }
  }
}