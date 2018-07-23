package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

val DateTime.unixTimeHours: Float
    get() = (this.millis / 1000 / 60 / 60).toFloat()

operator fun DateTime.minus(start: DateTime) : DateTime {
    return this.minus(start.millis)
}

fun DateTime.inBetween(other: DateTime) : DateTime {
    val mx = maxOf(other.millis, millis)
    val mn = minOf(other.millis, millis)
    val delta = mx - mn
    return DateTime(mn + (delta / 2))
}


fun Float.hoursToDateTime(dateTimeZone: DateTimeZone): DateTime {
    return DateTime((this.toLong() * 1000 * 60 * 60), dateTimeZone)
}