package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.lang.StrictMath.abs
import kotlin.math.round


/**
 *  https://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
 *  Symbol  Meaning                      Presentation  Examples
 *  ------  -------                      ------------  -------
 *  G       era                          text          AD
 *  C       century of era (>=0)         number        20
 *  Y       year of era (>=0)            year          1996
 *
 *  x       weekyear                     year          1996
 *  w       week of weekyear             number        27
 *  e       day of week                  number        2
 *  E       day of week                  text          Tuesday; Tue
 *
 *  y       year                         year          1996
 *  D       day of year                  number        189
 *  M       month of year                month         July; Jul; 07
 *  d       day of month                 number        10
 *
 *  a       halfday of day               text          PM
 *  K       hour of halfday (0~11)       number        0
 *  h       clockhour of halfday (1~12)  number        12
 *
 *  H       hour of day (0~23)           number        0
 *  k       clockhour of day (1~24)      number        24
 *  m       minute of hour               number        30
 *  s       second of minute             number        55
 *  S       fraction of second           millis        978
 *
 *  z       time zone                    text          Pacific Standard Time; PST
 *  Z       time zone offset/id          zone          -0800; -08:00; America/Los_Angeles
 *
 *  '       escape for text              delimiter
 *  ''      single quote                 literal
 */
private val formatDateTime = DateTimeFormat.forPattern("MMM dd yyyy HH:mm") // Jul 23 2018 23:51
private val formatTime = DateTimeFormat.forPattern("HH:mm") // 23:51am

fun DateTime.differenceHoursMinutes(start: DateTime) : Float {
    val delta = this.minus(start)
    val retVal =  (delta.millis / 1000 / 60).toFloat() / 60F
    return round(retVal * 100) / 100
}

operator fun DateTime.minus(start: DateTime) : DateTime {
    return this.minus(start.millis)
}

fun DateTime.inBetween(other: DateTime) : DateTime {
    val delta = (maxOf(other.millis, millis) - minOf(other.millis, millis)) / 2
    return if (other.millis > millis) this.plus(delta) else this.minus(delta)
}

fun DateTime.formatDateTime() : String {
    return formatDateTime.print(this)
}

fun DateTime.formatTime() : String {
    return formatTime.print(this)
}

fun DateTime.withinOneMinute(other: DateTime?) : Boolean {
    return other?.let {
        val diff = abs(millis - it.millis)

        return diff <= 60 * 1000
    } ?: false
}
