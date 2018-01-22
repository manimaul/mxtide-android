package com.mxmariner.mxtide.api

import android.content.Context
import android.support.annotation.RawRes
import java.io.File

interface ITidesAndCurrents {

    /**
     * Add the stations from a harmonics file raw resource
     *
     * @param context the application [Context]
     * @param resId the raw resource id of the harmonics file
     */
    fun addHarmonicsFile(context: Context, @RawRes resId: Int)

    /**
     * Add the stations from a harmonics file
     *
     * @param file the tide harmonics file
     */
    fun addHarmonicsFile(file: File)

    /**
     * The number of tide and current stations
     */
    val stationCount: Int

    /**
     * The names of all of the tide and current stations
     */
    val stationNames: List<String>

    /**
     * Find a station by it's name
     *
     * @param name the name of the tide or current station
     * @return the station of null if one with the supplied name does not exist
     */
    fun findStationByName(name: String?): IStation?

    /**
     * Find a station nearest to a position
     *
     * @param lat your latitude
     * @param lng your longitude
     * @param type the type of the station to find
     * @return the station closest to the provided position or null if no stations were found
     */
    fun findNearestStation(lat: Double, lng: Double, type: StationType): IStation?

    /**
     * Find the nearest stations sorted by distance
     *
     * @param lat your latitude
     * @param lng your longitude
     * @param type the type of the stations to find
     * @param limit (optional) the maximum number of stations to return
     * @return the list of closest stations sorted by distance to the provided position
     */
    fun findNearestStations(lat: Double, lng: Double, type: StationType, limit: Int? = 0): List<IStation>

    /**
     * Find stations residing in a geographic circle
     *
     * @param lat your latitude
     * @param lng your longitude
     * @param radius the circle radius
     * @param measureUnit the radius measurement
     * @param type the type of the station to find
     * @return the stations within the specified geographic circle
     */
    fun findStationsInCircle(lat: Double,
                             lng: Double,
                             radius: Double,
                             measureUnit: MeasureUnit,
                             type: StationType): List<IStation>

    /**
     * Find stations residing within a geographic bounding box
     *
     * @param northLat the bounding box top
     * @param eastLng the bounding box right
     * @param southLat the bounding box bottom
     * @param westLng the bounding box left
     * @param type the type of the station to find
     * @return the stations within the specified geographic bounds
     */
    fun findStationsInBounds(northLat: Double,
                             eastLng: Double,
                             southLat: Double,
                             westLng: Double,
                             type: StationType): List<IStation>
}
