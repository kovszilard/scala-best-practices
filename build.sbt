import Dependencies._

enablePlugins(TutPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    scalacOptions ++= Seq(
      //"-Xfatal-warnings"
    ),
    name := "scala-best-practices",
    libraryDependencies += scalaTest % Test
  )
