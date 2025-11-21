package com.solvek.kotlindna.tools

import com.google.api.services.sheets.v4.model.Sheet
import com.solvek.kotlindna.Keys
import com.solvek.kotlindna.gspreadsheet.SpreadsheetSource
import com.solvek.kotlindna.trees.CommentRow
import com.solvek.kotlindna.trees.EmptyTreeRow
import com.solvek.kotlindna.trees.TreeRow
import com.solvek.kotlindna.trees.parseTeeRow

private fun handleSheet(sheet: Sheet) {
    val id = sheet.properties.sheetId
    val name = sheet.properties.title
    val location = name.split("-")[1].trim()

    println("Handling sheet $name")

    val rows = sheet.data[0].rowData
    var rowNumber = 0
    for (row in rows){
        rowNumber++
        val treeRow = parseTeeRow(row.values)
        when(treeRow) {
            EmptyTreeRow -> println("Empty row")
            CommentRow -> println("Comment row")
            is TreeRow -> {
                val person = treeRow.parse(rowNumber)
                println(person)
            }
        }
    }
}

fun tree2plain(){
    val spreadsheetId = "19b3yZW6QU4Q7iCCw-kzbp5Fg4S_5pENNB0Dd3cIYkGE" // Родоводи (Володимирщина)
    val source = SpreadsheetSource(spreadsheetId, Keys.apiKey)

    source.sheets.forEach {
        handleSheet(it)
    }

    println("All finished")
}