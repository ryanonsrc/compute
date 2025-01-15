package io.nary.compute.tabdsl

import io.nary.espresso.*
import compose.*
import io.nary.compute.tabdsl.TabDslError.InvalidCsvTable
import lift.*

object library:
  def readCsvTable: defs.Expr[TabDslError, String, CsvTable] =
    funcExpr1[TabDslError, String, CsvTable] { str =>
      val rows = str.split('\n').map(_.split("."))
      val headerRow = rows(0)
      val map = Map[CsvTableRowKey, List[CsvTableRow]](DefaultRowKey() -> 
        rows.slice(1, rows.length).map(arr => CsvTableRow(arr.toList)).toList)
      CsvTable(headerRow.toList, map)
    }(_ => InvalidCsvTable)

  def byKey : defs.Expr[TabDslError, (CsvTable, String), CsvTable] =
    op2[TabDslError, CsvTable, String, CsvTable]{ case (tab, key) => 
    
    }