package com.solvek.kotlindnascripts.tools

import com.solvek.kotlindnascripts.SpreadsheetSource

fun spovidky(){
    val spreadsheetId = "1wCKnRCDV8eiPDM70Hgcoa3Ofz8TRjbY9EmO_Pd33RNs"
    val sheetId = 1216283179
    val source = SpreadsheetSource(spreadsheetId, Keys.apiKey)

    val sheet = source.sheets.find { it.properties.sheetId == sheetId }
}
