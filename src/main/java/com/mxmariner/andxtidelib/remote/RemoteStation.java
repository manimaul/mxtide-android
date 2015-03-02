package com.mxmariner.andxtidelib.remote;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class RemoteStation implements Parcelable {
    private final long stationId;
    private final double latitude;
    private final double longitude;
    private final StationType type;

    public RemoteStation(long stationId, double latitude, double longitude, StationType type) {
        this.stationId = stationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public long getStationId() throws RemoteException {
        return stationId;
    }

    public double getLatitude() throws RemoteException {
        return latitude;
    }

    public double getLongitude() throws RemoteException {
        return longitude;
    }

    protected RemoteStation(Parcel in) {
        stationId = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        type = in.readParcelable(StationType.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(stationId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(type, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RemoteStation> CREATOR = new Parcelable.Creator<RemoteStation>() {
        @Override
        public RemoteStation createFromParcel(Parcel in) {
            return new RemoteStation(in);
        }

        @Override
        public RemoteStation[] newArray(int size) {
            return new RemoteStation[size];
        }
    };
}
