package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<com.solvek.kotlindna.chatgpt.ChatMessage>,
    val max_tokens: Int? = null,
    val temperature: Double? = null
)