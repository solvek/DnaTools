package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val role: String,                // "system" | "user" | "assistant"
    val content: List<com.solvek.kotlindna.chatgpt.MessageContent>
)