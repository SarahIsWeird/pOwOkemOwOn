package com.sarahisweird.powokemowon.commands.preconditions

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.pokemonList
import me.jakejmattson.discordkt.api.dsl.precondition

fun notYetLoaded() = precondition {
    if (pokemonList == null) {
        fail(R.notLoaded)
    }
}