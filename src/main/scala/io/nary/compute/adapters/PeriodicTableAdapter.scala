package io.nary.compute.adapters

import cats.effect.IO
import sttp.client3.*
import sttp.model.Uri

import scala.util.Random

object PeriodicTableAdapter extends Adapter:
  url = uri"https://gist.githubusercontent.com/GoodmanSciences/c2dd862cd38f21b0ad36b8f96b4bf1ee/raw/1d92663004489a5b6926e944c1b3d9ec5c40900e/Periodic%20Table%20of%20Elements.csv"
  
  def read: IO[List[(String, String)]] =
    for
      backend <- IO(HttpURLConnectionBackend())
      (compound, url) = choice
      response = basicRequest.get(url).send(backend)
      tableStr = response.body.toString
    yield List("table" -> tableStr) 
      
  def compute(d: Data with Resolved) : ComputationResult | Unknown = d match
    case ChemicalCompoundSynonyms(commonName, synonym) =>
      ComputationResult("Compound", s""""$commonName" is sometimes referred to as "$synonym"""")
    case _ => Unknown(d.kvPair._1, d.kvPair._2)



