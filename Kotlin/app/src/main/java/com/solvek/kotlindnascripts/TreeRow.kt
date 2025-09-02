@file:Suppress("UNCHECKED_CAST")

package com.solvek.kotlindnascripts

import com.google.api.services.sheets.v4.model.CellData

sealed class BaseTreeRow

object EmptyTreeRow: BaseTreeRow()

data class TreeRow(
    val indent: Int,
    val text: String,
    val links: List<Link>? = null
): BaseTreeRow()

data class Link(
    val url: String,
    val startIndex: Int
)

fun parseTeeRow(range: MutableCollection<in Any>): BaseTreeRow {
    if (range.isEmpty()) {
        return EmptyTreeRow
    }
    val columns = range.first() as List<CellData>

    columns.forEachIndexed { index, cell ->
        val text = cell.formattedValue
        if (text == null) return@forEachIndexed

        if (text.startsWith("#")) return@forEachIndexed

        val links = cell.textFormatRuns?.
            mapNotNull{
                val startIndex = it.startIndex
                val link = it.format?.link?.uri
                if (startIndex == null || link == null){
                    null
                }
                else {
                    Link(link, startIndex)
                }
            }

        return TreeRow(index+1, text, links)
    }

    return EmptyTreeRow
}