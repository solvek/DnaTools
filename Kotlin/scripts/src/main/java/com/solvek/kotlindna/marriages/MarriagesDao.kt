package com.solvek.kotlindna.marriages

import com.solvek.kotlindna.sqlite.Db
import java.io.Closeable

class MarriagesDao(table: String): Closeable {
    private val db = Db.createConnection("ocr/marriages.sqlite")

    fun ensure() = db.prepareStatement(sqlCreate).use { it.execute() }

    fun exists(ign: Int, scan: Int) = db.prepareStatement(sqlExists).use { ps ->
        ps.setInt(1, ign)
        ps.setInt(2, scan)
        ps.executeQuery().use { return@use it.next() }
    }

    fun insert(ign: Int, scan: Int, signature: String, marriage: Marriage) {
        TODO("Not yet implemented")
    }

    override fun close() = db.close()

    private val sqlCreate = """
        CREATE TABLE IF NOT EXISTS $table (
                ign         INTEGER NOT NULL,
                scan        INTEGER NOT NULL,
                signature   TEXT NOT NULL,
                number      INTEGER NOT NULL,              
                date        TEXT NOT NULL,
                ms1         TEXT,
                ms2         TEXT NOT NULL,
                mn          TEXT NOT NULL,
                mp          TEXT,
                me          TEXT,
                ma          INTEGER,
                mc          INTEGER,
                mm          INTEGER,
                fs1         TEXT,
                fs2         TEXT NOT NULL,
                fn          TEXT NOT NULL,
                fp          TEXT,
                fe          TEXT,
                fa          INTEGER,
                fc          INTEGER,
                fm          INTEGER,
                PRIMARY KEY(ign, scan)
            );
    """.trimIndent()

    private val sqlExists = "SELECT 1 FROM $table WHERE ign = ? AND scan = ? LIMIT 1"
}