package io.nary.compute

import cats.effect.*
import fs2.Stream
import fs2.kafka.*

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

  def apply(record: ProducerRecord[String, String]) : ProducerRecord[String, String] = {
    val (computedKey, computedValue) = adapters.resolveAndCompute(record.key, record.value).kvPair
    ProducerRecord(record.topic, computedKey, computedValue)
  }


