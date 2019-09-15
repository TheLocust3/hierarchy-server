package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.HierarchyConfig
import org.neo4j.driver.v1.Values.{parameters, value}
import org.neo4j.driver.v1._
import org.neo4j.driver.v1.types.{Node, Relationship}

import collection.JavaConverters._

class GraphStore(config: HierarchyConfig) extends AutoCloseable {
  lazy val MAX_DEPTH = 999;

  lazy val neo4jConfig = config.neo4j
  lazy val driver = GraphDatabase.driver(neo4jConfig.address, AuthTokens.basic(neo4jConfig.username, neo4jConfig.password))

  override def close(): Unit = {
    driver.close()
  }

  def getTreesWhere(where: String, params: Map[String, Any]): (Set[GraphNode], Set[GraphNode], Map[GraphNode, Set[GraphNode]]) = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result: StatementResult = tx.run(s"MATCH (r: Tree)-[* 0..$MAX_DEPTH]->(t) WHERE $where\n" +
          "MATCH p1 = ()-[* 0..1]->(t)\n" +
          "MATCH p2 = ()-[* 0..1]->(r)\n" +
          "RETURN nodes(p1) AS cnodes, relationships(p1) AS crels, nodes(p2) AS rnode, relationships(p2) AS rrels",
          mapToParameters(params))

        val results = result.list().asScala.toList

        val rootNodes = results
          .flatMap((r: Record) => r.get("rnode")
            .asList()
            .asScala
            .map(n => GraphNode.fromNode(n.asInstanceOf[Node])))
          .toSet

        val nodes = (results
          .flatMap((r: Record) => r.get("cnodes")
            .asList()
            .asScala
            .map(n => GraphNode.fromNode(n.asInstanceOf[Node])))
          ++ rootNodes)
          .toSet

        val parent2Children: Map[GraphNode, Set[GraphNode]] =
          (results.map(_.get("rrels")) ++ results.map(_.get("crels")))
          .flatMap(_.asList(_.asRelationship()).asScala)
          .toSet
          .groupBy((p: Relationship) => p.startNodeId())
          .map { case (parentId: Long, childrenRels: Set[Relationship]) =>
            parentId -> childrenRels.map(_.endNodeId())
          }
          .map{ case (parentId: Long, childIds: Set[Long]) =>
              nodes.find(_.id == parentId.toString) match {
                case Some(parent) => parent -> childIds
                case None => throw MalformedData(s"parent id $parentId for relationship not found")
              }
          }
          .map { case (parent: GraphNode, childIds: Set[Long]) =>
              parent -> childIds.map(id =>
                nodes.find(_.id == id.toString) match {
                  case Some(child) => child
                  case None => throw MalformedData(s"child id $id for relationship not found")
                })
          }

        (rootNodes, nodes, parent2Children)
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def createRelationship(parentId: Int, childId: Int): GraphNode = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("MATCH (p:Tree) WHERE Id(p)=$parentId\n" +
          "MATCH (c:Tree) WHERE Id(c)=$childId\n" +
          "CREATE (p)-[:PARENT_OF]->(c)\n" +
          "RETURN p",
          parameters("parentId", new Integer(parentId), "childId", new Integer(childId)))

        GraphNode.fromNode(result.single().get("p").asNode())
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def createNodeByParentId(parentId: Int, data: Map[String, Any]): GraphNode = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("MATCH (p:Tree) WHERE Id(p)=$parentId\n" +
          "CREATE (r:Tree $data)\n" +
          "CREATE (p)-[:PARENT_OF]->(r)\n" +
          "RETURN r",
          parameters("parentId", new Integer(parentId), "data", (data + ("createdAt" -> System.currentTimeMillis())).asJava))

        GraphNode.fromNode(result.single().get("r").asNode())
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def createRootNode(data: Map[String, Any]): GraphNode = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("CREATE (r:Tree $data)\n" +
          "RETURN r",
          parameters("data", (data + ("createdAt" -> System.currentTimeMillis())).asJava))

        GraphNode.fromNode(result.single().get("r").asNode())
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def updateNodeById(id: Int, data: Map[String, Any]): GraphNode = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("MATCH (t:Tree) WHERE Id(t)=$id\n" +
          "WITH t, t.createdAt AS createdAt\n" +
          "SET t = $data, t.createdAt = createdAt\n" +
          "SET t.createdAt = t.createdAt\n" +
          "RETURN t",
          parameters("id", new Integer(id), "data", data.asJava))

        GraphNode.fromNode(result.single().get("t").asNode())
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def deleteTreeById(id: Int): Unit = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        tx.run("MATCH (t:Tree) WHERE Id(t)=$id\n" +
          "DETACH DELETE t",
          parameters("id", new Integer(id)))
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def deleteRelationship(parentId: Int, childId: Int): Unit = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run("MATCH (p:Tree) WHERE Id(p)=$parentId\n" +
          "MATCH (c:Tree) WHERE Id(c)=$childId\n" +
          "MATCH (p)-[r:PARENT_OF]->(c)\n" +
          "DELETE r",
          parameters("parentId", new Integer(parentId), "childId", new Integer(childId)))
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  private def mapToParameters(params: Map[String, Any]): Value = {
    value(params
      .map((parameter: (String, Any)) =>
        parameter._2 match {
          case i: Int => parameter._1 -> new Integer(i)
        }
      )
      .asJava)
  }
}
