package com.solvek.kotlindna.chatgpt

import kotlinx.serialization.Serializable

@Serializable
data class ImageUrl(
    val url: String,
    val detail: String? = null       // можна "low", "high" і т.д., якщо треба
)