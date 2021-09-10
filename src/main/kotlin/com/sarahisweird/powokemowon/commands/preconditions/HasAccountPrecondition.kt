package com.sarahisweird.powokemowon.commands.preconditions

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.db.entities.EconomyUser
import com.sarahisweird.powokemowon.db.tables.EconomyTable
import dev.kord.core.behavior.channel.createMessage
import me.jakejmattson.discordkt.api.dsl.precondition
import org.jetbrains.exposed.sql.transactions.transaction

fun hasAccount() = precondition {
    if (command?.category != "Economy" || transaction {
            !EconomyUser.find { EconomyTable.userId eq author.id.value }.empty()
    }) return@precondition

    respond(R.welcomeMessage)

    transaction {
        EconomyUser.new {
            userId = author.id
            cash = R.startingCash
        }
    }
}