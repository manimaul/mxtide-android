package com.mxmariner.tides.extensions

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Assert.*
import org.junit.Test

const val oneHrMillis: Long = 60 * 60 * 1000 //3600000

class DateTime_MxKtTest {

    @Test
    fun differenceHoursMinutes() {
        val dt = DateTime(oneHrMillis)
        assertEquals(0.0F, dt.differenceHoursMinutes(start = DateTime(dt)))
        assertEquals(0.05F, dt.differenceHoursMinutes(start = dt.minusMinutes(3)))
        assertEquals(0.1F, dt.differenceHoursMinutes(start = dt.minusMinutes(6)))
        assertEquals(0.08F, dt.differenceHoursMinutes(start = dt.minusMinutes(5)))
        assertEquals(0.25F, dt.differenceHoursMinutes(start = dt.minusMinutes(15)))
        assertEquals(0.5F, dt.differenceHoursMinutes(start = dt.minusMinutes(30)))
        assertEquals(1.0F, dt.differenceHoursMinutes(start = dt.minusMinutes(60)))

        assertEquals(-0.5F, dt.differenceHoursMinutes(start = dt.plusMinutes(30)))
        assertEquals(-1.0F, dt.differenceHoursMinutes(start = dt.plusMinutes(60)))
    }

    @Test
    fun withinOneMinute() {
        val dt = DateTime(oneHrMillis)
        assertTrue(dt.withinOneMinute(DateTime(dt)))
        assertTrue(dt.withinOneMinute(dt.minusMinutes(1)))
        assertTrue(dt.withinOneMinute(dt.plusMinutes(1)))

        assertFalse(dt.withinOneMinute(null))
        assertFalse(dt.withinOneMinute(dt.minusMinutes(2)))
        assertFalse(dt.withinOneMinute(dt.plusMinutes(2)))
    }

    @Test
    fun minus() {
        val dt = DateTime(oneHrMillis, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.minus(DateTime(oneHrMillis, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(0L, newDt.millis)
        assertEquals(DateTimeZone.forID("America/Nassau"), newDt.zone)
    }

    @Test
    fun inBetween2() {
        val dt = DateTime(oneHrMillis * 2, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.inBetween(DateTime(oneHrMillis, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(60 * 60 * 1500, newDt.millis)
        assertEquals(DateTimeZone.forID("America/Nassau"), newDt.zone)
    }

    @Test
    fun inBetween1() {
        val dt = DateTime(oneHrMillis, DateTimeZone.forID("America/Nassau"))
        val newDt = dt.inBetween(DateTime(oneHrMillis * 2, DateTimeZone.forID("America/Los_Angeles")))
        assertEquals(60 * 60 * 1500, newDt.millis)
        assertEquals(DateTimeZone.forID("America/Nassau"), newDt.zone)
    }
}