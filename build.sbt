import sbt.Keys._

name := "SenateDB"

git.baseVersion in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

val countEngineProjectRoot = file("native")

lazy val root = (project in file("."))
  .enablePlugins(GitVersioning)
  .aggregate(core, native)

lazy val core = (project in file("core"))
  .dependsOn(native % Runtime)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test",
      "au.id.tmm" %% "tmmtestutils" % "0.1-232dfb0ec3e00d4ff2c6db057e3af36cb727b848" % "test",

      "au.id.tmm" %% "tmmutils" % "0.1-232dfb0ec3e00d4ff2c6db057e3af36cb727b848",

      "org.slf4j" % "slf4j-simple" % "1.7.19",
      "com.github.scopt" %% "scopt" % "3.4.0",

      "com.typesafe.akka" %% "akka-actor" % "2.3.12",
      "com.typesafe.slick" %% "slick" % "3.1.0",
      "org.xerial" % "sqlite-jdbc" % "3.8.11.2",

      "commons-io" % "commons-io" % "2.4"
    ),
    target in javah := countEngineProjectRoot / "include"
  )

lazy val native = (project in countEngineProjectRoot)
  .enablePlugins(JniNative)
  .settings(
    target in javah := countEngineProjectRoot / "include",
    sourceDirectory in nativeCompile := countEngineProjectRoot,
    target in nativeCompile := countEngineProjectRoot / "target"
  )