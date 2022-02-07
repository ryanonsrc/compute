package io.nary.compute.adapters

import cats.effect.IO

import scala.util.Random

object SampleNumericDataAdapter extends Adapter:
  def read: IO[List[(String, String)]] =
    for {
      _ <- IO(Random.setSeed(System.currentTimeMillis()))
      t1 = "test1.number" -> Random.nextInt().toString
      t2 = "test2.number" -> Random.nextInt().toString
    } yield t1 :: t2 :: Nil

  def compute(d: Data with Resolved) : ComputationResult | Unknown = d match
    case SampleLabeledInt(label, number) => ComputationResult(s"$label-computed", s"$number-computed")
    case _ => Unknown(d.kvPair._1, d.kvPair._2)

