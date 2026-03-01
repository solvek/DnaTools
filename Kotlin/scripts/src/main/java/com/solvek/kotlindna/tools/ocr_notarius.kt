package com.solvek.kotlindna.tools

import com.solvek.kotlindna.Keys
import com.solvek.kotlindna.chatgpt.ChatSession
import com.solvek.kotlindna.chatgpt.ChatSession.Companion.asDataUrl
import com.solvek.kotlindna.csv.CsvOutput
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

// Папка де лежать підпапки з книгами (кожна книга = окрема підпапка)
const val notariusRootDir = "ocr/notarius"

@Serializable
data class NotariusPerson(
    val surname: String? = null,
    val name: String? = null,
    val patronymic: String? = null,
    val settlement: String? = null,
    val status: String? = null,
    val page_ref: String? = null
)

@Suppress("FunctionName")
fun ocr_notarius() {
    val chat = ChatSession(
        apiKey = Keys.chatGptApiKey,
        model = "gpt-4.1"
    )

    chat.system(
        """
        You will receive scanned pages from notarial books of Volyn Governorate, late 19th - early 20th century.
        Text is handwritten in old Russian language.
        Find ALL mentioned people on the page and return a JSON array of objects.
        Each object must have these fields:
          surname - last name,
          name - first name,
          patronymic - father's name only (without suffix, not "Васильович", "Василевич", "Васильев", but simple "Василь"),
          settlement - village or city name,
          status - social status (селянин/дворянин/міщанин/купець etc.),
          page_ref - where on the page (e.g. "ліва колонка", "права колонка").
        If a field is missing or unreadable - use null.
        Translate and adapt ALL names to modern Ukrainian language.
        Return ONLY a valid JSON array, no extra text, no markdown.
        """.trimIndent()
    )

    val json = Json { ignoreUnknownKeys = true }
    val rootDir = File(notariusRootDir)

    CsvOutput("notarius_persons.csv").use { csv ->
        csv.newFile("url", "Файл", "Прізвище", "Ім'я", "По батькові", "Населений пункт", "Статус", "Посилання на сторінку")

        rootDir.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name.lowercase() }
            ?.forEach { bookDir ->
                val bookName = bookDir.name
                println("=== Книга: $bookName ===")

                // Нова сесія для кожної книги щоб не переповнювати контекст
                chat.reset()

                val bookUrl = "https://www.familysearch.org/uk/records/images/search-results?imageGroupNumbers=$bookName"

                bookDir.listFiles()
                    ?.take(3)
                    ?.filter { it.isFile && it.extension.lowercase() in listOf("jpg", "jpeg", "png") }
                    ?.sortedBy { it.name.lowercase() }
                    ?.forEach { file ->
                        println("  Обробляю: ${file.name}")

                        try {
                            val dataUrl = file.asDataUrl()
                            val rawReply = chat.sendUserImageFromDataUrl(
                                dataUrl,
                                extraText = "This is page ${file.nameWithoutExtension}. Extract all people mentioned on this page and return JSON array only."
                            )
                            println("  Відповідь: $rawReply")

                            // Витягуємо JSON масив з відповіді (на випадок якщо модель додала зайвий текст)
                            val jsonReply = extractJsonArray(rawReply)
                            if (jsonReply == null) {
                                System.err.println("  Не вдалось витягти JSON з відповіді, пропускаємо")
                                return@forEach
                            }

                            val persons = json.decodeFromString(
                                ListSerializer(NotariusPerson.serializer()),
                                jsonReply
                            ).filter { !it.surname.isNullOrBlank() }

                            println("  Знайдено осіб: ${persons.size}")

                            persons.forEach { person ->
                                csv.append(
                                    bookUrl,
                                    file.name,
                                    person.surname ?: "",
                                    person.name ?: "",
                                    person.patronymic ?: "",
                                    person.settlement ?: "",
                                    person.status ?: "",
                                    person.page_ref ?: ""
                                )
                            }
                        } catch (ex: Exception) {
                            System.err.println("  Помилка при обробці ${file.name}: $ex")
                        }
                    }
            }
    }

    println("Готово! Результат збережено у output/notarius_persons.csv")
}

/**
 * Витягує JSON масив з рядка відповіді.
 * Захист від випадку коли модель додає зайвий текст навколо JSON.
 */
private fun extractJsonArray(text: String): String? {
    val start = text.indexOf('[')
    val end = text.lastIndexOf(']')
    if (start == -1 || end == -1 || end < start) return null
    return text.substring(start, end + 1)
}