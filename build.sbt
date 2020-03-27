organization in ThisBuild := "org.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.10"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test
val postgresql ="org.postgresql"%"postgresql"%"42.2.5.jre7"

lazy val `getvis` = (project in file("."))
  .aggregate(`getvis-api`, `getvis-impl`)

val slick = Seq(
  "com.typesafe.slick" %% "slick" % "3.3.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0"
)

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
      lagomScaladslPersistenceCassandra,postgresql,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
    ) ++ slick
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`getvis-api`)



