package io.nary.compute

import cats.effect.IO
import cats.implicits.*

package object adapters:

  opaque type Prefix = String
  opaque type MetaKey = String

  trait Resolved:
    val kvPair: (String, String)

  trait StoredAs(override val kvPair: (String, String)) extends Resolved
  trait Computable(val adapter: Adapter) extends Resolved

  abstract case class SampleLabeledInt(label: String, number: Int) extends Computable(SampleNumericDataAdapter)
  abstract case class SampleLabeledText(label: String, text: String) extends Computable(SampleTextualDataAdapter)
  abstract case class DataError(p: Prefix, error: String) extends Resolved
  case class Unknown(key: String, value: String) extends StoredAs(s"$key.unknown" -> s"$value.unknown")
  case class ComputationResult(key: String, value: String) extends StoredAs(key -> value)

  type Data = SampleLabeledInt | SampleLabeledText | ComputationResult | DataError | Unknown

  trait Adapter:
    def read: IO[List[(String, String)]]
    def compute(d: Data with Resolved) : ComputationResult | Unknown

  val sources : List[Adapter] = List(SampleNumericDataAdapter, SampleTextualDataAdapter)

  def resolve(key: String, value: String) : Data with Resolved = key.split('.').toList match
    case prefix :: meta :: Nil if meta == "number" =>
      value.toIntOption.map(i => new SampleLabeledInt(prefix, i) with StoredAs(key -> value))
        .getOrElse(new DataError(prefix, "Data with meta == 'number' contains non-integer.") with StoredAs(key -> value))
    case prefix :: meta :: Nil if meta == "text" =>
      new SampleLabeledText(prefix, value) with StoredAs(key -> value)
    case _ => Unknown(key, value)

  def readAll : IO[List[(String, String)]] = sources.foldLeft(IO(List.empty[(String, String)])){ case (iol, adapter) =>
    for {
      all <- iol
      kvlist <- adapter.read
    } yield all ++ kvlist
  }

  def resolveAndCompute(key: String, value: String) : ComputationResult | Unknown = resolve(key, value) match
    case c : Data with Computable => c.adapter.compute(c)
    case _ => Unknown(key, value)

end adapters
