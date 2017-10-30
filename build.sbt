name := "fort"
organization := "com.oschrenk"
version := "0.1.0"

scalaVersion := "2.11.11"

// for janstenpickle.vault
resolvers += Resolver.bintrayRepo("janstenpickle", "maven")
// for transitive dependencies uscala:* of janstenpickle.vault
resolvers +=  Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.typesafe" % "config" % "1.3.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "janstenpickle.vault" %% "vault-core" % "0.4.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    //"-Ywarn-unused-import",
    "-Ywarn-nullary-unit",
    "-Xfatal-warnings",
    "-Xlint",
    //"-Yinline-warnings",
    "-Ywarn-dead-code",
    "-Xfuture")

initialCommands := "import com.oschrenk.fort._"

