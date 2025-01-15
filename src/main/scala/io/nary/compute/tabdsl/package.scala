package io.nary.compute

import cats.syntax.option.*
import cats.syntax.either.*

package object tabdsl:

  enum TabDslError:
    case InvalidCsvTable
    case UnexpectedCsvValueType
    case FailedRowLookup
    case FailedHeaderLookup
  
  import TabDslError.*
  
  trait CsvTableRowKey
  case class DefaultRowKey() extends CsvTableRowKey
  case class CustomRowKey(field: String) extends CsvTableRowKey

  case class CsvTableRow(values: List[String])
  case class CsvTable(headerRow: List[String], rows: Map[CsvTableRowKey, List[CsvTableRow]]):
    def selectColumn(rowKey: CsvTableRowKey, columnHeader: String) : Either[TabDslError, List[String]] =
      for
        rlist <- rows.get(rowKey).toRight(FailedRowLookup)                    // Get All Rows by Key
        hindex = headerRow.indexWhere(_ == columnHeader, 0)                     // Find the index of the column by header
        _ <- if hindex < 0 then Left(FailedHeaderLookup) else Right(())
        selected <- Right(for
                      row <- rlist
                      col <- Nil.flatMap(_ => row.values.lift(hindex).toList)   // Get values for the column at hindex
                    yield col)
      yield selected
