@file:Suppress("unused")

package com.mxmariner.mxtide.api

import android.location.Location

class LatLng {

    constructor(location: Location? = null) {
        location?.let {
            latitude = it.latitude
            longitude = it.longitude
            altitude = it.altitude
        }
    }

    constructor(lat: Double? = null,
                lng: Double? = null) {
        lat?.let { latitude = it }
        lng?.let { longitude = it }
    }

    constructor(latE6: Int? = null,
                lngE6: Int? = null) {
        latE6?.let { latitudeE6 = it }
        lngE6?.let { longitudeE6 = it }
    }

    var latitude = 0.0
        set(value) {
            field = clip(value, MIN_LATITUDE, MAX_LATITUDE)
        }

    var longitude = 0.0
        set(value) {
            field = clip(value, MIN_LONGITUDE, MAX_LONGITUDE)
        }

    var altitude = 0.0

    val latitudeCardinal: Cardinal
        get() = if (latitude < 0) {
            Cardinal.SOUTH
        } else Cardinal.NORTH

    val longitudeCardinal: Cardinal
        get() = if (longitude < 0) {
            Cardinal.WEST
        } else Cardinal.EAST

    val latitudeDegrees: Int
        get() = getDegrees(Math.abs(latitude))

    val longitudeDegrees: Int
        get() = getDegrees(Math.abs(longitude))

    val latitudeMinutes: Int
        get() = getMinutes(Math.abs(latitude))

    val longitudeMinutes: Int
        get() = getMinutes(Math.abs(longitude))

    val latitudeDecimalDegrees: Double
        get() = getDecimalDegrees(Math.abs(latitude))

    val longitudeDecimalDegrees: Double
        get() = getDecimalDegrees(Math.abs(longitude))

    val latitudeDecimalMinutes: Double
        get() = getDecimalMinutes(Math.abs(latitude))

    val longitudeDecimalMinutes: Double
        get() = getDecimalMinutes(Math.abs(longitude))

    val latitudeDecimalSeconds: Double
        get() = getDecimalSeconds(Math.abs(latitude))

    val longitudeDecimalSeconds: Double
        get() = getDecimalSeconds(Math.abs(longitude))

    var latitudeE6: Int
        get() = (latitude * 1E6).toInt()
        set(latE6) {
            latitude = latE6.toDouble() / 1E6
        }

    var longitudeE6: Int
        get() = (longitude * 1E6).toInt()
        set(lngE6) {
            longitude = lngE6.toDouble() / 1E6
        }

    fun setLatitude(latitude: Double,
                    cardinal: Cardinal) {
        if (Cardinal.SOUTH == cardinal) {
            this.latitude = 0 - Math.abs(latitude)
        } else {
            this.latitude = Math.abs(latitude)
        }
    }

    // set latitude degrees decimal minutes
    fun setLatitude(degrees: Int,
                    minutes: Double,
                    cardinal: Cardinal) {
        var coord = 0.0
        coord += Math.abs(degrees).toDouble()
        coord += Math.abs(minutes) / 60.0
        if (Cardinal.SOUTH == cardinal) {
            this.latitude = 0 - coord
        } else {
            this.latitude = coord
        }
    }

    // set latitude degrees minutes decimal seconds
    fun setLatitude(degrees: Int,
                    minutes: Int,
                    seconds: Double,
                    cardinal: Cardinal) {
        var coord = 0.0
        coord += Math.abs(degrees).toDouble()
        coord += Math.abs(minutes + Math.abs(seconds) / 60.0) / 60.0
        if (Cardinal.SOUTH == cardinal) {
            this.latitude = 0 - coord
        } else {
            this.latitude = coord
        }
    }

    fun setLongitude(longitude: Double,
                     cardinal: Cardinal) {
        if (Cardinal.WEST == cardinal) {
            this.longitude = 0 - Math.abs(longitude)
        } else {
            this.longitude = Math.abs(longitude)
        }
    }

    // set longitude degrees decimal minutes
    fun setLongitude(degrees: Int,
                     minutes: Double,
                     cardinal: Cardinal) {
        var coord = 0.0
        coord += Math.abs(degrees).toDouble()
        coord += Math.abs(minutes) / 60.0
        if (Cardinal.WEST == cardinal) {
            this.longitude = 0 - coord
        } else {
            this.longitude = coord
        }
    }

