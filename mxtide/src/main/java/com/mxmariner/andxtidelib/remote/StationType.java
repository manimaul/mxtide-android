package com.mxmariner.andxtidelib.remote;


import android.os.Parcel;
import android.os.Parcelable;

public enum StationType implements Parcelable {
    STATION_TYPE_TIDE("tide"),
    STATION_TYPE_CURRENT("current");

    private String typeStr;

    StationType(String type) {
        typeStr = type;
    }

    public static StationType typeWithString(String type) {
        for (StationType stationType : StationType.values()) {
            if (stationType.typeStr.equalsIgnoreCase(type))
                return stationType;
        }

        return null;
    }

    public String getTypeStr() {
        return typeStr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<StationType> CREATOR = new Creator<StationType>() {
        @Override
        public StationType createFromParcel(final Parcel source) {
            return StationType.values()[source.readInt()];
        }

        @Override
        public StationType[] newArray(final int size) {
            return new StationType[size];
        }
    };
}
