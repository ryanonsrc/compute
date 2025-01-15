package io.nary.compute.adapters

import cats.effect.IO
import sttp.client3.*
import sttp.model.Uri

import scala.util.Random

object PubChemAdapter extends Adapter:
  def read: IO[List[(String, String)]] =
    for
      backend <- IO(HttpURLConnectionBackend())
      (compound, url) = pick
      response = basicRequest.get(url).send(backend)
      synonyms = response.body.toString.split('\n')
    yield List(s"$compound.compound" -> pick(synonyms))

  def compute(d: Data with Resolved) : ComputationResult | Unknown = d match
    case ChemicalCompoundSynonyms(commonName, synonym) =>
      ComputationResult("Compound", s""""$commonName" is sometimes referred to as "$synonym"""")
    case _ => Unknown(d.kvPair._1, d.kvPair._2)

  // Choose an compound we want to get synonyms for at random
  private def pick: (String, Uri) = choices(Random.nextInt(2))
  
  // Choose a synonym at random
  private def pick(arr: Array[String]): String = arr(Random.nextInt(arr.length))

  val choices: List[(String, sttp.model.Uri)] =
    "water" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/water/synonyms/txt" ::
    "carbon" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/carbon/synonyms/txt" ::
    "aspirin" -> uri"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/asprin/synonyms/txt" :: Nil



