package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.IStationPrediction
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import java.time.Duration
import java.util.*

internal class Station(private val nativePtr: Long) : IStation {


    companion object {
        init {
            System.loadLibrary("mxtide")
        }

        @JvmStatic external fun latitude(nativePtr: Long): Double
        @JvmStatic external fun longitude(nativePtr: Long): Double
        @JvmStatic external fun timeZone(nativePtr: Long): String
        @JvmStatic external fun name(nativePtr: Long): String
        @JvmStatic external fun stationLocalTime(nativePtr: Long, epoch: Long): String
        @JvmStatic external fun type(nativePtr: Long): String
        @JvmStatic external fun deleteStation(ptr: Long)
    }

    override val latitude: Double
        get() = latitude(nativePtr)
    override val longitude: Double
        get() = longitude(nativePtr)
    override val timeZone: String
        get() = timeZone(nativePtr)
    override val name: String
        get() = name(nativePtr)
    override val type: StationType
        get() {
            return when (type(nativePtr)) {
                "tide" -> StationType.TIDES
                "current" -> StationType.CURRENTS
                else -> throw RuntimeException("invalid station type")
            }
        }

    override fun getStationLocalTime(date: Date) : String {
        val unixTimeSeconds = date.time / 1000
        return stationLocalTime(nativePtr, unixTimeSeconds)
    }

    override fun getPredictionRaw(date: Date, duration: Duration, measureUnit: MeasureUnit): IStationPrediction<Float> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPredictionPlain(date: Date, duration: Duration, measureUnit: MeasureUnit): IStationPrediction<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPredictionClockSVG(date: Date, duration: Duration, measureUnit: MeasureUnit): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Suppress("unused")
    fun finalize() {
        deleteStation(nativePtr)
    }

}
