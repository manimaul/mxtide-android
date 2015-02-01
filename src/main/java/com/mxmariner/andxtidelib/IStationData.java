package com.mxmariner.andxtidelib;


public interface IStationData {
    public String getDataTimeStamp();
    public String[] getPlainData();
    public String[] getRawData();
    public String getPrediction();
    public String getAboutStation();
    public void preLoad();

    public String getName();
    public StationType getType();
    public MXLatLng getPosition();
}
