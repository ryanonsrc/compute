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

  def processAndCache(original: ProducerRecord[String, String]) : ProducerRecord[String, String] = {
    val proc = processor(original)
    collectedCache.addLast(original.key -> original.value)
    processedCache.addLast(proc.key -> proc.key)
    proc
  }

  def collectionConsumer : Stream[IO, KafkaConsumer[IO, String, String]] =
    KafkaConsumer.stream(ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest).withBootstrapServers(connectTo).withGroupId(groupId))
      .subscribeTo(collector.topic)

  def processCollections : IO[ExitCode] =
    KafkaProducer.stream(ProducerSettings[IO, String, String].withBootstrapServers(connectTo))
      .flatMap { producer =>
        collectionConsumer.records.map { committable =>
            ProducerRecords.one(
              processAndCache(ProducerRecord(processor.topic, committable.record.key, committable.record.value)),
              committable.offset
            )}.evalMap { record => producer.produce(record).flatten }
      }.compile.drain.as(ExitCode.Success)
