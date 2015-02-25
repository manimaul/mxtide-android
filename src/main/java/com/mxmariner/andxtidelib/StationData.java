package com.mxmariner.andxtidelib;

import java.util.Date;

public class StationData implements IStationData {

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

    @Override
    public String getDataTimeStamp() {
        if (timeStamp == null) {
            timeStamp = XtideJni.getInstance().getStationTimestamp(station.getName(), epoch);
        }
        return timeStamp;
    }

    @Override
    public String[] getPlainData() {
        if (plainData == null) {
            plainData = XtideJni.getInstance().getStationPlainData(station.getName(), epoch).split("\n");
        }
        return plainData;
    }

    @Override
    public String[] getRawData() {
        if (rawData == null) {
            rawData = XtideJni.getInstance().getStationRawData(station.getName(), epoch).split("\n");
        }
        return rawData;
    }

    @Override
    public String getPrediction() {
        if (prediction == null) {
            prediction = XtideJni.getInstance().getStationPrediction(station.getName(), epoch).trim();
        }
        return prediction;
    }

    @Override
    public String getAboutStation() {
        if (about == null) {
            about = XtideJni.getInstance().getStationAbout(station.getName(), epoch).trim();
        }
        return about;
    }

    @Override
    public void preLoad() {
        getDataTimeStamp();
        getPlainData();
        getRawData();
        getAboutStation();
        getPrediction();
    }

    @Override
    public String getName() {
        return station.getName();
    }

    @Override
    public StationType getType() {
        return station.getType();
    }

    @Override
    public MXLatLng getPosition() {
        return station.getPosition();
    }
}
