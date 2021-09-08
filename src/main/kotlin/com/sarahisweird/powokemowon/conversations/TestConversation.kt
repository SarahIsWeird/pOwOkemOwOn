package com.sarahisweird.powokemowon.conversations

import com.sarahisweird.powokemowon.R
import com.sarahisweird.powokemowon.utils.addBalance
import com.sarahisweird.powokemowon.utils.intersperseZWS
import com.sarahisweird.powokemowon.utils.randomAlphaNumericString
import dev.kord.core.entity.Member
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.conversations.conversation
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

suspend fun testConversation(member: Member) = conversation {
    val length = Random.nextInt(5..10)
    val sequence = randomAlphaNumericString(length)

    val userResponse = prompt(
        AnyArg,
        "Bist du ein Roboter, ${member.displayName}? Tippe diese Zeichen ab! **${sequence.intersperseZWS()}**"
    )

    if (userResponse != sequence) {
        respond("Du bist also ein Roboter? :O Kein Geld für dich!")
        return@conversation
    }

    val reward = Random.nextLong(R.RewardRanges.test)

    addBalance(user.id, reward)

    respond("Du bist kein Roboter! Gut gemacht. Hier sind $reward ₽, ${member.displayName}!")
}