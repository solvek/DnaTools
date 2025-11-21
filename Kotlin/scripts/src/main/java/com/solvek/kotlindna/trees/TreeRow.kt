@file:Suppress("UNCHECKED_CAST")

package com.solvek.kotlindna.trees

import com.google.api.services.sheets.v4.model.CellData
import kotlin.text.get

sealed class BaseTreeRow

object EmptyTreeRow: BaseTreeRow()
object CommentRow: BaseTreeRow()

data class TreeRow(
    val indent: Int,
    val text: String,
    val links: List<Link>
): BaseTreeRow(){
    fun parse(rowNumber: Int): Person {
        val regex = "(?<isFemale>Ж\\s)?(?<hasMarriage>[SMМП]((?<marriageDate>(\\d{1,2}\\.\\d{1,2}\\.)?\\d{4})(?<marriageRef>\\S*))?\\s)?(?<rawName>[^,]+)(,\\s((?<birthDate>(\\d{1,2}\\.\\d{1,2}\\.)?\\d{4})(?<birthRef>[^\\s,-]*))?(-(?<deathDate>(\\d{1,2}\\.\\d{1,2}\\.)?\\d{4})(?<deathRef>[^\\s,]*))?(,\\s(?<location>[^,=]+))?)?".toRegex()
        val m = regex.find(text)!!

        return Person(
            rowNumber,
            this,
            m.groups["rawName"]!!.value,
            m.hasGroup("isFemale"),
            m.buildEvent("birthDate", "birthRef"),
            m.buildEvent("deathDate", "deathRef"),
            m.buildEvent("marriageDate", "marriageRef"),
            m.hasGroup("hasMarriage"),
            m.groups["location"]?.value
        )
    }

    private fun MatchResult.buildEvent(dateName: String, refName: String) = groups[dateName]?.let { g ->
        Event(
            g.value,
            groups[refName]?.value,
            g.range.link
        )
    }

    private fun MatchResult.hasGroup(groupName: String) =
        groups[groupName] != null

    private val IntRange.link get() = links.firstOrNull {
        contains(it.startIndex)
    }?.url
}

data class Link(
    val url: String,
    val startIndex: Int
)

fun parseTeeRow(range: MutableCollection<in Any>): BaseTreeRow {
    val columns = (range.firstOrNull() ?: return EmptyTreeRow) as List<CellData>

    columns.forEachIndexed { index, cell ->
        val text = cell.formattedValue
        if (text == null) return@forEachIndexed

        if (text.startsWith("#")) return CommentRow

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
            } ?: emptyList()

        return TreeRow(index+1, text, links)
    }

    return EmptyTreeRow
}