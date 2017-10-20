package com.mxmariner.andxtidelib

import com.mxmariner.andxtidelib.remote.StationType

/**
 * @param xtideStr ex "some station name;45.243829;-122.193847;current"
 * "some station name;45.243829;-122.193847;tide;
 */
class Station(xtideStr: String, val id: Long = -1) {
    val name: String
    var type: StationType
    val position: MXLatLng

    init {
        val data = xtideStr.split(";")
        name = data[0].trim { it <= ' ' }
        position = MXLatLng(0, 0)
        position.setLatitudeParseString(data[1])
        position.setLongitudeParseString(data[2])
        if (data[3] == "current") {
            this.type = StationType.STATION_TYPE_CURRENT
        } else {
            this.type = StationType.STATION_TYPE_TIDE
        }
    }
}
