package com.mxmariner.andxtidelib;

import android.location.Location;
import android.util.Log;

@SuppressWarnings("UnusedDeclaration")
public class MXLatLng {
    public static final String TAG = MXLatLng.class.getSimpleName();

    public static final char N = 'N';
    public static final char S = 'S';
    public static final char E = 'E';
    public static final char W = 'W';
    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West"; // is the best :)
    public static final int RADIUS_EARTH_METERS = 6378137;
    public static final double MIN_LATITUDE = -90;
    public static final double MAX_LATITUDE = 90;
    public static final double MIN_LONGITUDE = -180;
    public static final double MAX_LONGITUDE = 180;
    public static final float DEG2RAD = (float) (Math.PI / 180.0);
    public static final float RAD2DEG = (float) (180.0 / Math.PI);


    private double latitude = 0d;
    private double longitude = 0d;
    private double altitude = 0d;

    public MXLatLng(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();
        }
    }

    public MXLatLng(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

    public MXLatLng(int latE6, int lngE6) {
        setLatitudeE6(latE6);
        setLongitudeE6(lngE6);
    }
    
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setLatitude(double lat) {
        this.latitude = clip(lat, MIN_LATITUDE, MAX_LATITUDE);
    }

    public void setLatitude(double latitude, String direction) {
        if (direction.equals(SOUTH)) {
            setLatitude(0 - Math.abs(latitude));
        } else {
            setLatitude(Math.abs(latitude));
        }
    }

    // set latitude degrees decimal minutes
    public void setLatitude(int degrees, double minutes, String direction) {
        double coord = 0d;
        coord += Math.abs(degrees);
        coord += (Math.abs(minutes) / 60d);
        if (direction.equals(SOUTH)) {
            setLatitude(0 - coord);
        } else {
            setLatitude(coord);
        }
    }

    // set latitude degrees minutes decimal seconds
    public void setLatitude(int degrees, int minutes, double seconds,
                            String direction) {
        double coord = 0d;
        coord += Math.abs(degrees);
        coord += (Math.abs(minutes + (Math.abs(seconds) / 60d)) / 60d);
        if (direction.equals(SOUTH)) {
            setLatitude(0 - coord);
        } else {
            setLatitude(coord);
        }
    }

    public void setLongitude(double lng) {
        this.longitude = clip(lng, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    public void setLongitude(double longitude, String direction) {
        if (direction.equals(WEST)) {
            setLongitude(0 - Math.abs(longitude));
        } else {
            setLongitude(Math.abs(longitude));
        }
    }

    // set longitude degrees decimal minutes
    public void setLongitude(int degrees, double minutes, String direction) {
        double coord = 0d;
        coord += Math.abs(degrees);
        coord += (Math.abs(minutes) / 60d);
        if (direction.equals(WEST)) {
            setLongitude(0 - coord);
        } else {
            setLongitude(coord);
        }
    }

    // set longitude degrees minutes decimal seconds
    public void setLongitude(int degrees, int minutes, double seconds,
                             String direction) {
        double coord = 0d;
        coord += Math.abs(degrees);
        coord += (Math.abs(minutes + (Math.abs(seconds) / 60d)) / 60d);
        if (direction.equals(WEST)) {
            setLongitude(0 - coord);
        } else {
            setLongitude(coord);
        }
    }


    // getLatitudeCardinal
    public char getLatitudeCardinal() {
        if (latitude < 0) {
            return S;
        }
        return N;
    }

    // getLongitudeCardinal
    public char getLongitudeCardinal() {
        if (longitude < 0) {
            return W;
        }
        return E;
    }

    // getLatitudeDirection
    public String getLatitudeDirection() {
        if (latitude < 0) {
            return SOUTH;
        }
        return NORTH;
    }

    // getLongitudeDirection
    public String getLongitudeDirection() {
        if (longitude < 0) {
            return WEST;
        }
        return EAST;
    }

    // getDegrees
    private int getDegrees(final double coordinate) {
        return (int) coordinate;
    }

    public int getLatitudeDegrees() {
        return getDegrees(Math.abs(this.latitude));
    }

    public int getLongitudeDegrees() {
        return getDegrees(Math.abs(this.longitude));
    }

    // getMinutes
    private int getMinutes(final double coordinate) {
        return (int) getDecimalMinutes(coordinate);
    }

    public int getLatitudeMinutes() {
        return getMinutes(Math.abs(this.latitude));
    }

    public int getLongitudeMinutes() {
        return getMinutes(Math.abs(this.longitude));
    }

    // getDecimalDegrees DDD
    private double getDecimalDegrees(final double coordinate) {
        return (double) Math.round(coordinate * 1000000) / 1000000;
    }

    public double getLatitudeDecimalDegrees() {
        return getDecimalDegrees(Math.abs(this.latitude));
    }

    public double getLongitudeDecimalDegrees() {
        return getDecimalDegrees(Math.abs(this.longitude));
    }

    // getDecimalMinutes
    private double getDecimalMinutes(final double coordinate) {
        return (double) Math
                .round(((coordinate - getDegrees(coordinate)) * 60) * 100000) / 100000;
    }

    public double getLatitudeDecimalMinutes() {
        return getDecimalMinutes(Math.abs(this.latitude));
    }

    public double getLongitudeDecimalMinutes() {
        return getDecimalMinutes(Math.abs(this.longitude));
    }

    // getDecimalSeconds
    private double getDecimalSeconds(final double coordinate) {
        return (double) Math
                .round(((getDecimalMinutes(coordinate) - getMinutes(coordinate)) * 60) * 1000) / 1000;
    }

    public double getLatitudeDecimalSeconds() {
        return getDecimalSeconds(Math.abs(this.latitude));
    }

    public double getLongitudeDecimalSeconds() {
        return getDecimalSeconds(Math.abs(this.longitude));
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLatitudeE6(int latE6) {
        setLatitude((double) latE6 / 1E6);
    }

    public void setLongitudeE6(int lngE6) {
        setLongitude((double) lngE6 / 1E6);
    }

    public int getLatitudeE6() {
        return (int) (latitude * 1E6);
    }

    public int getLongitudeE6() {
        return (int) (longitude * 1E6);
    }

    public static MXLatLng fromCenterBetween(final MXLatLng startPoint, final MXLatLng endPoint) {
        return new MXLatLng((startPoint.getLatitude() + endPoint.getLatitude()) / 2,
                (startPoint.getLongitude() + endPoint.getLongitude()) / 2);
    }

    public double bearingTo(MXLatLng endPoint) {
        return bearingToPoint(this, endPoint);
    }

    public int distanceToPoint(MXLatLng endPoint) {
        return distanceToPoint(latitude, longitude, endPoint.getLatitude(), endPoint.getLatitude());
    }

    /**
     * @return bearing in degrees
     * @see <a href="http://groups.google.com/group/osmdroid/browse_thread/thread/d22c4efeb9188fe9/bc7f9b3111158dd">discussion</a>
     */
    public static double bearingToPoint(final MXLatLng startPoint, final MXLatLng endPoint) {
        final double lat1 = Math.toRadians(startPoint.getLatitude());
        final double long1 = Math.toRadians(startPoint.getLongitude());
        final double lat2 = Math.toRadians(endPoint.getLatitude());
        final double long2 = Math.toRadians(endPoint.getLongitude());
        final double delta_long = long2 - long1;
        final double a = Math.sin(delta_long) * Math.cos(lat2);
        final double b = Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(delta_long);
        final double bearing = Math.toDegrees(Math.atan2(a, b));
        return (bearing + 360) % 360;
    }


    public static MXLatLng destinationPoint(final MXLatLng startPoint, final double aDistanceInMeters, final float aBearingInDegrees) {
        return destinationPoint(startPoint.getLatitude(), startPoint.getLongitude(), aDistanceInMeters, aBearingInDegrees);
    }

    /**
     * Calculate a point that is the specified distance and bearing away from this point.
     *
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html">latlong.html</a>
     * @see <a href="http://www.movable-type.co.uk/scripts/latlon.js">latlon.js</a>
     */
    public static MXLatLng destinationPoint(final double startLat, final double startLng, final double aDistanceInMeters, final float aBearingInDegrees) {
        // convert distance to angular distance
        final double dist = aDistanceInMeters / RADIUS_EARTH_METERS;

        // convert bearing to radians
        final float brg = DEG2RAD * aBearingInDegrees;

        // get current location in radians
        final double lat1 = DEG2RAD * startLat;
        final double lon1 = DEG2RAD * startLng;

        final double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1)
                * Math.sin(dist) * Math.cos(brg));
        final double lon2 = lon1
                + Math.atan2(Math.sin(brg) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist)
                - Math.sin(lat1) * Math.sin(lat2));

        final double lat2deg = lat2 / DEG2RAD;
        final double lon2deg = lon2 / DEG2RAD;

        return new MXLatLng(lat2deg, lon2deg);
    }

    /**
     * @return distance in meters
     * @see <a href="http://www.geocities.com/DrChengalva/GPSDistance.html">GPSDistance.html</a>
     */
    public static int distanceToPoint(final double startLat, final double startLng, final double endLat, final double endLng) {
        final double a1 = DEG2RAD * startLat;
        final double a2 = DEG2RAD * startLng;
        final double b1 = DEG2RAD * endLat;
        final double b2 = DEG2RAD * endLng;

        final double cosa1 = Math.cos(a1);
        final double cosb1 = Math.cos(b1);

        final double t1 = cosa1 * Math.cos(a2) * cosb1 * Math.cos(b2);

        final double t2 = cosa1 * Math.sin(a2) * cosb1 * Math.sin(b2);

        final double t3 = Math.sin(a1) * Math.sin(b1);

        final double tt = Math.acos(t1 + t2 + t3);

        return (int) (RADIUS_EARTH_METERS * tt);
    }

    @Override
    public String toString() {
        return "latitude: " + latitude + " longitude: " + longitude;
    }

    /**
     * Clips a number to the specified minimum and maximum values.
     *
     * @param n        The number to clip
     * @param minValue Minimum allowable value
     * @param maxValue Maximum allowable value
     * @return The clipped value.
     */
    public static double clip(final double n, final double minValue, final double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    public boolean setLatitudeParseString(String str) {
        ///Log.d(TAG, "parsing latitude string: " + str);
        return parseString(str, true);
    }

    public boolean setLongitudeParseString(String str) {
        //Log.d(TAG, "parsing longitude string: " + str);
        return parseString(str, false);
    }

    private boolean parseString(String str, final boolean isLatitude) {

        // remove leading and trailing whitespace
        str = str.trim();

        // see if there is a north south east or west in string
        final String cardinal = str.replaceAll("[^A-Za-z]", "").toUpperCase();
        boolean westOrSouth = (cardinal.startsWith("W") || cardinal
                .startsWith("S"));

        // see if there is a leading negative
        if (str.startsWith("-"))
            westOrSouth = true;

        String direction;
        if (isLatitude) {
            if (westOrSouth) {
                direction = MXLatLng.SOUTH;
            } else {
                direction = MXLatLng.NORTH;
            }
        } else {
            if (westOrSouth) {
                direction = MXLatLng.WEST;
            } else {
                direction = MXLatLng.EAST;
            }
        }

        // remove leading non digits
        str = str.replaceAll("^\\D+", "");

        // separate digits with commas
        str = str.replaceAll("[^0-9\\.]+", ",");

        String[] lst = str.split(",");
        try {
            if (lst.length == 1) {
                // ddd
                if (isLatitude) {
                    setLatitude(Double.parseDouble(lst[0]), direction);
                    return true;
                } else {
                    setLongitude(Double.parseDouble(lst[0]), direction);
                    return true;
                }

            } else if (lst.length == 2) {
                // dmm
                if (isLatitude) {
                    setLatitude(Integer.parseInt(lst[0]),
                            Double.parseDouble(lst[1]), direction);
                    return true;
                } else {
                    setLongitude(Integer.parseInt(lst[0]),
                            Double.parseDouble(lst[1]), direction);
                    return true;
                }

            } else if (lst.length == 3) {
                // dms
                if (isLatitude) {
                    setLatitude(Integer.parseInt(lst[0]),
                            Integer.parseInt(lst[1]),
                            Double.parseDouble(lst[2]), direction);
                    return true;
                } else {
                    setLongitude(Integer.parseInt(lst[0]),
                            Integer.parseInt(lst[1]),
                            Double.parseDouble(lst[2]), direction);
                    return true;
                }
            }

        } catch (NumberFormatException e) {
            Log.e("LatLongParser", e.getMessage());
        }

        return false;
    }
}
