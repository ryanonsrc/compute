package io.nary.compute.adapters

import cats.effect.IO
import sttp.client3.*
import sttp.model.Uri

import scala.util.Random

object PeriodicTableAdapter extends Adapter:
  def read: IO[List[(String, String)]] =
    for {
      backend <- IO(HttpURLConnectionBackend())
      (compound, url) = choice
      response = basicRequest.get(url).send(backend)
      syn = response.body.toString.split('\n')
    } yield List(s"$compound.compound" -> choice(syn))

  def compute(d: Data with Resolved) : ComputationResult | Unknown = d match
    case ChemicalCompoundSynonyms(commonName, synonym) =>
      ComputationResult("Compound", s""""$commonName" is sometimes referred to as "$synonym"""")
    case _ => Unknown(d.kvPair._1, d.kvPair._2)

  def choice = choices(Random.nextInt(2))
  def choice(arr: Array[String]) = arr(Random.nextInt(arr.length)) // TODO: add expression evaluator here

  val choices  = //  
    "water" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/water/synonyms/txt" ::
    "carbon" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/carbon/synonyms/txt" ::
    "aspirin" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/asprin/synonyms/txt" :: Nil



