val scala3Version = "3.3.1"  // Match espresso's Scala version

val fs2KafkaVersion = "3.0.0-M4"
val catsEffectVersion = "3.4.0"  // Updated to be compatible with cats-core 2.10.0
val http4sVersion = "0.23.10"
val sttpVersion = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "compute",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    resolvers += "jitpack" at "https://jitpack.io",

    libraryDependencies ++= Seq(
      "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.typelevel" %% "cats-core" % "2.10.0",  // Explicitly declare same version as espresso
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "com.github.ryanonsrc" % "espresso" % "v1.1.4"  // Keeping this as is since it works
    )
  )