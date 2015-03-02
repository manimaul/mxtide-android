package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.remote.StationType;

import java.util.Date;

public class StationData {

    private String timeStamp;
    private String[] plainData;
    private String[] rawData;
    private String prediction;
    private String about;

    private final long epoch;
    private final StationDetail stationDetail;

    public StationData(Date date, StationDetail stationDetail) {
        this(date.getTime() / 1000, stationDetail);
    }

    public StationData(long epoch, StationDetail stationDetail) {
        this.epoch = epoch;
        this.stationDetail = stationDetail;
    }

    public String getDataTimeStamp() {
        if (timeStamp == null) {
            timeStamp = XtideJni.getInstance().getStationTimestamp(stationDetail.getName(), epoch);
        }
        return timeStamp;
    }

    public String[] getPlainData() {
        if (plainData == null) {
            plainData = XtideJni.getInstance().getStationPlainData(stationDetail.getName(), epoch).split("\n");
        }
        return plainData;
    }

    public String[] getRawData() {
        if (rawData == null) {
            rawData = XtideJni.getInstance().getStationRawData(stationDetail.getName(), epoch).split("\n");
        }
        return rawData;
    }

    public String getPrediction() {
        if (prediction == null) {
            prediction = XtideJni.getInstance().getStationPrediction(stationDetail.getName(), epoch).trim();
        }
        return prediction;
    }

    public String getAboutStation() {
        if (about == null) {
            about = XtideJni.getInstance().getStationAbout(stationDetail.getName(), epoch).trim();
        }
        return about;
    }

    public String getName() {
        return stationDetail.getName();
    }

    public StationType getType() {
        return stationDetail.getType();
    }

    public MXLatLng getPosition() {
        return stationDetail.getPosition();
    }
}
