package com.sarahisweird.powokemowon.utils

import java.net.URL
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private val validAlphaNumericalCharacters: String =
    ('a'..'z').joinToString("") + ('0'..'9').joinToString("")

fun getResource(path: String): URL? = object {}.javaClass.getResource(path)

fun damerauLevenshtein(str1: String, str2: String): Int {
    val d = Array(str1.length + 1) { Array(str2.length + 1) { 0 } }

    (0..str1.length).forEach { d[it][0] = it }
    (0..str2.length).forEach { d[0][it] = it }

    (1..str1.length).forEach { i ->
        (1..str2.length).forEach { j ->
            val cost = if (str1[i - 1] == str2[j - 1]) 0 else 1

            d[i][j] = minOf(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost)

            if (i > 1 && j > 1 && str1[i - 1] == str2[j - 2] && str1[i - 2] == str2[j - 1])
                d[i][j] = minOf(d[i][j], d[i - 2][j - 2] + 1)
        }
    }

    return d[str1.length][str2.length]
}

fun calculateScore(actualName: String, suppliedName: String)
        = maxOf(0, actualName.length
        - damerauLevenshtein(actualName.lowercase(), suppliedName.lowercase())
)

fun calculatePercentage(score: Int, possible: Int) =
    ((score.toFloat() / possible.toFloat()) * 1000f).roundToInt().toFloat() / 10f

fun String.keepLetters() =
    filter {
        ('A'..'Z').contains(it)
                || ('a'..'z').contains(it)
                || listOf('ä', 'ö', 'ü', 'ß').contains(it)
    }

fun Int.createString(char: Char) =
    (0 until this).joinToString("") { char.toString() }

fun randomAlphaNumericString(length: Int) =
    (0 until length).joinToString("") {
        validAlphaNumericalCharacters.random().toString()
    }

fun String.intersperseZWS() =
    map { it }.joinToString("\u200B")

@OptIn(ExperimentalTime::class)
fun Duration.toGermanString(): String {
    if (inWholeSeconds == 0L) return "unter einer Sekunde"

    val parts = mutableListOf<String>()

    val days = inWholeDays
    val hours = inWholeHours - days * 24
    val minutes = inWholeMinutes - days * 24 * 60 - hours * 60
    val seconds = inWholeSeconds - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60

    if (days > 0)
        parts += "$days Tag${if (days > 1) "en" else ""}"
    if (hours > 0)
        parts += "$hours Stunde${if (hours > 1) "n" else ""}"
    if (minutes > 0)
        parts += "$minutes Minute${if (minutes > 1) "n" else ""}"
    if (seconds > 0)
        parts += "$seconds Sekunde${if (seconds > 1) "n" else ""}"

    return parts.joinToString(" ")
}