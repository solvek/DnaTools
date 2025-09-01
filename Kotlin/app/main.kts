#!/usr/bin/env kotlin

@file:DependsOn("com.google.apis:google-api-services-sheets:v4-rev20230815-2.0.0")
@file:DependsOn("com.google.api-client:google-api-client:2.0.0")
@file:DependsOn("com.google.oauth-client:google-oauth-client:1.34.1")
@file:DependsOn("com.google.http-client:google-http-client-gson:1.43.3")

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory

println("Hello, World!")

val spreadsheetId = "19b3yZW6QU4Q7iCCw-kzbp5Fg4S_5pENNB0Dd3cIYkGE"
val apiKey = "YOUR_API_KEY"

val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
val jsonFactory = GsonFactory.getDefaultInstance()