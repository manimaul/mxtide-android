package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.remote.StationType;

public class Station {
    private String stationName;
    private StationType stationType;
	private MXLatLng latLng;
    private long id = -1;

    public Station(String xtideStr, long id) {
        this(xtideStr);
        this.id = id;
    }
	
	/**
	 * 
	 * @param xtideStr ex "some station name;45.243829;-122.193847;current"
     *                    "some station name;45.243829;-122.193847;tide;
	 */
    public Station(String xtideStr) {
        String[] data = xtideStr.split(";");
		stationName = data[0].trim();
        latLng = new MXLatLng(0,0);
        latLng.setLatitudeParseString(data[1]);
        latLng.setLongitudeParseString(data[2]);
        String type = data[3];

        if (type.equals("current")) {
            stationType = StationType.STATION_TYPE_CURRENT;
        } else {
            stationType = StationType.STATION_TYPE_TIDE;
        }
	}

    public String getName() {
        return stationName;
    }

    public StationType getType() {
        return stationType;
    }

    public long getId() {
        return id;
    }

    public MXLatLng getPosition() {
        return latLng;
    }
}
