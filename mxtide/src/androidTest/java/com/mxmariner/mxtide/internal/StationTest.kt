package com.mxmariner.mxtide.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mxmariner.andxtidelib.R
import com.mxmariner.mxtide.api.MXTideFactory.createTidesAndCurrents
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StationTest {

  private lateinit var subject: Station

  @Before
  fun beforeEach() {
    val tnc = createTidesAndCurrents()
    tnc.addHarmonicsFile(InstrumentationRegistry.getInstrumentation().targetContext, "harmonics_dwf_20190620_free_tcd")
    subject = tnc.findStationByName("Seattle, Puget Sound, Washington", StationType.TIDES) as Station
  }

  @Test
  fun getLatitude() {
    assertEquals(47.60264, subject.latitude, 0.0)
  }

  @Test
  fun getLongitude() {
    assertEquals(-122.33931, subject.longitude, 0.0)
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
        56, 0, timeZone)
    val prediction = subject.getPredictionRaw(date, duration, MeasureUnit.STATUTE)
    assertTrue(prediction.isNotEmpty())
    assertEquals(timeZone, prediction.first().date.zone)
  }
}
