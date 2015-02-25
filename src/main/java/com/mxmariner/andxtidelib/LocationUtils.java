package com.mxmariner.andxtidelib;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {

    public static MXLatLng getLastKnownLocation(Context context) {
        LocationManager pLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final Location gpsLocation = getLastKnownLocation(pLocationManager, LocationManager.GPS_PROVIDER);
        final Location networkLocation = getLastKnownLocation(pLocationManager, LocationManager.NETWORK_PROVIDER);
        if (gpsLocation == null) {
            return new MXLatLng(networkLocation);
        } else if (networkLocation == null) {
            return new MXLatLng(gpsLocation);
        } else {
            // both are non-null - use the most recent (+ delay for GPS)
            if (networkLocation.getTime() > gpsLocation.getTime() + 3000) {
                return new MXLatLng(networkLocation);
            } else {
                return new MXLatLng(gpsLocation);
            }
        }
    }

    private static Location getLastKnownLocation(LocationManager pLocationManager, String pProvider) {
        try {
            if (!pLocationManager.isProviderEnabled(pProvider)) {
                return null;
            }
        } catch (final IllegalArgumentException e) {
            return null;
        }
        return pLocationManager.getLastKnownLocation(pProvider);
    }
}
