package com.sarahisweird.powokemowon.commands.preconditions

import com.sarahisweird.powokemowon.R
import me.jakejmattson.discordkt.api.dsl.precondition

fun isDM() = precondition {
    if (channel.data.guildId.value == null) {
        fail(R.onPrivateMessage)
    }
}