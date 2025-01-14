package io.nary.compute

import cats.effect.*
import fs2.Stream
import fs2.kafka.*

import adapters.{ComputationResult, Unknown}

object processor:
  val topic = "processed"

  val in = KafkaConsumer.stream(ConsumerSettings[IO, String, String]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group")
  )

  val out : Stream[IO, KafkaProducer.Metrics[IO, String, String]] = KafkaProducer.stream(
    ProducerSettings[IO, String, String].withBootstrapServers("localhost:9092")
  )

  def apply(record: ProducerRecord[String, String]) : Option[ProducerRecord[String, String]] =
    adapters.resolveAndCompute(record.key, record.value) match {
      case ComputationResult(key, value) => Some(ProducerRecord(record.topic, key, value))
      case Unknown(_, _) => None
    }