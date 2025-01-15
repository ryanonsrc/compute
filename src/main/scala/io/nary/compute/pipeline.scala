package io.nary.compute

import cats.effect.*
import cats.syntax.all.*
import fs2.Stream
import fs2.kafka.*
import fs2.concurrent.*
import io.nary.compute.pipeline.collectionConsumer

import java.util.concurrent.ConcurrentLinkedDeque
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object pipeline:
  val connectTo = "localhost:9092"
  val groupId = "group"

  val collectedCache = new ConcurrentLinkedDeque[(String, String)]()
  val processedCache = new ConcurrentLinkedDeque[(String, String)]()

  def processAndCache(original: ProducerRecord[String, String]) : Option[ProducerRecord[String, String]] =
    processor(original) match {
      case Some(proc) =>
        collectedCache.addLast(original.key -> original.value)
        processedCache.addLast(proc.key -> proc.value)
        Some(proc)
      case None => None
    }

  def collectionConsumer : Stream[IO, KafkaConsumer[IO, String, String]] =
    KafkaConsumer.stream(ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest).withBootstrapServers(connectTo).withGroupId(groupId))
      .subscribeTo(collector.topic)

  def processCollections : IO[ExitCode] =
    KafkaProducer.stream(ProducerSettings[IO, String, String].withBootstrapServers(connectTo))
      .flatMap { producer =>
        collectionConsumer.records.flatMap { committable =>
          processAndCache(ProducerRecord(processor.topic,
            committable.record.key, committable.record.value)) match {
              case None => Stream.empty
              case Some(record) => Stream.emit(ProducerRecords.one(record, committable.offset))
            }
        }.evalMap { record => producer.produce(record).flatten }
      }.compile.drain.as(ExitCode.Success)
