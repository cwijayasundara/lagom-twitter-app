organization in ThisBuild := "com.cham"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `lagom-twitter` = (project in file("."))
  .aggregate(`lagom-twitter-api`, `lagom-twitter-impl`, `lagom-twitter-stream-api`, `lagom-twitter-stream-impl`)

lazy val `lagom-twitter-api` = (project in file("lagom-twitter-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-twitter-impl` = (project in file("lagom-twitter-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-twitter-api`)

lazy val `lagom-twitter-stream-api` = (project in file("lagom-twitter-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-twitter-stream-impl` = (project in file("lagom-twitter-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`lagom-twitter-stream-api`, `lagom-twitter-api`)
