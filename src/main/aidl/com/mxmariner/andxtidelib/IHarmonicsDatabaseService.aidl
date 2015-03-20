package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.IRemoteServiceCallback;
import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;

interface IHarmonicsDatabaseService {
    void loadDatabaseAsync(IRemoteServiceCallback callback);
    List<RemoteStation> getStationsInBounds(in StationType type, double north, double east, double south, double west);
    int getStationsCountInBounds(in StationType type, double north, double east, double south, double west);
    List<RemoteStation> getClosestStations(in StationType type, double lat, double lng, int count);
    RemoteStationData getDataForTime(long stationId, long dateEpoch, int options);
}
