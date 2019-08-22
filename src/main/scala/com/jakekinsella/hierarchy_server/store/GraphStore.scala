package com.jakekinsella.hierarchy_server.store

import com.jakekinsella.hierarchy_server.models.HierarchyConfig
import org.neo4j.driver.v1.Values.{value, parameters}
import org.neo4j.driver.v1._
import org.neo4j.driver.v1.types.Relationship

import collection.JavaConverters._

class GraphStore(config: HierarchyConfig) extends AutoCloseable {
  lazy val neo4jConfig = config.neo4j

  lazy val driver = GraphDatabase.driver(neo4jConfig.address, AuthTokens.basic(neo4jConfig.username, neo4jConfig.password))

  override def close(): Unit = {
    driver.close()
  }

  def getTreesWhere(where: String, params: Map[String, Any]): (Set[GraphNode], Map[GraphNode, Set[GraphNode]]) = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run(s"MATCH (r: Tree)-[p *]->(t) WHERE $where RETURN r, t, p",
          mapToParameters(params))

        if (!result.hasNext) throw RecordNotFound(s"statement: $where, parameters; ${params.toString}")

        val results = result.list().asScala.toList

        val rootNodes = results.map((r: Record) => GraphNode.fromNode(r.get("r").asNode())).toSet
        val nodes: Set[GraphNode] = (results.map((r: Record) => GraphNode.fromNode(r.get("t").asNode())) ++ rootNodes).toSet

        val parent2Children: Map[GraphNode, Set[GraphNode]] = results
          .map(_.get("p"))
          .flatMap(_.asList(_.asRelationship()).asScala)
          .toSet
          .groupBy((p: Relationship) => p.startNodeId())
          .map { case (l: Long, relationships: Set[Relationship]) =>
            nodes.find(n => n.id == l.toString).get -> relationships.map(_.endNodeId())
          }
          .map { case (n: GraphNode, relationships: Set[Long]) =>
            n -> relationships.flatMap(r => nodes.find(n => n.id == r.toString)) }

        (rootNodes, parent2Children)
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def getNodesWhere(where: String, params: Map[String, Any]): List[GraphNode] = {
    try {
      val session: Session = driver.session()

      session.writeTransaction((tx: Transaction) => {
        val result = tx.run(s"MATCH (r: Tree) WHERE $where RETURN r", mapToParameters(params))

        val results = result.list().asScala.toList
        results.map(r => GraphNode.fromNode(r.get("r").asNode()))
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
          parameters("parentId", new Integer(parentId), "data", data.asJava))

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
          "SET t = $data\n" +
          "RETURN t",
          parameters("id", new Integer(id), "data", data.asJava))

        GraphNode.fromNode(result.single().get("t").asNode())
      })
    } catch {
      case t: Throwable => throw t
    }
  }

  def deleteTreeById(id: Int) = {
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
