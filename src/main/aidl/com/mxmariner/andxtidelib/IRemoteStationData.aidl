package com.mxmariner.andxtidelib;

interface IRemoteStationData {
    String getDataTimeStamp();
    String[] getPlainData();
    String[] getRawData();
    String getPrediction();
    String getAboutStation();

    String getName();
    String getType();
    double getLatitude();
    double getLongitude();
}
