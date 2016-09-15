import sbt.Keys._

name := "SenateDB"

val _ = enablePlugins(GitVersioning)

git.baseVersion := "0.1"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers +=
  "Ambitious Tools Artifactory" at "http://artifactory.ambitious.tools/artifactory/sbt-libs-release-local/"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
  "au.id.tmm" %% "tmmtestutils" % "0.1.5" % "test",

  "au.id.tmm" %% "tmmutils" % "0.1.5",

  "org.slf4j" % "slf4j-simple" % "1.7.19",
  "com.github.scopt" %% "scopt" % "3.4.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.3",

  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.xerial" % "sqlite-jdbc" % "3.8.11.2",
  "com.h2database" % "h2" % "1.4.192",
  "org.postgresql" % "postgresql" % "9.4.1209",

  "commons-io" % "commons-io" % "2.4"
)