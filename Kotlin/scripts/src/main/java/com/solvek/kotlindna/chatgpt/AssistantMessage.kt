package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class AssistantMessage(
    val role: String,
    val content: String
)