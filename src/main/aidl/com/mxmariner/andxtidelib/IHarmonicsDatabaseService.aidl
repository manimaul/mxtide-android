package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.IRemoteServiceCallback;
import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;

interface IHarmonicsDatabaseService {
    void loadDatabaseAsync(IRemoteServiceCallback callback);
    List<RemoteStation> getStationsInBounds(in StationType type, double maxLat, double maxLng, double minLat, double minLng);
    List<RemoteStation> getClosestStations(in StationType type, double lat, double lng, int count);
    RemoteStationData getDataForTime(long stationId, long dateEpoch);
}
