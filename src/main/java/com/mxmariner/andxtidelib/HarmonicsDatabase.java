package com.mxmariner.andxtidelib;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.StationType;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class HarmonicsDatabase implements Closeable {
    public static final String TAG = HarmonicsDatabase.class.getSimpleName();

    private static final String TABLE_STATIONS = "stations";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private static final String CREATE_TABLE_STATIONS = "CREATE TABLE " + TABLE_STATIONS + " (" +
            ID + "  INTEGER PRIMARY KEY AUTOINCREMENT," +
            NAME + " TEXT UNIQUE," +
            TYPE + " TEXT," +
            LATITUDE + " NUMERIC, " +
            LONGITUDE + " NUMERIC);";

    private static final String CREATE_INDEX = "CREATE INDEX idx_stations ON " + TABLE_STATIONS + " (" +
            LATITUDE + "," +
            LONGITUDE + ");";

    private SQLiteDatabase db = null;

    public static Observable<HarmonicsDatabase> openOrCreateAsync(final Context context, final File tcdHarmonicsFile) {

        return Observable.create(new Observable.OnSubscribe<HarmonicsDatabase>() {
            @Override
            public void call(final Subscriber<? super HarmonicsDatabase> subscriber) {
                Log.i(TAG, "name = " + tcdHarmonicsFile.getName());
                File dbFile = new File(context.getFilesDir(), tcdHarmonicsFile.getName() + ".s3db");

                XtideJni.getInstance().loadHarmonics(tcdHarmonicsFile.getAbsolutePath());

                final SQLiteDatabase stationsDb;
                if (dbFile.exists()) {
                    stationsDb = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                    subscriber.onNext(new HarmonicsDatabase(stationsDb));
                    subscriber.onCompleted();
                } else {
                    stationsDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
                    stationsDb.execSQL(CREATE_TABLE_STATIONS);
                    stationsDb.execSQL(CREATE_INDEX);
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            long t = System.currentTimeMillis();

                            long dt = System.currentTimeMillis() - t;
                            Log.d(TAG, "loadHarmonics() - " + dt);
                            t = System.currentTimeMillis();
                            String[] stationIndex = XtideJni.getInstance().getStationIndex();
                            dt = System.currentTimeMillis() - t;
                            Log.d(TAG, "getStationIndex() - " + dt);
                            HashSet<String> stationSet = new HashSet<>(stationIndex.length);
                            Station station;
                            ContentValues stationValues = new ContentValues(4);
                            stationsDb.beginTransaction();
                            for (String staStr : stationIndex) {
                                station = new Station(staStr);
                                if (!stationSet.contains(station.getName())) {
                                    stationValues.put(NAME, station.getName());
                                    stationValues.put(LATITUDE, station.getPosition().getLatitude());
                                    stationValues.put(LONGITUDE, station.getPosition().getLongitude());
                                    stationValues.put(TYPE, station.getType().getTypeStr());
                                    stationSet.add(station.getName());
                                    stationsDb.insert(TABLE_STATIONS, null, stationValues);
                                }
                            }

                            stationsDb.setTransactionSuccessful();
                            stationsDb.endTransaction();

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            super.onPostExecute(result);
                            HarmonicsDatabase database = new HarmonicsDatabase(stationsDb);
                            subscriber.onNext(database);
                            subscriber.onCompleted();
                        }
                    };

                    task.execute();
                }
            }
        });
    }

    private HarmonicsDatabase(SQLiteDatabase db) {
        this.db = db;
    }

    public List<RemoteStation> getClosestStationsIds(StationType type, double lat, double lng, int count) {
        double maxLat = lat + 1;
        double maxLng = lng + 1;
        double minLat = lat - 1;
        double minLng = lng - 1;

        Cursor cursor = getStationIdCursorInBounds(type, maxLat, maxLng, minLat, minLng);
        int found = cursor.getCount();
        int cardinals = 4;
        while (found < count && cardinals > 0) {
            cardinals = 0;
            cursor.close();
            if (maxLat < 90d) {
                maxLat++;
                cardinals++;
            }
            if (maxLng < 180d) {
                maxLng++;
                cardinals++;
            }
            if (minLat > -90d) {
                minLat--;
                cardinals++;
            }
            if (minLng > -180d) {
                minLng--;
                cardinals++;
            }
            cursor = getStationIdCursorInBounds(type, maxLat, maxLng, minLat, minLng);
            found = cursor.getCount();
        }
        List<RemoteStation> remoteStations = new ArrayList<>(found);
        cursor.moveToFirst();
        RemoteStation remoteStation;
        while (!cursor.isAfterLast()) {
            remoteStation = getRemoteStationById(cursor.getLong(0));
            if (remoteStation != null) {
                remoteStations.add(remoteStation);
            }
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(remoteStations, new StationSorter(lat, lng));

        return remoteStations.subList(0, Math.min(found, count));
    }

    /**
     * Clips a number to the specified minimum and maximum values.
     *
     * @param n        The number to clip
     * @param minValue Minimum allowable value
     * @param maxValue Maximum allowable value
     * @return The clipped value.
     */
    private static double clip(final double n, final double minValue, final double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    private Cursor getStationIdCursorInBounds(StationType type, double north, double east, double south, double west) {
        east = clip(east, -180d, 180d);
        west = clip(west, -180d, 180d);
        if (east < west) {
            Cursor westCursor = getStationIdCursorInBounds(type, north, 180d, south, west);
            Cursor eastCursor = getStationIdCursorInBounds(type, north, east, south, -180d);
            return new MergeCursor(new Cursor[]{westCursor, eastCursor});
        }

        final String sql = "Select " + ID + " FROM " + TABLE_STATIONS
                + " WHERE (type=?) AND (" + LATITUDE + " BETWEEN ? AND ?) AND (" + LONGITUDE + " BETWEEN ? AND ?);";

        final String[] selArgs = {type.getTypeStr(), String.valueOf(south), String.valueOf(north),
                String.valueOf(west), String.valueOf(east)};

        return db.rawQuery(sql, selArgs);
    }

    public int getStationsCountInBounds(StationType type, double north, double east, double south, double west) {
        final Cursor cursor = getStationIdCursorInBounds(type, north, east, south, west);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<RemoteStation> getStationsInBounds(StationType type, double north, double east, double south, double west) {

        final Cursor cursor = getStationIdCursorInBounds(type, north, east, south, west);
        List<RemoteStation> remoteStations = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        RemoteStation remoteStation;
        while (!cursor.isAfterLast()) {
            remoteStation = getRemoteStationById(cursor.getLong(0));
            if (remoteStation != null) {
                remoteStations.add(remoteStation);
            }
            cursor.moveToNext();
        }
        cursor.close();

        return remoteStations;
    }

    public RemoteStation getRemoteStationById(long id) {
        RemoteStation remoteStation = null;
        final String[] columns = {LATITUDE, LONGITUDE, TYPE};
        final String selection = ID + "=?";
        final String selectionArgs[] = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            remoteStation = new RemoteStation(id, cursor.getDouble(0), cursor.getDouble(1),
                    StationType.typeWithString(cursor.getString(2)));
        }
        cursor.close();
        return remoteStation;

    }

    public Station getStationDetailById(long id) {
        Station station = null;
        final String[] columns = {NAME, LATITUDE, LONGITUDE, TYPE};
        final String selection = ID + "=?";
        final String selectionArgs[] = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null);
        String str = null;
        if (cursor.moveToFirst()) {
            str = cursor.getString(0) + ";" +
                    cursor.getString(1) + ";" +
                    cursor.getString(2) + ";" +
                    cursor.getString(3);
        }
        cursor.close();
        if (str != null) {
            station = new Station(str, id);
        }
        return station;
    }

    @Override
    public void close() {
        db.close();
    }

    private class StationSorter implements Comparator<RemoteStation> {

        private final double lat;
        private final double lng;

        public StationSorter(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public int compare(RemoteStation lhs, RemoteStation rhs) {

            int lhsDistance = 0;
            int rhsDistance = 0;
            try {
                lhsDistance = MXLatLng.distanceToPoint(lat, lng, lhs.getLatitude(), lhs.getLongitude());
                rhsDistance = MXLatLng.distanceToPoint(lat, lng, rhs.getLatitude(), rhs.getLongitude());
            } catch (RemoteException ignored) {}

            if (lhsDistance == rhsDistance) {
                return 0;
            }

            return lhsDistance < rhsDistance ? -1 : 1;
        }
    }
}
