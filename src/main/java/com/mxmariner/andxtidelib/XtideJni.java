package com.mxmariner.andxtidelib;

public class XtideJni {

    public void loadHarmonicsI(String pPath) {
        loadHarmonics(pPath);
    }

    public   String getStationIndexI() {
        return getStationIndex();
    }

    public String getStationAboutI(String pStation, long epoch) {
        return getStationAbout(pStation, epoch);
    }

    public String getStationRawDataI(String pStation, long epoch) {
        return getStationRawData(pStation, epoch);
    }

    public String getStationPlainDataI(String pStation, long epoch) {
        return getStationPlainData(pStation, epoch);
    }

    public String getStationPredictionI(String pStation, long epoch) {
        return getStationPrediction(pStation, epoch);
    }

    public String getStationTimestampI(String pStation, long epoch) {
        return getStationTimestamp(pStation, epoch);
    }

    private native void loadHarmonics(String pPath);

	private native String getStationIndex();

	private native String getStationAbout(String pStation, long epoch);
	
	private native String getStationRawData(String pStation, long epoch);
	
	private native String getStationPlainData(String pStation, long epoch);
	
	private native String getStationPrediction(String pStation, long epoch);
	
	private native String getStationTimestamp(String pStation, long epoch);
	
	static {
		System.loadLibrary("AndXTideLib");
	}

}
