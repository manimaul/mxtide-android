package com.mxmariner.andxtidelib.remote;

import android.os.Parcel;
import android.os.Parcelable;

public enum UnitType implements Parcelable {
    METERS,
    FEET;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<UnitType> CREATOR = new Creator<UnitType>() {
        @Override
        public UnitType createFromParcel(final Parcel source) {
            return UnitType.values()[source.readInt()];
        }

        @Override
        public UnitType[] newArray(final int size) {
            return new UnitType[size];
        }
    };
}
