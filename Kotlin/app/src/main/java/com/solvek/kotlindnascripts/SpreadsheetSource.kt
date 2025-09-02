package com.solvek.kotlindnascripts

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet

class SpreadsheetSource(private val id: String, private val apiKey: String) {
    private val httpTransport by lazy { GoogleNetHttpTransport.newTrustedTransport() }
    private val jsonFactory by lazy { GsonFactory.getDefaultInstance() }

    private val service by lazy { Sheets.Builder(httpTransport, jsonFactory, null)
        .setApplicationName("KotlinDnaTools")
        .build()
    }

    val sheets by lazy {
        val spreadsheet: Spreadsheet = service.spreadsheets()
            .get(id)
            .setIncludeGridData(true)
            .setKey(apiKey)
            .execute()

        spreadsheet.sheets
    }

//    fun readAllRows(sheetName: String): ValueRange? {
//        val range = "'$sheetName'!A1:Z"
//        return service.spreadsheets().values()
//            .get(id, range)
//            .setKey(apiKey)
//            .execute()
//    }
}