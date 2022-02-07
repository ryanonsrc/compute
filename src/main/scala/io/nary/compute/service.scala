package io.nary.compute

import cats.effect.*
import cats.syntax.all.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.blaze.server.*
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.global

// TODO: This is a simple service just to see items as they are collected and processed.  Ideally, we'd want
// to output in a more usable format like JSON, support filtering/pagination of results etc but once again: its a POC
object service:
  val http = HttpRoutes.of[IO] {
    case request@GET -> Root / "status" => Ok("Okay.")

    case request@GET -> Root / "cache" / "collections" =>
      Ok(pipeline.collectedCache.toArray.mkString("\n"))

    case request@GET -> Root / "cache" / "processed" =>
      Ok(pipeline.processedCache.toArray.mkString("\n"))

    case unknown => NotFound()
  }

  def run = BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(http.orNotFound)
    .withExecutionContext(global)
    .serve.compile.drain.as(ExitCode.Success)
