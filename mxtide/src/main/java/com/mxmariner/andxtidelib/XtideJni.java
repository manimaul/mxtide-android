package com.mxmariner.andxtidelib;

public enum  XtideJni {

	INSTANCE;

//	public String[] getStationPlainDataSa(String pStation, long epoch) {
//		String[] data = getStationPlainData(pStation, epoch).split("\n");
//		for (int i=0; i<data.length; i++) {
//			//remove date prefix e.g. 2015-01-01 or 2015/01/01
//			data[i] = data[i].replaceFirst("\\d{4}(-|/)\\d{2}(-|/)\\d{2}", "");
//		}
//		return data;
//	}
//
//	public String[] getStationRawDataSa(String pStation, long epoch) {
//		String[] data = getStationRawData(pStation, epoch).split("\n");
//		for (int i=0; i<data.length; i++) {
//			data[i] = data[i].trim();
//		}
//		return data;
//	}

	public String getStationPredictionS(String pStation, long epoch) {
		return getStationPrediction(pStation, epoch).trim();
	}

	public synchronized native void loadHarmonics(String pPath);

	private synchronized native void setUnits(int pPath);

	public synchronized native String[] getStationIndex();

	public synchronized native String getStationAbout(String pStation, long epoch);

	private synchronized native String getStationRawData(String pStation, long epoch);

	private synchronized native String getStationPlainData(String pStation, long epoch);

	private synchronized native String getStationPrediction(String pStation, long epoch);

	public synchronized native String getStationTimestamp(String pStation, long epoch);

	public synchronized native String getStationClockSvg(String pStation, long epoch);

	public synchronized native String getStationGraphSvg(String pStation, long epoch);

	static {
		System.loadLibrary("andxtide");
	}

}
