name := "leapfin-exercise"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

lazy val akkaVersion = "2.5.12"

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.4",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)