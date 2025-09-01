import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory

fun main() {

    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = GsonFactory.getDefaultInstance()


    val service = Sheets.Builder(httpTransport, jsonFactory, null)
        .setApplicationName("KotlinSheetsDemo")
        .build()

    val spreadsheet: Spreadsheet = service.spreadsheets()
        .get(Keys.spreadsheetId)
        .setKey(Keys.apiKey)
        .execute()

    println("Листи у таблиці:")
    spreadsheet.sheets.forEach {
        println("- " + it.properties.title)
    }
}