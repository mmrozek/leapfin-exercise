name := "leapfin-exercise"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4",
  "io.monix" %% "monix" % "3.0.0-RC1"
)