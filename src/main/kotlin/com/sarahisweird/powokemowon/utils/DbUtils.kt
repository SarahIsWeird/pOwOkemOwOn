package com.sarahisweird.powokemowon.utils

import com.sarahisweird.powokemowon.db.entities.EconomyUser
import com.sarahisweird.powokemowon.db.tables.EconomyTable
import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

fun getTabulatedDatabaseResult(query: String) =
    transaction {
        TransactionManager.current().exec(query) {
            tabulateDatabaseResult(it)
        }
    }

fun tabulateDatabaseResult(rs: ResultSet): String {
    val header = buildDbResultHeader(rs)
    val body = buildDbResultBody(rs)

    return "$header\n${header.length.createString('-')}\n$body"
}

fun buildDbResultHeader(rs: ResultSet): String =
    (1..rs.metaData.columnCount).joinToString(" | ") { i ->
        rs.metaData.getColumnName(i)
    }

fun buildDbResultBody(rs: ResultSet): String {
    val result = mutableListOf<String>()

    while (rs.next()) {
        result += (1..rs.metaData.columnCount).joinToString(" | ") { i ->
            rs.getString(i)
        }
    }

    return result.joinToString("\n")
}

fun addBalance(userId: Snowflake, cash: Long) {
    transaction {
        EconomyUser.find { EconomyTable.userId eq userId.value }
            .forEach { it.cash += cash }
    }
}

fun exportDatabaseToSql(): String =
    "INSERT INTO ${EconomyTable.tableName} (${EconomyTable.userId.name}," +
            " ${EconomyTable.cash.name}) VALUES " +
            transaction {
        EconomyUser.all().joinToString(", ") {
            "(${it.userId.value}, ${it.cash})"
        }
    } + ";"