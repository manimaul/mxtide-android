package com.mxmariner.tides.main.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

internal val DateTime.unixTimeHours: Float
    get() = (this.millis / 1000 / 60 / 60).toFloat()

internal fun Float.hoursToDateTime(dateTimeZone: DateTimeZone): DateTime {
    return DateTime((this.toLong() * 1000 * 60 * 60), dateTimeZone)
}