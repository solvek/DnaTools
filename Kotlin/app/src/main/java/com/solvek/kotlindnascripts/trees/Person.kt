package com.solvek.kotlindnascripts.trees

data class Person(
    val rowNumber: Int,
    val treeRow: TreeRow,
    val rawName: String,
    val markedFemale: Boolean,
    val birth: Event?,
    val death: Event?,
    val marriage: Event?,
    val withMarriage: Boolean,
    val location: String?
)