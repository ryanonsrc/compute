package io.nary.compute

import cats.effect.*
import fs2.Stream
import fs2.kafka.*

import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object collector:
  val topic = "collected"

  val out = KafkaProducer.pipe[IO, String, String, Unit](ProducerSettings[IO, String, String]
    .withBootstrapServers(pipeline.connectTo))

  def runCollection : IO[ExitCode] =
    fs2.Stream.awakeEvery[IO](30.seconds).evalMap(_ =>
      IO(collectData)).through[IO, ProducerResult[Unit, String, String]](out)
        .compile.drain.as(ExitCode.Success)
  
  def collectData : ProducerRecords[Unit, String, String] = 
    ProducerRecords.one(ProducerRecord(topic, "test", "foo")) 