    // set longitude degrees minutes decimal seconds
    fun setLongitude(degrees: Int,
                     minutes: Int,
                     seconds: Double,
                     cardinal: Cardinal) {
        var coord = 0.0
        coord += Math.abs(degrees).toDouble()
        coord += Math.abs(minutes + Math.abs(seconds) / 60.0) / 60.0
        if (Cardinal.WEST == cardinal) {
            this.longitude = 0 - coord
        } else {
            this.longitude = coord
        }
    }

    // getDegrees
    private fun getDegrees(coordinate: Double): Int {
        return coordinate.toInt()
    }

    // getMinutes
    private fun getMinutes(coordinate: Double): Int {
        return getDecimalMinutes(coordinate).toInt()
    }

    // getDecimalDegrees DDD
    private fun getDecimalDegrees(coordinate: Double): Double {
        return Math.round(coordinate * 1000000).toDouble() / 1000000
    }

    // getDecimalMinutes
    private fun getDecimalMinutes(coordinate: Double): Double {
        return Math
                .round((coordinate - getDegrees(coordinate)) * 60 * 100000).toDouble() / 100000
    }

    // getDecimalSeconds
    private fun getDecimalSeconds(coordinate: Double): Double {
        return Math
                .round((getDecimalMinutes(coordinate) - getMinutes(coordinate)) * 60 * 1000).toDouble() / 1000
    }

    fun bearingTo(endPoint: LatLng): Double {
        return bearingToPoint(this, endPoint)
    }

    fun distanceToPoint(endPoint: LatLng): Double {
        return distanceToPoint(latitude, longitude, endPoint.latitude, endPoint.latitude)
    }

    override fun toString(): String {
        return "latitude: $latitude longitude: $longitude"
    }

    fun setLatitudeParseString(str: String): Boolean {
        return parseString(str, Parallel.LATITUDE)
    }

    fun setLongitudeParseString(str: String): Boolean {
        return parseString(str, Parallel.LONGITUDE)
    }

    private fun parseString(coordinate: String,
                            parallel: Parallel): Boolean {
        return Cardinal.fromCoordinateWord(coordinate, parallel)?.let { cardinal ->
            // remove leading non digits
            var str = coordinate.replace("^\\D+".toRegex(), "")

            // separate digits with commas
            str = str.replace("[^0-9\\.]+".toRegex(), ",")

            val lst = str.split(",")
            when {
                lst.size == 1 -> // ddd
                    return if (parallel == Parallel.LATITUDE) {
                        setLatitude(lst[0].toDouble(), cardinal)
                        true
                    } else {
                        setLongitude(lst[0].toDouble(), cardinal)
                        true
                    }
                lst.size == 2 -> // dmm
                    return if (parallel == Parallel.LATITUDE) {
                        setLatitude(Integer.parseInt(lst[0]),
                                lst[1].toDouble(), cardinal)
                        true
                    } else {
                        setLongitude(Integer.parseInt(lst[0]),
                                lst[1].toDouble(), cardinal)
                        true
                    }
                lst.size == 3 -> // dms
                    return if (parallel == Parallel.LATITUDE) {
                        setLatitude(Integer.parseInt(lst[0]),
                                Integer.parseInt(lst[1]),
                                lst[2].toDouble(), cardinal)
                        true
                    } else {
                        setLongitude(Integer.parseInt(lst[0]),
                                Integer.parseInt(lst[1]),
                                lst[2].toDouble(), cardinal)
                        true
                    }
                else -> false
            }
        } ?: false
    }
}

enum class Parallel {
    LATITUDE,
    LONGITUDE
}

enum class Cardinal(val cardinal: Char, val direction: String) {
    NORTH('N', "north"),
    SOUTH('S', "south"),
    EAST('E', "east"),
    WEST('W', "west");

    companion object {
        fun fromChar(cardinal: Char): Cardinal? {
            return values().find {
                it.cardinal == cardinal
            }
        }

        fun fromDirection(direction: String): Cardinal? {
            return values().find {
                it.direction == direction.toLowerCase()
            }
        }

        fun fromCoordinateWord(word: String, parallel: Parallel): Cardinal? {
            // remove leading and trailing whitespace
            val str = word.trim()

            // see if there is a north south east or west in string
            val cardinal = str.replace("[^A-Za-z]".toRegex(), "").toUpperCase()
            var westOrSouth = cardinal.startsWith("W") || cardinal
                    .startsWith("S")

            // see if there is a leading negative
            if (str.startsWith("-"))
                westOrSouth = true

            return if (parallel == Parallel.LATITUDE) {
                if (westOrSouth) SOUTH else NORTH
            } else {
                if (westOrSouth) WEST else EAST
            }
        }
    }
}

