import cats.effect.{ExitCode, IO}
import io.nary.compute.*
import cats.effect.unsafe.implicits.*

// TODO: This is only to be intended as a simple pipeline executor for demonstration purposes
// in a production deployment we would want to use IO.unsafeRunAsync() for better safety (in addition to adding
// some error handling/logging, better scaling when handling large numbers of adapters, etc.
@main def main: Unit = IO.parSequenceN(3)(
  collector.runCollection ::
  pipeline.processCollections ::
  service.run :: Nil
).map(reduceExitCode).unsafeRunSync()

def reduceExitCode(l: List[ExitCode]) : ExitCode =
  l.forall(_ == ExitCode.Success) match
    case true => ExitCode.Success
    case false => ExitCode.Error