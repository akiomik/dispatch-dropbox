import sbt._
import sbt.Keys._

object DispatchDropboxBuild extends Build {

  lazy val dispatchDropbox = Project(
    id = "dispatch-dropbox",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "dispatch-dropbox",
      organization := "com.github.akiomik",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.2",
      libraryDependencies ++= Seq(
        "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
        "com.ning" % "async-http-client" % "1.7.23",
        "org.specs2" %% "specs2" % "2.3.7" % "test",
        "org.scalacheck" %% "scalacheck" % "1.11.1" % "test"
      ),
      scalacOptions in Test ++= Seq("-Yrangepos")
    )
  )
}
