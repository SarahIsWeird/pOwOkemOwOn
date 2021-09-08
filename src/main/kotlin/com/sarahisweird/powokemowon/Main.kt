package com.sarahisweird.powokemowon

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sarahisweird.powokemowon.data.PokemonList
import com.sarahisweird.powokemowon.db.Database
import com.sarahisweird.powokemowon.utils.getResource
import dev.kord.common.annotation.KordPreview
import me.jakejmattson.discordkt.api.dsl.bot
import java.io.File

var pokemonList: PokemonList? = null

@OptIn(KordPreview::class)
fun main() {
    val token = System.getenv("powokemowon_token")

    val csvResource = getResource("/pokemon.csv") ?: error("Couldn't find the csv file.")
    val csvFile = File(csvResource.toURI())

    val csv2Resource = getResource("/pokemon2.csv") ?: error("Couldn't find the second csv file.")
    val csv2File = File(csv2Resource.toURI())

    csvReader().open(csvFile) {
        val data = readAllWithHeaderAsSequence()

        pokemonList = PokemonList(data, csvReader().readAllWithHeader(csv2File))

        println("Loaded Pokemon data.")
    }

    val db = Database.db

    println("Successfully connected to database at ${db.url} (${db.dialect.name}).")

    bot(token) {
        prefix { "p!" }

        configure {
            permissions(commandDefault = Permissions.EVERYONE)
        }
    }
}