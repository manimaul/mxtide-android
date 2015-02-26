package com.mxmariner.andxtidelib;

import java.util.Date;

public class StationData {

    private String timeStamp;
    private String[] plainData;
    private String[] rawData;
    private String prediction;
    private String about;

    private final long epoch;
    private final Station station;

    public StationData(Date date, Station station) {
        this(date.getTime() / 1000, station);
    }
    
    public StationData(long epoch, Station station) {
        this.epoch = epoch;
        this.station = station;
    }

    public String getDataTimeStamp() {
        if (timeStamp == null) {
            timeStamp = XtideJni.getInstance().getStationTimestamp(station.getName(), epoch);
        }
        return timeStamp;
    }

    public String[] getPlainData() {
        if (plainData == null) {
            plainData = XtideJni.getInstance().getStationPlainData(station.getName(), epoch).split("\n");
        }
        return plainData;
    }

    public String[] getRawData() {
        if (rawData == null) {
            rawData = XtideJni.getInstance().getStationRawData(station.getName(), epoch).split("\n");
        }
        return rawData;
    }

    public String getPrediction() {
        if (prediction == null) {
            prediction = XtideJni.getInstance().getStationPrediction(station.getName(), epoch).trim();
        }
        return prediction;
    }

    public String getAboutStation() {
        if (about == null) {
            about = XtideJni.getInstance().getStationAbout(station.getName(), epoch).trim();
        }
        return about;
    }

    public void preLoad() {
        getDataTimeStamp();
        getPlainData();
        getRawData();
        getAboutStation();
        getPrediction();
    }

    public String getName() {
        return station.getName();
    }

    public StationType getType() {
        return station.getType();
    }

    public MXLatLng getPosition() {
        return station.getPosition();
    }
}
