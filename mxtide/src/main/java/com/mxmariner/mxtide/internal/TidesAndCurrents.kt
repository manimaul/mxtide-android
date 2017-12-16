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
            System.loadLibrary("mxtide")
        }

        @JvmStatic external fun create(): Long
        @JvmStatic external fun delete(ptr: Long)
        @JvmStatic external fun addHarmonicsFile(ptr: Long, path: String)
        @JvmStatic external fun stationCount(ptr: Long): Int
        @JvmStatic external fun stationNames(ptr: Long): List<String>
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
        return null
    }

    override fun findNearestStation(lat: Double,
                                    lng: Double,
                                    type: StationType): IStation? {
        return null
    }

    override fun findStationInBounds(northLat: Double,
                                     southLat: Double,
                                     eastLng: Double,
                                     westLng: Double,
                                     type: StationType): List<IStation> {
        return emptyList()
    }

    @Suppress("unused")
    fun finalize() {
        delete(nativePtr)
    }
}