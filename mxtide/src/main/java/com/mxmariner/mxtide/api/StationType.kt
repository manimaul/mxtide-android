package com.mxmariner.mxtide.api

fun stationTypeFromString(input: String?) : StationType? {
    return when(input?.toLowerCase()) {
        "tides" -> StationType.TIDES
        "currents" -> StationType.CURRENTS
        else -> null
    }
}

enum class StationType {
    TIDES,
    CURRENTS
}