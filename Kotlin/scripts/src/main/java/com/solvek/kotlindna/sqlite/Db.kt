package com.solvek.kotlindna.sqlite

import java.sql.Connection
import java.sql.DriverManager

object Db {
    fun createConnection(dbFile: String): Connection =
        DriverManager.getConnection("jdbc:sqlite:$dbFile")
}