import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.ValueRange

fun Sheets.parseSheet(sheet: Sheet) {
    val id = sheet.properties.sheetId
    val name = sheet.properties.title
    val location = name.split("-")[1].trim()

    val range = "'$name'!A1:Z"
    val response: ValueRange = spreadsheets().values()
        .get(Keys.spreadsheetId, range)
        .setKey(Keys.apiKey)
        .execute()
}

fun main() {

    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = GsonFactory.getDefaultInstance()


    val service = Sheets.Builder(httpTransport, jsonFactory, null)
        .setApplicationName("KotlinSheetsDemo")
        .build()

    val spreadsheet: Spreadsheet = service.spreadsheets()
        .get(Keys.spreadsheetId)
        .setIncludeGridData(true)
        .setKey(Keys.apiKey)
        .execute()

    spreadsheet.sheets.forEach{
        service.parseSheet(it)
    }
}