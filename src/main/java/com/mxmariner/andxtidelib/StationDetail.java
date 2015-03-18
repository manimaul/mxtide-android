package com.mxmariner.andxtidelib;

import com.mxmariner.andxtidelib.remote.StationType;

import java.util.Date;

public class StationDetail {
    private String stationName;
    private StationType stationType;
	private MXLatLng latLng;
    private long id = -1;

    public StationDetail(String xtideStr, long id) {
        this(xtideStr);
        this.id = id;
    }
	
	/**
	 * 
	 * @param xtideStr ex "some station name;45.243829;-122.193847;current"
     *                    "some station name;45.243829;-122.193847;tide;
	 */
    public StationDetail(String xtideStr) {
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

    public StationData getDataForTime(Date date) {
        return new StationData(date, this);
    }
    
    public StationData getDataForTime(long epoch) {
        return new StationData(epoch, this);
        
    }

    public long getId() {
        return id;
    }

    public MXLatLng getPosition() {
        return latLng;
    }
}
