package com.mxmariner.andxtidelib.remote

import android.os.Parcel
import android.os.Parcelable


enum class StationType constructor(val typeStr: String) : Parcelable {
    STATION_TYPE_TIDE("tide"),
    STATION_TYPE_CURRENT("current");

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(typeStr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StationType> {

        @JvmStatic
        fun typeWithString(type: String): StationType? {
            return values().find {
                it.typeStr.equals(type, ignoreCase = true)
            }
        }

        override fun createFromParcel(parcel: Parcel): StationType {
            return typeWithString(parcel.readString())!!
        }

        override fun newArray(size: Int): Array<StationType?> {
            return arrayOfNulls(size)
        }
    }


}
