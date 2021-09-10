package com.sarahisweird.powokemowon.commands

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.conversations.buildEncounter
import com.sarahisweird.powokemowon.conversations.testConversation
import com.sarahisweird.powokemowon.conversations.workConversation
import com.sarahisweird.powokemowon.db.entities.EconomyUser
import com.sarahisweird.powokemowon.db.tables.EconomyTable
import com.sarahisweird.powokemowon.pokemonList
import com.sarahisweird.powokemowon.utils.addBalance
import com.sarahisweird.powokemowon.utils.toGermanString
import com.sarahisweird.powokemowon.utils.toSeparatedString
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.requestMembers
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.jakejmattson.discordkt.api.TypeContainer
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.commands
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private val executionTimes = mutableMapOf<Snowflake, MutableMap<String, Instant>>()

@OptIn(ExperimentalTime::class)
private fun getOrSetTimeRemaining(
    userId: Snowflake,
    command: String,
    cooldown: Long
): Duration {
    val now = Clock.System.now()
    val lastExecution = executionTimes[userId]?.get(command) ?: Instant.DISTANT_PAST
    val difference = now - lastExecution

    if (difference.inWholeSeconds > cooldown) {
        executionTimes.getOrPut(userId) { mutableMapOf() }[command] = Clock.System.now()
    }

    return Duration.seconds(cooldown - difference.inWholeSeconds)
}

@OptIn(ExperimentalTime::class)
private suspend fun <T : TypeContainer> CommandEvent<T>.timedCommand(
    userId: Snowflake,
    commandName: String,
    cooldown: Long,
    onSuccess: suspend CommandEvent<T>.() -> Unit
) {
    val timeRemaining = getOrSetTimeRemaining(userId, commandName, cooldown)

    if (timeRemaining.isNegative()) {
        onSuccess()
    } else {
        respond("Du kannst diesen Befehl erst in ${timeRemaining.toGermanString()}" +
                " ausführen, ${getMember()!!.displayName}.")
    }
}

@OptIn(PrivilegedIntent::class)
fun economyCommands() = commands("Economy") {
    command("catch") {
        description = "Fange wilde Pokemon!"

        execute {
            timedCommand(
                author.id,
                "catch",
                R.Cooldowns.catch
            ) {
                buildEncounter(pokemonList!!).startPublicly(discord, author, channel)
            }

        }
    }

    command("simp") {
        description = "Feelinara simpt für dich!"

        execute {
            timedCommand(
                author.id,
                "simp",
                R.Cooldowns.simp
            ) {
                val reward = Random.nextLong(R.RewardRanges.simp)

                addBalance(author.id, reward)

                respond("Feelinara hat für ${getMember()!!.displayName} gesimpt!" +
                        " Es gab dir $reward ₽.")
            }
        }
    }

    command("freerealestate", "free", "fre") {
        description = "It's free real estate!"

        execute {
            timedCommand(
                author.id,
                "freerealestate",
                R.Cooldowns.freeRealEstate
            ) {
                val reward = Random.nextLong(R.RewardRanges.freeRealEstate)

                addBalance(author.id, reward)

                respond("*It's free real estate im Wert von $reward ₽," +
                        " ${getMember()!!.displayName}!*")
            }
        }
    }

    command("daily") {
        description = "Einmal pro Tag spendet Feelinara dir Geld!"

        execute {
            timedCommand(
                author.id,
            "daily",
                R.Cooldowns.daily
            ) {
                val reward = Random.nextLong(R.RewardRanges.daily)

                addBalance(author.id, reward)

                respond("Feelinara spendet dir $reward ₽, ${getMember()!!.displayName}!")
            }
        }
    }

    command("test") {
        description = "Bist du ein Roboter? :O"

        execute {
            timedCommand(
                author.id,
                "test",
                R.Cooldowns.test
            ) {
                testConversation(getMember()!!).startPublicly(discord, author, channel)
            }
        }
    }

    command("work") {
        description = "Arbeite für Feelinara, du Sklave!"

        execute {
            timedCommand(
                author.id,
                "work",
                R.Cooldowns.work
            ) {
                workConversation(getMember()!!).startPublicly(discord, author, channel)
            }
        }
    }

    command("kontostand", "konto", "ks", "balance", "bal", "b") {
        description = "Zeigt deinen Kontostand an."

        execute {
            val balance = transaction {
                EconomyUser.find { EconomyTable.userId eq author.id.value }
                    .first().cash.toSeparatedString()
            }

            respond("${getMember()!!.displayName}," +
                    " dein jetziger Kontostand beträgt $balance ₽.")
        }
    }

    command("topbalance", "topbal", "balancetop", "baltop", "rangliste", "top") {
        description = "Zeigt die Nutzer mit dem höchsten Kontostand an."

        execute {
            channel.type()

            val members = guild!!.requestMembers().toList().flatMap { it.members }
                .associate { it.id to it.displayName }

            val tops = transaction {
                EconomyUser.all().orderBy(EconomyTable.cash to SortOrder.DESC)
                    .limit(10).map { it.userId to it.cash.toSeparatedString() }
            }.mapIndexed { i, it ->
                "${if (i + 1 < 10) " " else ""}${i + 1}." +
                        " ${members[it.first]}: ${it.second} ₽"
            }.joinToString("\n")

            respond("Hier ist die Rangliste der Kontostände:\n\n```$tops```")
        }
    }
}