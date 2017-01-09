import sbt.Keys._
import sbt._

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

resolvers in ThisBuild +=
  "Ambitious Tools Artifactory" at "http://artifactory.ambitious.tools/artifactory/sbt-libs-release-local/"

val tmmUtilsVersion = "0.1.18"

lazy val root = Project("SenateDB", file("."))
  .enablePlugins(GitVersioning)
  .aggregate(core, webapp)

lazy val core = project.in(file("core"))
  .enablePlugins(GitVersioning)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test,it",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test,it",
      "au.id.tmm" %% "tmmtestutils" % tmmUtilsVersion % "test,it",

      "au.id.tmm" %% "tmmutils" % tmmUtilsVersion,

      "com.github.scopt" %% "scopt" % "3.4.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.3",

      "com.typesafe.akka" %% "akka-actor" % "2.3.12",
      "com.typesafe.slick" %% "slick" % "3.1.0",
      "org.xerial" % "sqlite-jdbc" % "3.8.11.2",
      "com.h2database" % "h2" % "1.4.192",
      "org.postgresql" % "postgresql" % "9.4.1209",

      "commons-io" % "commons-io" % "2.4",
      "org.apache.commons" % "commons-lang3" % "3.4"
    )
  )
  .settings(coverageExcludedPackages := "au.id.tmm.senatedb.mainclasses.*")

lazy val webapp = project.in(file("webapp"))
  .enablePlugins(GitVersioning)
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    libraryDependencies += jdbc,
    libraryDependencies += cache,
    libraryDependencies += ws,
    libraryDependencies += "org.flywaydb" %% "flyway-play" % "3.0.1"
  )
  .settings(
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc"                    % "2.5.0",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config"             % "2.5.0",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.5.1",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-jsr310"             % "2.5.0",
    libraryDependencies += "org.postgresql"  %  "postgresql"                     % "9.4.1212",
    libraryDependencies += "ch.qos.logback"  %  "logback-classic"                % "1.1.7"
  )
  .settings(
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
    libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
    libraryDependencies += "au.id.tmm" %% "tmmtestutils" % tmmUtilsVersion % "test"
  )
  .settings(
    baseDirectory in run := file("..")
  )
  .dependsOn(core % "compile->compile;test->test")
