package com.mxmariner.mxtide.api

import java.time.Duration
import java.util.*

interface IStation {
    val latitude: Double
    val longitude: Double
    val timeZone: String
    val name: String
    val timeStamp: String
    val type: StationType
    fun getPredictionRaw(date: Date, duration: Duration, measureUnit: MeasureUnit): IStationPrediction<Float>
    fun getPredictionPlain(date: Date, duration: Duration, measureUnit: MeasureUnit): IStationPrediction<String>
    fun getPredictionClockSVG(date: Date, duration: Duration, measureUnit: MeasureUnit): String?
}
