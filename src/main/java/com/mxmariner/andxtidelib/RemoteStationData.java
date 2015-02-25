package com.mxmariner.andxtidelib;

import android.os.RemoteException;


public class RemoteStationData extends IRemoteStationData.Stub {
    
    public StationData data;
    
    public RemoteStationData(StationData stationData) { 
        data = stationData;
        data.preLoad();
    }
    
    @Override
    public String getDataTimeStamp() throws RemoteException {
        return data.getDataTimeStamp();
    }

    @Override
    public String[] getPlainData() throws RemoteException {
        return data.getPlainData();
    }

    @Override
    public String[] getRawData() throws RemoteException {
        return data.getRawData();
    }

    @Override
    public String getPrediction() throws RemoteException {
        return data.getPrediction();
    }

    @Override
    public String getAboutStation() throws RemoteException {
        return null;
    }

    @Override
    public String getName() throws RemoteException {
        return data.getName();
    }

    @Override
    public String getType() throws RemoteException {
        return data.getType().name();
    }

    @Override
    public double getLatitude() throws RemoteException {
        return data.getPosition().getLatitude();
    }

    @Override
    public double getLongitude() throws RemoteException {
        return data.getPosition().getLongitude();
    }
}