const val RADIUS_EARTH_METERS = 6378137
const val MIN_LATITUDE = -90.0
const val MAX_LATITUDE = 90.0
const val MIN_LONGITUDE = -180.0
const val MAX_LONGITUDE = 180.0
const val DEG2RAD = (Math.PI / 180.0).toFloat()

fun fromCenterBetween(startPoint: LatLng, endPoint: LatLng): LatLng {
    return LatLng((startPoint.latitude + endPoint.latitude) / 2,
            (startPoint.longitude + endPoint.longitude) / 2)
}

/**
 * @return bearing in degrees
 * @see (http://groups.google.com/group/osmdroid/browse_thread/thread/d22c4efeb9188fe9/bc7f9b3111158dd)
 */
fun bearingToPoint(startPoint: LatLng, endPoint: LatLng): Double {
    val lat1 = Math.toRadians(startPoint.latitude)
    val long1 = Math.toRadians(startPoint.longitude)
    val lat2 = Math.toRadians(endPoint.latitude)
    val long2 = Math.toRadians(endPoint.longitude)
    val deltaLong = long2 - long1
    val a = Math.sin(deltaLong) * Math.cos(lat2)
    val b = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong)
    val bearing = Math.toDegrees(Math.atan2(a, b))
    return (bearing + 360) % 360
}

/**
 * Calculate a point that is the specified distance and bearing away from this point.
 */
fun destinationPoint(startPoint: LatLng, aDistanceInMeters: Double, aBearingInDegrees: Float): LatLng {
    return destinationPoint(startPoint.latitude, startPoint.longitude, aDistanceInMeters, aBearingInDegrees)
}

/**
 * Calculate a point that is the specified distance and bearing away from this point.
 *
 * @see (http://www.movable-type.co.uk/scripts/latlong.html)
 * @see (http://www.movable-type.co.uk/scripts/latlon.js)
 */
fun destinationPoint(startLat: Double, startLng: Double, aDistanceInMeters: Double, aBearingInDegrees: Float): LatLng {
    // convert distance to angular distance
    val dist = aDistanceInMeters / RADIUS_EARTH_METERS

    // convert bearing to radians
    val brg = DEG2RAD * aBearingInDegrees

    // get current location in radians
    val lat1 = DEG2RAD * startLat
    val lon1 = DEG2RAD * startLng

    val lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + (Math.cos(lat1)
            * Math.sin(dist) * Math.cos(brg.toDouble())))
    val lon2 = lon1 + Math.atan2(Math.sin(brg.toDouble()) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2))
    val lat2deg = lat2 / DEG2RAD
    val lon2deg = lon2 / DEG2RAD
    return LatLng(lat2deg, lon2deg)
}

/**
 * @return distance in meters
 * @see [GPS Distance](http://www.geocities.com/DrChengalva/GPSDistance.html)
 */
fun distanceToPoint(startLat: Double, startLng: Double, endLat: Double, endLng: Double): Double {
    val a1 = DEG2RAD * startLat
    val a2 = DEG2RAD * startLng
    val b1 = DEG2RAD * endLat
    val b2 = DEG2RAD * endLng
    val cosa1 = Math.cos(a1)
    val cosb1 = Math.cos(b1)
    val t1 = cosa1 * Math.cos(a2) * cosb1 * Math.cos(b2)
    val t2 = cosa1 * Math.sin(a2) * cosb1 * Math.sin(b2)
    val t3 = Math.sin(a1) * Math.sin(b1)
    val tt = Math.acos(t1 + t2 + t3)
    return RADIUS_EARTH_METERS * tt
}

/**
 * Clips a number to the specified minimum and maximum values.
 *
 * @param n        The number to clip
 * @param minValue Minimum allowable value
 * @param maxValue Maximum allowable value
 * @return The clipped value.
 */
fun clip(n: Double, minValue: Double, maxValue: Double): Double {
    return Math.min(Math.max(n, minValue), maxValue)
}
