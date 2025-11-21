package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class Choice(
    val message: AssistantMessage
)