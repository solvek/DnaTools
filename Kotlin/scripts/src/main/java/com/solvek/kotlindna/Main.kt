package com.solvek.kotlindna

import com.solvek.kotlindna.tools.ocr_marriages
import com.solvek.kotlindna.tools.ocr_notarius
import com.solvek.kotlindna.tools.spovidky
import com.solvek.kotlindna.tools.simple_test
import com.solvek.kotlindna.tools.tree2plain

fun main(args: Array<String>) {
    if (args.isEmpty()){
        println("No tool name specified")
        return
    }

    val tool = args[0]

    when(tool) {
        "test" -> simple_test()
        "tree2plain" -> tree2plain()
        "spovidky" -> spovidky()
        "ocr_marriages" -> ocr_marriages()
        "ocr_notarius" -> ocr_notarius()
        else -> {
            println("Unknown tool $tool")
            return
        }
    }

    println("All finished")
}
