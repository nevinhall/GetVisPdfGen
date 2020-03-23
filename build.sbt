organization in ThisBuild := "org.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.10"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

lazy val `getvis` = (project in file("."))
  .aggregate(`getvis-api`, `getvis-impl`)

lazy val `getvis-api` = (project in file("getvis-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )


lazy val `getvis-impl` = (project in file("getvis-impl"))
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
  .settings(lagomForkedTestSettings)
  .dependsOn(`getvis-api`)


