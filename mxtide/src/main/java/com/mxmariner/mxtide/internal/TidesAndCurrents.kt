package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import java.io.File

internal class TidesAndCurrents : ITidesAndCurrents {

    override fun addHarmonicsFile(file: File) {
    }

    override val stationCount: Int
        get() = 0
    override val stationNames: List<String>
        get() = emptyList()

    override fun findStationByName(name: String?) : IStation? {
        return null
    }

    override fun findNearestStation(lat: Double, lng: Double, type: StationType) : IStation? {
        return null
    }

    override fun findStationInBounds(northLat: Double, eastLng: Double, westLng: Double, type: StationType) : List<IStation> {
        return emptyList()
    }

}