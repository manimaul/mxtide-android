package com.mxmariner.andxtidelib.remote

import android.os.Parcel
import android.os.Parcelable

enum class UnitType constructor(private val typeStr: String) : Parcelable {

    METERS("Meters"),
    FEET("Feet");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(typeStr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UnitType> {

        @JvmStatic
        fun typeWithString(type: String): UnitType? {
            return values().find {
                it.typeStr.equals(type, ignoreCase = true)
            }
        }

        override fun createFromParcel(parcel: Parcel): UnitType {
            return typeWithString(parcel.readString())!!
        }

        override fun newArray(size: Int): Array<UnitType?> {
            return arrayOfNulls(size)
        }
    }

}
