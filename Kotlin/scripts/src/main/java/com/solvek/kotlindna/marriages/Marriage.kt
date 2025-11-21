package com.solvek.kotlindna.marriages

import kotlinx.serialization.Serializable

@Serializable
data class Marriage(
    val number: Int,
    val date: String,
    val ms1: String?,
    val ms2: String,
    val mn: String,
    val mp: String?,
    val me: String?,
    val ma: Int?,
    val mc: Int?,
    val mm: Int?,
    val fs1: String?,
    val fs2: String,
    val fn: String,
    val fp: String?,
    val fe: String?,
    val fa: Int?,
    val fc: Int?,
    val fm: Int?
)
