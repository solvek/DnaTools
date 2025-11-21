package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.Base64

/**
 * Простий клієнт чату з історією.
 */
class ChatSession(
    private val apiKey: String,
    private val model: String,
    private val baseUrl: String = "https://api.openai.com/v1/chat/completions"
) {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val messages = mutableListOf<ChatMessage>()

    /** Додає системний промпт (робиш це один раз на початку). */
    fun system(text: String) {
        messages += ChatMessage(
            role = "system",
            content = listOf(
                MessageContent(
                    type = "text",
                    text = text
                )
            )
        )
    }

    /** Надіслати текстове повідомлення від користувача і отримати відповідь асистента. */
    fun sendUserText(text: String): String {
        val parts = listOf(MessageContent(type = "text", text = text))
        messages += ChatMessage(role = "user", content = parts)
        return callApiAndGetText()
    }

    /**
     * Надіслати зображення (у вигляді data:URL) + опціональний супровідний текст.
     * Використаємо це якраз для актів про шлюб.
     */
    fun sendUserImageFromDataUrl(dataUrl: String, extraText: String? = null): String {
        val parts = mutableListOf<MessageContent>()
        if (extraText != null) {
            parts += MessageContent(type = "text", text = extraText)
        }
        parts += MessageContent(
            type = "image_url",
            image_url = ImageUrl(url = dataUrl)
        )
        messages += ChatMessage(role = "user", content = parts)
        return callApiAndGetText()
    }

    /** Якщо треба – можна дістати всю відповідь як об’єкт. */
    fun sendRaw(requestModifier: (ChatCompletionRequest) -> ChatCompletionRequest = { it }): ChatCompletionResponse {
        val baseReq = ChatCompletionRequest(model = model, messages = messages)
        val req = requestModifier(baseReq)
        val bodyJson = json.encodeToString(ChatCompletionRequest.serializer(), req)

        val requestBody = bodyJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(baseUrl)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { resp ->
            val respBody = resp.body.string()
            if (!resp.isSuccessful) {
                throw IOException("HTTP ${resp.code}: $respBody")
            }
            return json.decodeFromString(ChatCompletionResponse.serializer(), respBody)
        }
    }

    /** Внутрішній метод: виклик API й повернення контенту as текст. */
    private fun callApiAndGetText(): String {
        val response = sendRaw()
        return response.choices.first().message.content
    }

    companion object {
        /** Допоміжна функція: перетворити файл-зображення у data:URL (для Vision). */
        fun File.asDataUrl(mimeType: String = "image/jpeg"): String {
            val bytes = readBytes()
            val b64 = Base64.getEncoder().encodeToString(bytes)
            return "data:$mimeType;base64,$b64"
        }
    }
}