package com.solvek.kotlindna.marriages

import com.solvek.kotlindna.sqlite.Db
import com.solvek.kotlindna.sqlite.Db.setIntN
import java.io.Closeable

class MarriagesDao(table: String): Closeable {
    private val db = Db.createConnection("ocr/marriages.sqlite")

    fun ensure() = db.prepareStatement(sqlCreate).use { it.execute() }

    fun exists(ign: Int, scan: Int) = db.prepareStatement(sqlExists).use { ps ->
        ps.setInt(1, ign)
        ps.setInt(2, scan)
        ps.executeQuery().use { return@use it.next() }
    }

    fun insert(ign: Int, scan: Int, signature: String, marriage: Marriage) = db
        .prepareStatement(sqlInsert)
        .use { ps ->
            var i = 1
            ps.setInt(i++, ign)
            ps.setInt(i++, scan)
            ps.setString(i++, signature)

            with(marriage) {
                ps.setInt(i++, number)
                ps.setString(i++, date)

                ps.setString(i++, ms1)
                ps.setString(i++, ms2)
                ps.setString(i++, mn)
                ps.setString(i++, mp)
                ps.setString(i++, me)

                ps.setIntN(i++, ma)
                ps.setIntN(i++, mc)
                ps.setIntN(i++, mm)

                ps.setString(i++, fs1)
                ps.setString(i++, fs2)
                ps.setString(i++, fn)
                ps.setString(i++, fp)
                ps.setString(i++, fe)

                ps.setIntN(i++, fa)
                ps.setIntN(i++, fc)
                @Suppress("AssignedValueIsNeverRead")
                ps.setIntN(i++, fm)
            }

            ps.executeUpdate()
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

    private val sqlInsert = """
            INSERT INTO $table (
                ign, scan, signature, number, date,
                ms1, ms2, mn, mp, me, ma, mc, mm,
                fs1, fs2, fn, fp, fe, fa, fc, fm
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
}