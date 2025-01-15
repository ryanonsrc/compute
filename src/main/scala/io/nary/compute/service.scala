package io.nary.compute

import cats.effect.*
import cats.syntax.all.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.blaze.server.*
import org.http4s.server.Router
import scala.concurrent.duration.*

object service:
  // blocking thread pool fixed at for threads, makes sense for sync. endpoints.
  private def blockingPool = Resource.make(
    IO.delay(java.util.concurrent.Executors.newFixedThreadPool(4))
  )(pool => IO.delay(pool.shutdown()))

  val http = HttpRoutes.of[IO] {
    case GET -> Root / "status" =>
      Ok("Okay.")

    case GET -> Root / "cache" / "collections" => // GET collected source data from adapters
      IO.blocking(pipeline.collectedCache.toArray.mkString("\n")).flatMap(Ok(_))

    case GET -> Root / "cache" / "processed" =>   // GET processed data (where computations have occurred)
      IO.blocking(pipeline.processedCache.toArray.mkString("\n")).flatMap(Ok(_))

    case unknown => NotFound()
  }

  def run =
    blockingPool.flatMap { pool =>
      Resource.eval(
        BlazeServerBuilder[IO]
          .bindHttp(8080, "localhost")
          .withHttpApp(http.orNotFound)
          .withIdleTimeout(60.seconds)
          .withResponseHeaderTimeout(30.seconds)
          .withExecutionContext(
            scala.concurrent.ExecutionContext.fromExecutor(pool)
          ).serve.compile.drain.as(ExitCode.Success)
      )
    }