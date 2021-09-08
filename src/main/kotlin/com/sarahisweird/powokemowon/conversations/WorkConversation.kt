package com.sarahisweird.powokemowon.conversations

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.utils.addBalance
import dev.kord.core.entity.Member
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.conversations.conversation
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

fun workConversation(member: Member) = conversation {
    val a = Random.nextInt(100..1000)
    val b = Random.nextInt(99 until a)

    val isPlus = Random.nextBoolean()
    val op: Int.(Int) -> Int = if (isPlus) Int::plus else Int::minus

    val result = a.op(b)

    val userResponse = prompt(
        IntegerArg,
        "Was ist **$a ${if (isPlus) "+" else "-"} $b**, ${member.displayName}?"
    )

    if (userResponse != result) {
        respond("Tut mir leid ${member.displayName}," +
                " aber die richtige Antwort wäre **$result** gewesen!")
        return@conversation
    }

    val reward = Random.nextLong(R.RewardRanges.work)

    addBalance(user.id, reward)

    respond("Deine Antwort war korrekt! Für deine Anstrengungen bekommst du $reward ₽," +
            " ${member.displayName}.")
}