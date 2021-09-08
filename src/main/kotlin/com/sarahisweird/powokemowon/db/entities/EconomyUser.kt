package com.sarahisweird.powokemowon.db.entities

import com.sarahisweird.powokemowon.db.tables.EconomyTable
import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EconomyUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EconomyUser>(EconomyTable)

    var userId: Snowflake by EconomyTable.userId.transform(
        toColumn = { it.value },
        toReal = { Snowflake(it) }
    )

    var cash by EconomyTable.cash
}