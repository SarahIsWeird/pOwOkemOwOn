package com.sarahisweird.powokemowon.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object EconomyTable : IntIdTable() {
    val userId = long("user_id")
    val cash = long("cash")
}