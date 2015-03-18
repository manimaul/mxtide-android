package com.mxmariner.andxtidelib.remote;

import android.os.Parcel;
import android.os.Parcelable;

import com.mxmariner.andxtidelib.StationData;

public class RemoteStationData implements Parcelable {

    private final long id;
    private final String name;
    private final String dataTimeStamp;
    private final double latitude;
    private final double longitude;
    private final String timeStamp;
    private final String[] plainData;
    private final String[] rawData;
    private final String prediction;
    private final String about;

    public RemoteStationData(StationData stationData) {
        id = stationData.getId();
        name = stationData.getName();
        dataTimeStamp = stationData.getDataTimeStamp();
        latitude = stationData.getPosition().getLatitude();
        longitude = stationData.getPosition().getLongitude();
        timeStamp = stationData.getDataTimeStamp();
        plainData = stationData.getPlainData();
        rawData = stationData.getRawData();
        prediction = stationData.getPrediction();
        about = stationData.getAboutStation();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDataTimeStamp() {
        return timeStamp;
    }

    public String[] getPlainData() {
        return plainData;
    }

    public String[] getRawData() {
        return rawData;
    }

    public String getPrediction() {
        return prediction;
    }

    public String getAbout() {
        return about;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    protected RemoteStationData(Parcel in) {
        id = in.readLong();
        name = in.readString();
        dataTimeStamp = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timeStamp = in.readString();
        plainData = (String[]) in.readArray(String.class.getClassLoader());
        rawData = (String[]) in.readArray(String.class.getClassLoader());
        prediction = in.readString();
        about = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(dataTimeStamp);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(timeStamp);
        dest.writeArray(plainData);
        dest.writeArray(rawData);
        dest.writeString(prediction);
        dest.writeString(about);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RemoteStationData> CREATOR = new Parcelable.Creator<RemoteStationData>() {
        @Override
        public RemoteStationData createFromParcel(Parcel in) {
            return new RemoteStationData(in);
        }

        @Override
        public RemoteStationData[] newArray(int size) {
            return new RemoteStationData[size];
        }
    };
}
