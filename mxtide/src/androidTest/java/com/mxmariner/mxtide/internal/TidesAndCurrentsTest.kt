package com.mxmariner.mxtide.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mxmariner.mxtide.api.MXTideFactory.createTidesAndCurrents
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TidesAndCurrentsTest {

  private lateinit var subject: TidesAndCurrents

  @Before
  fun beforeEach() {
    subject = createTidesAndCurrents() as TidesAndCurrents
    subject.addHarmonicsFile(InstrumentationRegistry.getInstrumentation().targetContext, "harmonics_dwf_20190620_free_tcd")
  }

  @Test
  fun addStationCount() {
    assertTrue(subject.stationCount > 0)
  }

  @Test
  fun getStationNames() {
    val names = subject.stationNames
    assertTrue(names.isNotEmpty())
  }

  @Test
  fun findStationByName() {
    val tideStation = subject.findStationByName("Seattle, Puget Sound, Washington", StationType.TIDES)
    assertNotNull(tideStation)
    assertEquals("Seattle, Puget Sound, Washington", tideStation?.name)
    assertEquals(47.60264, tideStation?.latitude)
    assertEquals(-122.33931, tideStation?.longitude)
    assertEquals(tideStation?.type, StationType.TIDES)

    val currentStation = subject.findStationByName("Harbor Island East (Depth 42.3ft), Washington Current", StationType.CURRENTS)
    assertNotNull(currentStation)
    assertEquals("Harbor Island East (Depth 42.3ft), Washington Current", currentStation?.name)
    assertEquals(47.58845, currentStation?.latitude)
    assertEquals(-122.34397, currentStation?.longitude)
    assertEquals(currentStation?.type, StationType.CURRENTS)
  }

  @Test
  fun findNearestStation() {
    val tideStation = subject.findNearestStation(47.603962, -122.33071, StationType.TIDES)
    assertNotNull(tideStation)
    assertEquals("Seattle, Puget Sound, Washington", tideStation?.name)
    assertEquals(47.60264, tideStation?.latitude)
    assertEquals(-122.33931, tideStation?.longitude)
    assertEquals(tideStation?.type, StationType.TIDES)

    val currentStation = subject.findNearestStation(47.603962, -122.33071, StationType.CURRENTS)
    assertNotNull(currentStation)
    assertEquals("Harbor Island East (Depth 42.3ft), Washington Current", currentStation?.name)
    assertEquals(47.58845, currentStation?.latitude)
    assertEquals(-122.34397, currentStation?.longitude)
    assertEquals(currentStation?.type, StationType.CURRENTS)
  }

  @Test
  fun findStationsInCircle() {
    val tideStations = subject.findStationsInCircle(47.0, -122.0, 100000.0,
        MeasureUnit.METRIC, StationType.TIDES)
    assertTrue(tideStations.isNotEmpty())

    val currentStations = subject.findStationsInCircle(47.0, -122.0, 100000.0,
        MeasureUnit.METRIC, StationType.CURRENTS)
    assertTrue(currentStations.isNotEmpty())
  }

  @Test
  fun findStatiosnInBounds() {
    val tideStations = subject.findStationsInBounds(49.0, -117.0, 45.4, -125.0, StationType.TIDES)
    assertTrue(tideStations.isNotEmpty())

    val currentStations = subject.findStationsInBounds(49.0, -117.0, 45.4, -125.0, StationType.CURRENTS)
    assertTrue(currentStations.isNotEmpty())
  }
}
