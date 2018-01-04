package com.mxmariner.mxtide.internal

import android.content.Context
import android.support.annotation.RawRes
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import java.io.File

internal class TidesAndCurrents : ITidesAndCurrents {

    companion object {
        init {
            System.loadLibrary("jmxtide")
        }

        @JvmStatic external fun create(): Long
        @JvmStatic external fun delete(ptr: Long)
        @JvmStatic external fun addHarmonicsFile(ptr: Long, path: String)
        @JvmStatic external fun stationCount(ptr: Long): Int
        @JvmStatic external fun stationNames(ptr: Long): List<String>
        @JvmStatic external fun findStationByName(ptr: Long,
                                                  name: String): Long

        @JvmStatic external fun findNearestStation(ptr: Long,
                                                   lat: Double,
                                                   lng: Double,
                                                   type: String): Long

        @JvmStatic external fun findStationsInCircle(ptr: Long,
                                                     centerLat: Double,
                                                     centerLng: Double,
                                                     radius: Double,
                                                     type: String): List<Long>?

        @JvmStatic external fun findStationsInBounds(ptr: Long,
                                                     northLat: Double,
                                                     eastLng: Double,
                                                     southLat: Double,
                                                     westLng: Double,
                                                     type: String): List<Long>?
    }

    private val nativePtr: Long = create()

    override fun addHarmonicsFile(context: Context, @RawRes resId: Int) {
        addHarmonicsFile(context.rawResourceAsCacheFile(resId))
    }

    override fun addHarmonicsFile(file: File) {
        addHarmonicsFile(nativePtr, file.absolutePath)
    }

    override val stationCount: Int
        get() = stationCount(nativePtr)

    override val stationNames: List<String>
        get() = stationNames(nativePtr)

    override fun findStationByName(name: String?): IStation? {
        return name?.let {
            findStationByName(nativePtr, it).takeIf {
                it != 0L
            }?.let {
                Station(it)
            }
        }
    }

    override fun findNearestStation(lat: Double,
                                    lng: Double,
                                    type: StationType): IStation? {
        return findNearestStation(nativePtr, lat, lng, type.nativeStringValue).takeUnless {
            it == 0L
        }?.let {
            Station(it)
        }
    }

    override fun findStationsInCircle(lat: Double,
                                      lng: Double,
                                      radius: Double,
                                      type: StationType): List<IStation> {
        return findStationsInCircle(nativePtr, lat, lng, radius, type.nativeStringValue)?.map {
            Station(it)
        } ?: emptyList()
    }

    override fun findStationsInBounds(northLat: Double,
                                      eastLng: Double,
                                      southLat: Double,
                                      westLng: Double,
                                      type: StationType): List<IStation> {
        return emptyList()
    }

    @Suppress("unused")
    fun finalize() {
        delete(nativePtr)
    }
}