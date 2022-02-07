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
    fs2.Stream.awakeEvery[IO](3.seconds).flatMap(_ => collectAllData)
      .through[IO, ProducerResult[Unit, String, String]](out).compile.drain.as(ExitCode.Success)

  def collectAllData : Stream[IO, ProducerRecords[Unit, String, String]] =
    Stream.evalSeq[IO, List, ProducerRecords[Unit, String, String]](
      adapters.readAll.map(_.map { case (k, v) => ProducerRecords.one(ProducerRecord(topic, k, v))}))