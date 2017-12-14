package com.mxmariner.mxtide.api

import java.io.File

interface ITidesAndCurrents {
    fun addHarmonicsFile(file: File)
    val stationCount: Int
    val stationNames: List<String>
    fun findStationByName(name: String?) : IStation?
    fun findNearestStation(lat: Double, lng: Double, type: StationType) : IStation?
    fun findStationInBounds(northLat: Double, eastLng: Double, westLng: Double, type: StationType) : List<IStation>
}
