package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class DateTime_MxKtTest {

    @Test
    fun getUnixTimeHours() {
        val dt = DateTime(60 * 60 * 1000)
        assertEquals(dt.unixTimeHours, 1.0f)
    }

    @Test
    fun minus() {
        val dt = DateTime(60 * 60 * 1000, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.minus(DateTime(60 * 60 * 1000, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(newDt.millis, 0L)
        assertEquals(newDt.zone, DateTimeZone.forID("America/Nassau"))
    }

    @Test
    fun inBetween2() {
        val dt = DateTime(60 * 60 * 1000 * 2, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.inBetween(DateTime(60 * 60 * 1000, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(newDt.millis, 60 * 60 * 1500)
        assertEquals(DateTimeZone.forID("America/Nassau"), newDt.zone)
    }

    @Test
    fun inBetween1() {
        val dt = DateTime(60 * 60 * 1000, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.inBetween(DateTime(60 * 60 * 1000 * 2, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(newDt.millis, 60 * 60 * 1500)
        assertEquals(DateTimeZone.forID("America/Nassau"), newDt.zone)
    }

    @Test
    fun hoursToDateTime() {
        val dt = DateTime(60 * 60 * 1000)
        val floatHours = dt.unixTimeHours
    }
}