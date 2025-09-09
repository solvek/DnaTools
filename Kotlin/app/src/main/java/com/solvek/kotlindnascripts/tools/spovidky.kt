@file:Suppress("UNCHECKED_CAST")

package com.solvek.kotlindnascripts.tools

import Keys
import com.google.api.services.sheets.v4.model.CellData
import com.solvek.kotlindnascripts.csv.CsvOutput
import com.solvek.kotlindnascripts.gspreadsheet.SpreadsheetSource

fun spovidky(){
    val spreadsheetId = "1wCKnRCDV8eiPDM70Hgcoa3Ofz8TRjbY9EmO_Pd33RNs"
    val sheetId = 1216283179
    val source = SpreadsheetSource(spreadsheetId, Keys.apiKey)

    val sheet = source.sheets.find { it.properties.sheetId == sheetId }

    var currentSurname: String? = null
    var currentFather: String? = null
    var stan: String? = null
    var page: Int? = null

    CsvOutput("${sheet!!.properties.title} - 1912 L.csv").use { csv ->
        csv.newFile("Стан", "Ст", "Прізвище", "Ім'я", "Батько", "Примітка", "Рік народження")

        sheet
            .data[0]
            .rowData
            .drop(10)
            .forEach { row ->
                val columns = row.values.firstOrNull() as List<CellData>
                val col1 = columns[0].formattedValue

                if (!col1.empty){
                    try {
                        page = Integer.parseInt(col1)
                    }
                    catch (_: Exception){
                        stan = col1
                        return@forEach
                    }
                }

                var surname = columns[1].formattedValue
                val name = columns[2].formattedValue
                var father = columns[3].formattedValue

                if (name.empty){
                    return@use
                }

                if (father.empty){
                    father = currentFather
                }

                if (surname.empty){
                    surname = currentSurname
                }
                else {
                    currentFather = name
                    currentSurname = surname
                }

                csv.append(
                    stan!!,
                    page.toString(),
                    surname,
                    name,
                    father,
                    columns[5].formattedValue,
                    columns[6].formattedValue,
                    )
            }
    }
}

private val String?.empty get() = this == null || this.isEmpty()