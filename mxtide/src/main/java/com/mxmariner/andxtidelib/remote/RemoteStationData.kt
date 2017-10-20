package com.mxmariner.andxtidelib.remote

import android.os.Parcel
import android.os.Parcelable

const val REQUEST_OPTION_PLAIN_DATA = 2
const val REQUEST_OPTION_RAW_DATA = 4
const val REQUEST_OPTION_PREDICTION = 8
const val REQUEST_OPTION_ABOUT = 16
const val REQUEST_OPTION_GRAPH_SVG = 32
const val REQUEST_OPTION_CLOCK_SVG = 64

class RemoteStationData(val id: Long,
                        val name: String,
                        val dataTimeStamp: String,
                        val latitude: Double,
                        val longitude: Double,
                        val stationType: StationType,
                        var optionalPlainData: Array<String>? = null,
                        var optionalRawData: Array<String>? = null,
                        var optionalPrediction: String? = null,
                        var optionalAbout: String? = null,
                        var optionalGraphSvg: String? = null,
                        var optionalClockSvg: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readParcelable(StationType::class.java.classLoader),
            parcel.createStringArray(),
            parcel.createStringArray(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(dataTimeStamp)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeParcelable(stationType, flags)
        parcel.writeStringArray(optionalPlainData)
        parcel.writeStringArray(optionalRawData)
        parcel.writeString(optionalPrediction)
        parcel.writeString(optionalAbout)
        parcel.writeString(optionalGraphSvg)
        parcel.writeString(optionalClockSvg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteStationData> {

        override fun createFromParcel(parcel: Parcel): RemoteStationData {
            return RemoteStationData(parcel)
        }

        override fun newArray(size: Int): Array<RemoteStationData?> {
            return arrayOfNulls(size)
        }
    }
}
