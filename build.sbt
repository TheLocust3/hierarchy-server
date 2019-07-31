name := "hierarchy-server"

version := "0.1"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.29.0",
  "com.github.finagle" %% "finch-circe" % "0.20.0",
  "io.circe" %% "circe-core" % "0.11.1",
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-parser" % "0.11.1",
  "io.circe" %% "circe-config" % "0.6.1",
  "com.twitter" %% "twitter-server" % "19.7.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.softwaremill.macwire" %% "macros" % "2.3.3"
)
