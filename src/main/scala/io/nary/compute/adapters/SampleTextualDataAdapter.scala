package io.nary.compute.adapters

import cats.effect.IO

import scala.util.Random

object SampleTextualDataAdapter extends Adapter:
  def read: IO[List[(String, String)]] =
    for {
      _ <- IO(Random.setSeed(System.currentTimeMillis()))
      t = "test.text" -> Random.nextString(5).toString
    } yield t :: Nil

  def compute(d: Data with Resolved) : ComputationResult | Unknown = d match
    case SampleLabeledText(label, text) => ComputationResult(s"$label-computed", s"$text-computed")
    case _ => Unknown(d.kvPair._1, d.kvPair._2)

