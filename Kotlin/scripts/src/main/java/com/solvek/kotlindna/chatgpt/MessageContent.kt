package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class MessageContent(
    val type: String,                // "text" або "image_url"
    val text: String? = null,
    val image_url: ImageUrl? = null
)