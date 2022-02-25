package io.nary.compute

import cats.effect.*
import cats.syntax.all.*
import io.nary.compute.proto.processed.{ManyProcessed, Processed}
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.blaze.server.*
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.global

object service:
  val http = HttpRoutes.of[IO] {
    case request@GET -> Root / "status" => Ok("Okay.")

    case request@GET -> Root / "cache" / "collections" / "json" =>
      Ok(pipeline.collectedCache.toArray.mkString("\n"))

    case request@GET -> Root / "cache" / "processed" / "json" =>
      Ok(pipeline.processedCache.toArray.mkString("\n"))

    case request@GET -> Root / "cache" / "processed" / "msgs" =>
      Ok(processedMessages)

    case unknown => NotFound()
  }

  def run = BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(http.orNotFound)
    .withExecutionContext(global)
    .serve.compile.drain.as(ExitCode.Success)

  def processedMessages : Array[Byte] =
    ManyProcessed(pipeline.processedCache.toArray.toSeq.map {
      case (k, v) => Processed(k.toString, v.toString)}).toByteArray