package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.IStationPrediction
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.mxtide.internal.extensions.unixTimeSeconds
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration

internal class Station(private val nativePtr: Long) : IStation {
    companion object {
        init {
            System.loadLibrary("mxtide")
        }

        // JNI Functions implemented in JniStation.cpp

        @JvmStatic external fun latitude(nativePtr: Long): Double
        @JvmStatic external fun longitude(nativePtr: Long): Double
        @JvmStatic external fun timeZone(nativePtr: Long): String
        @JvmStatic external fun name(nativePtr: Long): String
        @JvmStatic external fun type(nativePtr: Long): String

        @JvmStatic external fun getPredictionRaw(nativePtr: Long,
                                                 epoch: Long,
                                                 duration: Long,
                                                 measureUnit: String): List<IStationPrediction<Float>>

        @JvmStatic external fun deleteStation(ptr: Long)
    }

    override val latitude: Double
        get() = latitude(nativePtr)
    override val longitude: Double
        get() = longitude(nativePtr)
    override val timeZone: DateTimeZone
        get() {
            val id = timeZone(nativePtr)
            return DateTimeZone.forID(id)
        }
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

    override fun getPredictionRaw(date: DateTime,
                                  duration: Duration,
                                  measureUnit: MeasureUnit): List<IStationPrediction<Float>> {
        return getPredictionRaw(nativePtr, date.unixTimeSeconds, duration.standardSeconds, measureUnit.toString())
    }

    @Suppress("unused")
    fun finalize() {
        deleteStation(nativePtr)
    }

    override fun toString(): String {
        return "Station: $name : $type - $latitude, $longitude $timeZone\n"
    }

}
