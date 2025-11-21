package com.solvek.kotlindna.tools

import com.solvek.kotlindna.Keys
import com.solvek.kotlindna.chatgpt.ChatCompletionResponse
import com.solvek.kotlindna.chatgpt.ChatSession
import com.solvek.kotlindna.chatgpt.ChatSession.Companion.asDataUrl
import com.solvek.kotlindna.marriages.Marriage
import com.solvek.kotlindna.marriages.MarriagesDao
import kotlinx.serialization.json.Json
import java.io.File

const val directory = "122484632"
const val raion = "ovadne_raion2"
const val signature = "Р-3247-2-1643"

@Suppress("FunctionName")
fun ocr_marriages() {
    val chat = ChatSession(
        apiKey = Keys.chatGptApiKey,
        model = "gpt-5.1"
    )

    chat.system(
        """
    Я тобі буду давати по одному зображенню. На цих зображеннях дивись тільки праву половину.
    Там акт про одруження. Текст може бути українською, російскьою чи змішано. Розпізнай дані про шлюб і видай у вигляді json.
    Мають бути такі поля: Номер акту(number), дата(date), прізвище нареченого після одруження(ms1), прізвище нареченого до одруження(ms2),
    ім'я нареченого(mn), по батькові нареченого(mp), національність нареченого(me), вік нареченого(ma), чи мав дітей(mc),
    номер шлюбу нареченого(числом, mm),
    прізвище нареченої після одруження(fs1), прізвище нареченої до одруження(fs2), ім'я нареченої(fn), по батькові нареченої(fp),
    національність нареченої(fe), вік нареченої(fa), чи мала дітей(fc), номер шлюбу нареченої(числом, fm).
    Повертай повністю правильний json, нічого зайвого.
    Дата має бути у форматі YYYY-MM-DD, кількість дітей теж переводь у число, якщо нема - 0.
    Перекладай усі прізвища на українську. Імена теж перекладай та адаптуй до сучасних українських імен.
    По батькові переводь просто у ім'я батька.
    """.trimIndent()
    )

    val json = Json { ignoreUnknownKeys = true }

    MarriagesDao(raion).use { db ->
        db.ensure()

        File("ocr/$directory").listFiles()
            ?.filter { it.isFile }
            ?.sortedBy { it.name.lowercase() }
            ?.forEach { file ->
                val parts = file.nameWithoutExtension.split("_")
                val ign = parts[0].toInt()
                val scan = parts[1].trimStart('0').ifEmpty { "0" }.toInt()

                if (db.exists(ign, scan)){
                    return
                }
                println("Processing: $ign, $scan")

                val dataUrl = file.asDataUrl()
                val jsonReply = chat.sendUserImageFromDataUrl(dataUrl)
                println(jsonReply)

                val marriage = json.decodeFromString(Marriage.serializer(), jsonReply)
                db.insert(ign, scan, signature, marriage)
            }
    }
}