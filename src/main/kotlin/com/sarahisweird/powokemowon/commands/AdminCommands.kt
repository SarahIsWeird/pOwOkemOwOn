package com.sarahisweird.powokemowon.commands

import com.sarahisweird.powokemowon.Permissions
import com.sarahisweird.powokemowon.utils.exportDatabaseToSql
import com.sarahisweird.powokemowon.utils.getTabulatedDatabaseResult
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun adminCommands() = commands("Administration") {
    command("sql") {
        description = "Führt einen SQL-Befehl aus."
        requiredPermission = Permissions.BOT_OWNER

        execute(EveryArg) {
            try {
                val result = getTabulatedDatabaseResult(args.first)

                if (result == null) {
                    respond("Der Befehl wurde ausgeführt.")
                } else {
                    respond("Der Befehl wurde ausgeführt:\n\n```$result```")
                }
            } catch (e: ExposedSQLException) {
                respond("Es gab einen Fehler:\n${e.message ?: "Es gab keine Fehlermeldung :("}")
            }
        }
    }

    command("exportDatabase") {
        description = "Exportiert die Datenbank in ein SQL-Befehl."
        requiredPermission = Permissions.BOT_OWNER

        execute {
            try {
                respond(
                    "Hier ist der Befehl, um die Datenbank zu kopieren:\n" +
                            "\n" +
                            "```${exportDatabaseToSql()}```"
                )
            } catch (e: ExposedSQLException) {
                respond(
                    "Es gab einen Fehler beim Exportieren der Datenbank:\n" +
                            "\n" +
                            (e.message ?: "Es gab keine Fehlermeldung :(")
                )
            }
        }
    }
}