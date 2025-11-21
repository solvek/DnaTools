package com.solvek.kotlindna.sqlite

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

object Db {
    fun createConnection(dbFile: String): Connection =
        DriverManager.getConnection("jdbc:sqlite:$dbFile")

    fun PreparedStatement.setIntN(pos: Int, v: Int?) = if (v == null){
        setObject(pos, null)
    }
    else {
        setInt(pos, v)
    }
}