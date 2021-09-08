package com.sarahisweird.powokemowon.conversations

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.data.PokemonData
import com.sarahisweird.powokemowon.data.PokemonList
import com.sarahisweird.powokemowon.db.entities.EconomyUser
import com.sarahisweird.powokemowon.db.tables.EconomyTable
import com.sarahisweird.powokemowon.utils.calculatePercentage
import com.sarahisweird.powokemowon.utils.calculateScore
import com.sarahisweird.powokemowon.utils.keepLetters
import dev.kord.core.entity.Member
import dev.kord.rest.builder.message.EmbedBuilder
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.conversations.conversation
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.roundToLong
import kotlin.random.Random

private fun buildGifUrl(pokemon: PokemonData, isShiny: Boolean) =
    "http://cvps.sarahisweird.com:8100/${if (isShiny) "ani-shiny" else "ani"}" +
        "/${pokemon.name.lowercase()
            .replace("♀", "f")
            .replace("♂", "m")
            .replace("-", "")
            .replace(":", "")
            .replace(".", "")
            .replace(" ", "")
        }.gif"

private fun EmbedBuilder.buildEncounterEmbed(
    member: Member,
    pokemon: PokemonData,
    isShiny: Boolean
) {
    author { name = "pOwOkemOwOn" }
    title = "Ein wildes Pokemon erscheint!"
    description = "${member.displayName}, wie heißt dieses Pokemon?"
    image = buildGifUrl(pokemon, isShiny)
}

private fun EmbedBuilder.buildResponseEmbed(
    pokemon: PokemonData,
    bothNames: String,
    score: Int,
    length: Int,
    percentage: Float,
    wasCaught: Boolean,
    isShiny: Boolean
) {
    val earnedCash = calculateEarnedCash(percentage, wasCaught)
    var percentageStr = percentage.toString()

    if (percentageStr.endsWith(".0"))
        percentageStr = percentageStr.dropLast(2)

    author { name = "pOwOkemOwOn" }

    title = if (wasCaught) {
        "Du hast $bothNames gefangen!" + if (isShiny) " Es ist Shiny! :O" else ""
    } else {
        "$bothNames ist entkommen!"
    }

    description = "Deine Antwort war zu $score/$length" +
            " ($percentageStr%) korrekt!"

    if (earnedCash > 0) description += "\nDu gewinnst $earnedCash ₽!"

    image = buildGifUrl(pokemon, isShiny)
}

fun calculateEarnedCash(
    percentage: Float,
    wasCaught: Boolean
): Long {
    var cashEarned = (percentage / 100 * R.maxCaughtCash).roundToLong()

    if (wasCaught) cashEarned *= 2

    val modDiff = cashEarned % 100

    when {
        (modDiff in 0..24) -> cashEarned -= modDiff
        (modDiff in 25..74) -> cashEarned += 50 - modDiff
        (modDiff in 75..99) -> cashEarned += 100 - modDiff
    }


    return cashEarned
}

fun buildEncounter(pokemonList: PokemonList) = conversation {
    val pokemon = pokemonList.randomPokemon()

    val isShiny = Random.nextInt(0, 4096) == 0

    val suppliedName = prompt(AnyArg) {
        buildEncounterEmbed(
            user.asMember(channel.data.guildId.value!!),
            pokemon,
            isShiny
        )
    }

    val onlyLettersEng = pokemon.name.keepLetters()
    val onlyLettersGer = pokemon.germanName.keepLetters()
    val onlyLettersSupplied = suppliedName.keepLetters()

    val scoreEng = calculateScore(onlyLettersEng, onlyLettersSupplied)
    val scoreGer = calculateScore(onlyLettersGer, onlyLettersSupplied)

    val percentageEng = calculatePercentage(scoreEng, onlyLettersEng.length)
    val percentageGer = calculatePercentage(scoreGer, onlyLettersGer.length)

    val score: Int
    val percentage: Float
    val length: Int

    // Prefer the German name, hence > and not >=
    if (percentageEng > percentageGer) {
        score = scoreEng
        percentage = percentageEng
        length = onlyLettersEng.length
    } else {
        score = scoreGer
        percentage = percentageGer
        length = onlyLettersGer.length
    }

    val wasCaught = Random.nextFloat() * 100f <= percentage

    val bothNames = pokemon.germanName +
            if (pokemon.name != pokemon.germanName) " (${pokemon.name})"
            else ""

    val earnedCash = calculateEarnedCash(percentage, wasCaught)

    channel.deleteMessage(previousBotMessageId)

    transaction {
        EconomyUser.find { EconomyTable.userId eq user.id.value }
            .forEach { it.cash += earnedCash }
    }

    respond {
        buildResponseEmbed(
            pokemon,
            bothNames,
            score,
            length,
            percentage,
            wasCaught,
            isShiny
        )
    }
}