package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

val DateTime.unixTimeHours: Float
    get() = (this.millis / 1000 / 60 / 60).toFloat()

operator fun DateTime.minus(start: DateTime) : DateTime {
    return this.minus(start.millis)
}

fun DateTime.inBetween(other: DateTime) : DateTime {
    val delta = (maxOf(other.millis, millis) - minOf(other.millis, millis)) / 2
    return if (other.millis > millis) this.plus(delta) else this.minus(delta)
}


fun Float.hoursToDateTime(dateTimeZone: DateTimeZone): DateTime {
    return DateTime((this.toLong() * 1000 * 60 * 60), dateTimeZone)
}