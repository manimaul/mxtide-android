package com.mxmariner.andxtidelib.remote

import android.os.Parcel
import android.os.Parcelable

class RemoteStation(val stationId: Long,
                    val latitude: Double,
                    val longitude: Double,
                    val type: StationType) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readParcelable(StationType::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(stationId)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeParcelable(type, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteStation> {
        override fun createFromParcel(parcel: Parcel): RemoteStation {
            return RemoteStation(parcel)
        }

        override fun newArray(size: Int): Array<RemoteStation?> {
            return arrayOfNulls(size)
        }
    }
}
