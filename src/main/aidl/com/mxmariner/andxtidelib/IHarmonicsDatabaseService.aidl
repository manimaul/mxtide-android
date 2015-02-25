package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.IRemoteServiceCallback;
import com.mxmariner.andxtidelib.IRemoteStationData;

interface IHarmonicsDatabaseService {
    void loadDatabaseAsync(IRemoteServiceCallback callback);
    long[] getStationsInBounds(double maxLat, double maxLng, double minLat, double minLng);
    long[] getClosestStations(double lat, double lng, int count);
    IRemoteStationData getDataForTime(long stationId, long dateEpoch);
}
