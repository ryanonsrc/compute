package io.nary.compute

import cats.effect.*
import fs2.Stream
import fs2.kafka.*

import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object pipeline:
  // TODO: we should bootstrap this from a config
  val connectTo = "localhost:9092"
  val groupId = "group"

  val consumerSettings = ConsumerSettings[IO, String, String]
    .withAutoOffsetReset(AutoOffsetReset.Earliest).withBootstrapServers(connectTo).withGroupId(groupId)

  def processCollections : IO[ExitCode] =
    KafkaProducer.stream(ProducerSettings[IO, String, String].withBootstrapServers(connectTo))
      .flatMap { producer =>
        KafkaConsumer.stream(consumerSettings)
          .subscribeTo(collector.topic).records.map { committable =>
            ProducerRecords.one(
              processor(ProducerRecord(processor.topic, committable.record.key, committable.record.value)), 
              committable.offset
            )}.evalMap { record => producer.produce(record).flatten }
      }.compile.drain.as(ExitCode.Success)

  def run : IO[ExitCode] = IO.parSequenceN(2)(collector.runCollection :: processCollections :: Nil)
    .map(l => if(l.forall(_ == ExitCode.Success)) ExitCode.Success else ExitCode.Error)
