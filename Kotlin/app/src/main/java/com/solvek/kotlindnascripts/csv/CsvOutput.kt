package com.solvek.kotlindnascripts.csv

import java.io.BufferedWriter
import java.io.Closeable
import java.io.File
import java.io.FileWriter

/**
 * Writes data to CSV file in `output` directory
 */
class CsvOutput(filename: String): Closeable {
    private val outputDir = File("output")
    private val file = File(outputDir, filename)
    private var writer: BufferedWriter? = null
    private var headerWritten = false

    /**
     * Always creates new file and adds header
     * @param columns Column names in header
     */
    fun newFile(vararg columns: String){
        ensureDir()
        writer?.close()
        writer = BufferedWriter(FileWriter(file, false)) // overwrite
        writeRow(*columns)
        headerWritten = true
    }

    /**
     * If file does not exist creates it and adds header. If exists opens this file and add rows to it by `append`
     * @param columns Column names in header
     */
    fun appendFile(vararg columns: String){
        ensureDir()
        val exists = file.exists()
        writer?.close()
        writer = BufferedWriter(FileWriter(file, true)) // append mode
        if (exists) {
            headerWritten = true // assume header already exists
        } else {
            writeRow(*columns)
            headerWritten = true
        }
    }

    /**
     * Appends a row into csv file
     */
    fun append(vararg values: String?){
        if (writer == null) {
            throw IllegalStateException("File is not opened. Call newFile() or appendFile() first.")
        }
        writeRow(*values)
    }

    private fun writeRow(vararg values: String?) {
        val line = values.joinToString(",") { escapeCsv(it ?: "") }
        writer!!.run {
            write(line)
            newLine()
            flush()
        }
    }

    private fun escapeCsv(value: String): String {
        val needsQuote = value.contains(",") || value.contains("\"") || value.contains("\n")
        return if (needsQuote) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    private fun ensureDir() {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }

    /**
     * Closes the csv file
     */
    override fun close() {
        writer?.close()
        writer = null
    }
}