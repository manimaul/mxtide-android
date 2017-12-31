package com.mxmariner.mxtide.internal

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.mxmariner.andxtidelib.R
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.mxtide.api.createTidesAndCurrents
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StationTest {

    private lateinit var subject : Station

    @Before
    fun beforeEach() {
        val tnc = createTidesAndCurrents()
        tnc.addHarmonicsFile(InstrumentationRegistry.getTargetContext(), R.raw.harmonics_dwf_20161231_free_tcd)
        subject = tnc.findStationByName("Seattle, Puget Sound, Washington") as Station
    }

    @Test
    fun getLatitude() {
        assertEquals(47.6026, subject.latitude, 0.0000001)
    }

    @Test
    fun getLongitude() {
        assertEquals(-122.33929999999999, subject.longitude, 0.0000001)
    }

    @Test
    fun getTimeZone() {
        assertEquals(DateTimeZone.forID("America/Los_Angeles"), subject.timeZone)
    }

    @Test
    fun getName() {
        assertEquals("Seattle, Puget Sound, Washington", subject.name)
    }

    @Test
    fun getType() {
        assertEquals(StationType.TIDES, subject.type)
    }

    @Test
    fun getPredictionRaw() {
        val duration = Duration.standardHours(24)
        val timeZone = DateTimeZone.forID("America/Los_Angeles")
        val date = DateTime(2017, 12, 17, 14,
                56,0, timeZone)
        val prediction = subject.getPredictionRaw(date, duration, MeasureUnit.FEET)
        assertTrue(prediction.isNotEmpty())
        assertEquals(timeZone, prediction.first().date.zone)
    }

    @Test
    fun getPredictionPlain() {
        val duration = Duration.standardHours(24)
        val timeZone = DateTimeZone.forID("America/Los_Angeles")
        val date = DateTime(2017, 12, 17, 14,
                56,0, timeZone)
        val prediction = subject.getPredictionPlain(date, duration, MeasureUnit.FEET)
        assertTrue(prediction.isNotEmpty())
        assertEquals(timeZone, prediction.first().date.zone)
    }

    @Test
    fun getPredictionClockSVG() {
        val duration = Duration.standardHours(24)
        val date = DateTime(2017, 12, 17, 14,
                56,0, DateTimeZone.forID("America/Los_Angeles"))
        val prediction = subject.getPredictionClockSVG(date, duration, MeasureUnit.FEET)
        assertTrue(prediction.isNotEmpty())
    }

}