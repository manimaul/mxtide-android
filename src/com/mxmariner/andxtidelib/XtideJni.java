package com.mxmariner.andxtidelib;

public class XtideJni {
	
//	/**
//	 * 
//	 * @param pPath - path to harmonics tcd file to load
//	 * http://www.flaterco.com/xtide/files.html#harmonicsfiles
//	 */
//	public XtideJni(String pPath) {
//		loadHarmonics(pPath);
//	}
	
//	/**
//	 * load additional harmonics file
//	 * @param pPath - path to harmonics tcd file to load
//	 * http://www.flaterco.com/xtide/files.html#harmonicsfiles
//	 */
//	public void loadHarmonicsFile(String pPath) {
//		loadHarmonics(pPath);
//	}
	
	public native void loadHarmonics(String pPath);
	
	/**
	 * get a list of tide and current stations and lat, lng
	 */
	public native String getStationIndex();
	
	/**
	 * get information about station in harmonics file
	 * @param pStation
	 * @param epoch
	 * @return
	 */
	public native String getStationAbout(String pStation, long epoch);
	
	public native String getStationRawData(String pStation, long epoch);
	
	public native String getStationPlainData(String pStation, long epoch);
	
	public native String getStationPrediction(String pStation, long epoch);
	
	public native String getStationTimestamp(String pStation, long epoch);
	
	static {
		System.loadLibrary("AndXTideLib");
	}

}
