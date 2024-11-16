val scala3Version = "3.1.1"

val fs2KafkaVersion = "3.0.0-M4"
val catsEffectVersion = "3.3.5"
val http4sVersion = "0.23.10"
val sttpVersion = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "compute",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion
    )
  )
