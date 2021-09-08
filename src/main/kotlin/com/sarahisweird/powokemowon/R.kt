package com.sarahisweird.powokemowon

object R {
    const val startingCash = 2500L
    const val maxCaughtCash = 2500L

    const val onPrivateMessage = "Dieser Befehl kann nur auf einem Server ausgeführt werden!"
    const val notLoaded = "Die Daten wurden noch nicht fertig geladen." +
            " Bitte versuche es nochmal in einer Minute!"
    const val welcomeMessage = "Willkommen! Da dies das erste mal ist," +
            " dass du einen Befehl ausführst, bekommst du $startingCash ₽!"

    const val ownerId = 116927399760756742L

    object Cooldowns {
        const val catch = 60L
        const val simp = 60L
        const val freeRealEstate = 120L
        const val daily = 86400L
        const val test = 90L
        const val work = 120L
    }

    object RewardRanges {
        val simp = 1500L..2000L
        val freeRealEstate = 2000L..2500L
        val daily = 10000L..15000L
        val test = 1500L..2000L
        val work = 2000..2500L
    }
}