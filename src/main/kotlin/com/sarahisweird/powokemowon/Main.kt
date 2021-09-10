package com.sarahisweird.powokemowon

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sarahisweird.powokemowon.data.PokemonList
import com.sarahisweird.powokemowon.db.Database
import com.sarahisweird.powokemowon.utils.asResource
import dev.kord.common.annotation.KordPreview
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import me.jakejmattson.discordkt.api.dsl.bot

var pokemonList: PokemonList? = null

@OptIn(KordPreview::class, dev.kord.gateway.PrivilegedIntent::class)
fun main() {
    val token = System.getenv("powokemowon_token")

    val csvResource = "/com/sarahisweird/powokemowon/pokemon.csv".asResource() ?: error("Couldn't find the csv file.")

    val csv2Resource = "/com/sarahisweird/powokemowon/pokemon2.csv".asResource() ?: error("Couldn't find the second csv file.")

    csvReader().open(csvResource) {
        val data = readAllWithHeaderAsSequence()

        pokemonList = PokemonList(data, csvReader().readAllWithHeader(csv2Resource))

        println("Loaded Pokemon data.")
    }

    val db = Database.db

    println("Successfully connected to database at ${db.url} (${db.dialect.name}).")

    bot(token) {
        prefix { "p!" }

        configure {
            permissions(commandDefault = Permissions.EVERYONE)
            intents = Intents.nonPrivileged + Intent.GuildMembers
        }
    }
}