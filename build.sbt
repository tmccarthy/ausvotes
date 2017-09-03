import sbt.Keys._
import sbt._

scalaVersion in ThisBuild := "2.12.2"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

resolvers in ThisBuild +=
  "Ambitious Tools Artifactory" at "http://artifactory.ambitious.tools/artifactory/sbt-libs-release-local/"

val applicationName = "SenateDB"

val tmmUtilsVersion = "0.2.4"
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
  .enablePlugins(PlayScala)
  .enablePlugins(sbtdocker.DockerPlugin)
  .disablePlugins(PlayLayoutPlugin)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    libraryDependencies += jdbc,
    libraryDependencies += cache,
    libraryDependencies += ws,
    libraryDependencies += guice,
    libraryDependencies += "org.flywaydb" %% "flyway-play" % "4.0.0"
  )
  .settings(
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc"                    % "2.5.1",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config"             % "2.5.1",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.6.0",
    libraryDependencies += "org.postgresql"  %  "postgresql"                     % "9.4.1212",
    libraryDependencies += "ch.qos.logback"  %  "logback-classic"                % "1.1.7"
  )
  .settings(
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % "test,it",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test,it",
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test,it",
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test,it",
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
  .settings(
    // Docker stuff

    docker <<= (docker dependsOn stage),

    imageNames in docker := Seq(
      ImageName(
        namespace=Some("tmccarthy"),
        repository=applicationName.toLowerCase,
        tag=Some(if (isSnapshot(version.value)) git.gitHeadCommit.value.get else version.value)
      )
    ),

    dockerfile in docker := {
      val localDistributionLocation: File = stage.value
      val containerLocation = s"/opt/$applicationName"

      new Dockerfile {
        from("openjdk:8u131-jre-alpine")
        runRaw("apk add --update bash && rm -rf /var/cache/apk/*")
        add(localDistributionLocation, containerLocation)
        entryPoint(s"$containerLocation/bin/api")
      }
    }
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
