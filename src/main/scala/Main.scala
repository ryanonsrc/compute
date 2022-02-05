import io.nary.compute.*
import cats.effect.unsafe.implicits._

// TODO: This is only to be intended as a simple pipeline executor for demonstration purposes
// in a production deployment we would want to use IO.unsafeRunAsync() for better safety
@main def runPipeline: Unit = pipeline.run.unsafeRunSync() 
