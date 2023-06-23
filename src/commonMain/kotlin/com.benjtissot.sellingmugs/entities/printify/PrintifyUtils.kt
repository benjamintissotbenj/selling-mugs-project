package com.benjtissot.sellingmugs.entities.printify

import kotlinx.datetime.*
import kotlinx.datetime.Month


fun customParseToInstant(isoString: String) : Instant {
    return Instant.parse(isoString.replace(" ", "T"))
}

fun Instant.toPrettyFormat() : String {
    val localDT: LocalDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDT.toPrettyFormat()
}

fun LocalDateTime.toPrettyFormat() : String {
    return "${dayOfMonth.twoDigits()} ${month.code()} $year at ${hour.twoDigits()}:${minute.twoDigits()}:${second.twoDigits()}"
}

fun Int.twoDigits() : String {
    return if (this < 10){
        "0$this"
    } else {
        "$this"
    }
}

fun Month.code() : String {
    return when (this) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "May"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Aug"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Dec"
        else -> "Jan"
    }
}