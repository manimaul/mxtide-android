package com.mxmariner.mxtide.internal

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.mxmariner.andxtidelib.R
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
        subject.addHarmonicsFile(InstrumentationRegistry.getTargetContext(), R.raw.harmonics_dwf_20161231_free_tcd)
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
        subject.stationNames.map {
            Pair(it, subject.findStationByName(it))
        }.forEach {
            assertNotNull("${it.first} was null", it.second)
        }
    }

    @Test
    fun findNearestStation() {
        val tideStation = subject.findNearestStation(47.603962, -122.33071, StationType.TIDES)
        assertNotNull(tideStation)
        assertEquals("Seattle, Puget Sound, Washington", tideStation?.name)
        assertEquals(47.6026, tideStation?.latitude)
        assertEquals(-122.3393, tideStation?.longitude)
        assertEquals(tideStation?.type, StationType.TIDES)

        val currentStation = subject.findNearestStation(47.603962, -122.33071, StationType.CURRENTS)
        assertNotNull(currentStation)
        assertEquals("Off Pleasant Beach, Rich Passage, Puget Sound, Washington Current", currentStation?.name)
        assertEquals(47.58333, currentStation?.latitude)
        assertEquals(-122.53333, currentStation?.longitude)
        assertEquals(currentStation?.type, StationType.CURRENTS)
    }

    @Test
    fun findStationsInCircle() {
        val tideStations = subject.findStationsInCircle(47.0, -122.0, 100000.0,
                MeasureUnit.METERS, StationType.TIDES)
        assertTrue(tideStations.isNotEmpty())

        val currentStations = subject.findStationsInCircle(47.0, -122.0, 100000.0,
                MeasureUnit.METERS, StationType.CURRENTS)
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