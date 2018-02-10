package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

val DateTime.unixTimeHours: Float
    get() = (this.millis / 1000 / 60 / 60).toFloat()

fun Float.hoursToDateTime(dateTimeZone: DateTimeZone): DateTime {
    return DateTime((this.toLong() * 1000 * 60 * 60), dateTimeZone)
}