package com.mxmariner.andxtidelib;


import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HarmonicsDatabase {
    public static final String TAG = HarmonicsDatabase.class.getSimpleName();

    private final HashMap<String, Station> tideStations = new HashMap<>();
    private final HashMap<String, Station> currentStations = new HashMap<>();

    public static void createDatabaseAsync(final File tcdHarmonicsFile, final IHarmonicsDatabaseCallback callback) {

        AsyncTask<Void, Void, List<Station>> task = new AsyncTask<Void, Void, List<Station>>() {
            @Override
            protected List<Station> doInBackground(Void... params) {
                long t = System.currentTimeMillis();
                XtideJni.getInstance().loadHarmonics(tcdHarmonicsFile.getAbsolutePath());
                long dt = System.currentTimeMillis() - t;
                Log.d(TAG, "loadHarmonics() - " + dt);
                t = System.currentTimeMillis();
                String[] stationIndex = XtideJni.getInstance().getStationIndex();
                dt = System.currentTimeMillis() - t;
                Log.d(TAG, "getStationIndex() - " + dt);
                LinkedList<Station> list = new LinkedList<>();
                for (String staStr : stationIndex) {
                    list.add(new Station(staStr));
                }

                return list;
            }

            @Override
            protected void onPostExecute(List<Station> stations) {
                super.onPostExecute(stations);
                HarmonicsDatabase database = new HarmonicsDatabase(stations);
                callback.onInitiated(database);
            }
        };

        task.execute();

    }

    private HarmonicsDatabase(List<Station> stations) {
        for (Station ea : stations) {
            if (ea.getType()== StationType.STATION_TYPE_TIDE) {
                tideStations.put(ea.getName(), ea);
            }
            if (ea.getType() == StationType.STATION_TYPE_CURRENT) {
                currentStations.put(ea.getName(), ea);
            }
        }
    }

    public ArrayList<Station> getTideStations() {
        return new ArrayList<>(tideStations.values());
    }

    public ArrayList<Station> getCurrentStations() {
        return new ArrayList<>(currentStations.values());
    }

    public interface IHarmonicsDatabaseCallback {
        public void onInitiated(HarmonicsDatabase database);
    }
}
