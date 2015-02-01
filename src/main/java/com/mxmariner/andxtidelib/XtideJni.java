package com.mxmariner.andxtidelib;

public class XtideJni {
    
    private static final XtideJni instance = new XtideJni();
    
    protected static XtideJni getInstance() {
        return instance;
    }
    
    private XtideJni() {}
	
	protected synchronized native void loadHarmonics(String pPath);
	
	protected synchronized native String[] getStationIndex();
	
	protected synchronized native String getStationAbout(String pStation, long epoch);
	
	protected synchronized native String getStationRawData(String pStation, long epoch);
	
	protected synchronized native String getStationPlainData(String pStation, long epoch);
	
	protected synchronized native String getStationPrediction(String pStation, long epoch);
	
	protected synchronized native String getStationTimestamp(String pStation, long epoch);
	
	static {
		System.loadLibrary("AndXTideLib");
	}

}
