import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.nary.compute.*
import cats.effect.kernel.Resource
import cats.syntax.parallel._

object MainApp extends IOApp:
  given Logger[IO] = Slf4jLogger.getLogger[IO]
  def run(args: List[String]): IO[ExitCode] =
    (for
      _ <- Resource.eval(Logger[IO].info(s"Starting Pipeline ..."))
      results <- List(
        Resource.eval(collector.runCollection),
        Resource.eval(pipeline.processCollections),
        service.run
      ).parSequence // run the collector, process and service in parallel (separate threads)
    yield reduceExitCode(results)).use(IO.pure)

def reduceExitCode(l: List[ExitCode]): ExitCode =
  if l.forall(_ == ExitCode.Success) then
    ExitCode.Success
  else
    ExitCode.Error