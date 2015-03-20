package com.mxmariner.andxtidelib.remote;

import android.os.Parcel;
import android.os.Parcelable;


public class RemoteStationData implements Parcelable {
    
    public static final int REQUEST_OPTION_PLAIN_DATA = 2;
    public static final int REQUEST_OPTION_RAW_DATA = 4;
    public static final int REQUEST_OPTION_PREDICTION = 8;
    public static final int REQUEST_OPTION_ABOUT = 16;
    public static final int REQUEST_OPTION_GRAPH_SVG = 32;
    public static final int REQUEST_OPTION_CLOCK_SVG = 64;

    protected long id;
    protected String name;
    protected String dataTimeStamp;
    protected double latitude;
    protected double longitude;

    /* optional data */
    protected String[] plainData;
    protected String[] rawData;
    protected String prediction;
    protected String about;
    protected String graphSvg;
    protected String clockSvg;

    public RemoteStationData() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDataTimeStamp() {
        return dataTimeStamp;
    }

    /* optional data */

    public String[] getOptionalPlainData() {
        return plainData;
    }

    public String[] getOptionalRawData() {
        return rawData;
    }

    public String getOptionalPrediction() {
        return prediction;
    }

    public String getOptionalAbout() {
        return about;
    }

    public String getOptionalGraphSvg() {
        return graphSvg;
    }

    public String getOptionalClockSvg() {
        return clockSvg;
    }

    protected RemoteStationData(Parcel in) {
        id = in.readLong();
        name = in.readString();
        dataTimeStamp = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        plainData = (String[]) in.readArray(String.class.getClassLoader());
        rawData = (String[]) in.readArray(String.class.getClassLoader());
        prediction = in.readString();
        about = in.readString();
        graphSvg = in.readString();
        clockSvg = in.readString();
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
        dest.writeArray(plainData);
        dest.writeArray(rawData);
        dest.writeString(prediction);
        dest.writeString(about);
        dest.writeString(graphSvg);
        dest.writeString(clockSvg);
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
