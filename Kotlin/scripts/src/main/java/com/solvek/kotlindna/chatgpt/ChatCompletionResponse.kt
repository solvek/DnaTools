package com.solvek.kotlindna.chatgpt;

import kotlinx.serialization.Serializable;

@Serializable
data class ChatCompletionResponse(
        val choices: List<com.solvek.kotlindna.chatgpt.Choice>
)