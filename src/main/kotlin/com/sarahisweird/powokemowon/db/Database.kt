package com.sarahisweird.powokemowon.db

import com.sarahisweird.powokemowon.db.tables.EconomyTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Database {
    private const val jdbcUrl = "jdbc:mysql://localhost:3306/powokemowon"
    private const val driver = "com.mysql.jdbc.Driver"
    private val username = System.getenv("powokemowon_dbuser")
    private val password = System.getenv("powokemowon_dbpassword")

    val db by lazy {
        val db = Database.connect(
            url = jdbcUrl,
            driver = driver,
            user = username,
            password = password
        )

        transaction {
            SchemaUtils.create(EconomyTable)
        }

        db
    }
}