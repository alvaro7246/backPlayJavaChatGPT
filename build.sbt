name := """tutorial"""
organization := "personal"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.8"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.13"

libraryDependencies += "com.theokanning.openai-gpt3-java" % "client" % "0.9.0"

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-core" % "5.4.33.Final" // replace by your jpa implementation
)

libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.26"