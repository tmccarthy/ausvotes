import sbt.Keys.{libraryDependencies, _}
import sbt._

scalaVersion in ThisBuild := "2.12.2"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

resolvers in ThisBuild ++= Seq(
  Resolver.jcenterRepo,
  "Ambitious Tools Artifactory" at "http://artifactory.ambitious.tools/artifactory/sbt-libs-release-local/"
)

val applicationName = "SenateDB"

val tmmUtilsVersion = "0.2.6"
val akkaVersion = "2.5.1"

def isSnapshot(version: String) = version endsWith "-SNAPSHOT"

lazy val root = Project(applicationName, file("."))
  .enablePlugins(GitVersioning)
  .aggregate(core, api)

lazy val core = project.in(file("core"))
  .enablePlugins(GitVersioning)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.3" % "test,it",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test,it",
      "au.id.tmm" %% "tmmtestutils" % tmmUtilsVersion % "test,it",

      "au.id.tmm" %% "tmmutils" % tmmUtilsVersion,

      "com.github.scopt" %% "scopt" % "3.6.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.4",

      "com.typesafe.akka" %% "akka-actor" % akkaVersion,

      "commons-io" % "commons-io" % "2.4",
      "org.apache.commons" % "commons-lang3" % "3.4"
    )
  )

lazy val api = project.in(file("api"))
  .enablePlugins(GitVersioning)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    libraryDependencies += "com.google.inject" % "guice" % "4.1.0",
    libraryDependencies += "org.flywaydb" % "flyway-core" % "4.2.0"
  )
  .settings(
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc"                    % "2.5.1",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config"             % "2.5.1",
    libraryDependencies += "org.postgresql"  %  "postgresql"                     % "9.4.1212",
    libraryDependencies += "ch.qos.logback"  %  "logback-classic"                % "1.1.7",
    libraryDependencies += "net.codingwell"  %% "scala-guice"                    % "4.1.0"
  )
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test,it",
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test,it",
    libraryDependencies += "au.id.tmm" %% "tmmtestutils" % tmmUtilsVersion % "test,it",
    libraryDependencies += "com.whisk" %% "docker-testkit-scalatest" % "0.9.0" % "it",
    libraryDependencies += "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.0" % "it"
  )
  .settings(
    baseDirectory in run := file("..")
  )
  .settings(
    parallelExecution in IntegrationTest := false
  )
  .dependsOn(core % "compile->compile;test->test;it->it;it->test")

coverageExcludedPackages in ThisBuild := List(
  """au\.id\.tmm\.senatedb\.core\.mainclasses\..*""",
  """au\.id\.tmm\.senatedb\.api\.controllers\.javascript\..*""",
  """au\.id\.tmm\.senatedb\.api\.controllers\..*Reverse.*""",
  """au\.id\.tmm\.senatedb\.api\.controllers\.ref\..*""",
  """au\.id\.tmm\.senatedb\.router\..*""",
  """router\..*"""
).mkString(";")
