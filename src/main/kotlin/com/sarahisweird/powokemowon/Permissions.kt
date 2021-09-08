package com.sarahisweird.powokemowon

import me.jakejmattson.discordkt.api.dsl.PermissionContext
import me.jakejmattson.discordkt.api.dsl.PermissionSet

enum class Permissions : PermissionSet {
    BOT_OWNER {
        override suspend fun hasPermission(context: PermissionContext) =
            context.user.id.value == R.ownerId
    },

    EVERYONE {
        override suspend fun hasPermission(context: PermissionContext) =
            true
    }
}