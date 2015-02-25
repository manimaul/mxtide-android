package com.mxmariner.andxtidelib;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

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
            TYPE +" TEXT," +
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

//    public ArrayList<Station> getTideStations() {
//        return getStations(StationType.STATION_TYPE_TIDE);
//    }
//
//    public ArrayList<Station> getCurrentStations() {
//        return getStations(StationType.STATION_TYPE_CURRENT);
//    }

    public Observable<Station> getStationsObservable(final StationType type) {
        return Observable.create(new Observable.OnSubscribe<Station>() {
            @Override
            public void call(Subscriber<? super Station> subscriber) {
                final String[] columns = {NAME, LATITUDE, LONGITUDE, TYPE};
                final String selection = TYPE + "=?";
                final String selectionArgs[] = {type.getTypeStr()};
                Cursor cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null);
                String str; //name;45.243829;-122.193847;current
                cursor.moveToFirst();
                while (!cursor.isAfterLast() && !subscriber.isUnsubscribed()) {
                    str = cursor.getString(0) + ";" +
                            cursor.getString(1) + ";" +
                            cursor.getString(2) + ";" +
                            cursor.getString(3);
                    subscriber.onNext(new Station(str));
                    cursor.moveToNext();
                }
                subscriber.onCompleted();
                cursor.close();
            }
        });
    }

    public long[] getClosestStationsIds(double lat, double lng, int count) {
        double maxLat = lat + 1;
        double maxLng = lng + 1;
        double minLat = lat - 1;
        double minLng = lng - 1;
        Cursor cursor = getStationIdCursorInBounds(maxLat, maxLng, minLat, minLng);
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
            cursor = getStationIdCursorInBounds(maxLat, maxLng, minLat, minLng);
            found = cursor.getCount();
        }
        long[] result = new long[found];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result[cursor.getPosition()] = cursor.getLong(0);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public ArrayList<Station> getClosestStations(MXLatLng position, int count) {
        double maxLat = position.getLatitude() + 1;
        double maxLng = position.getLongitude() + 1;
        double minLat = position.getLatitude() - 1;
        double minLng = position.getLongitude() - 1;
        Cursor cursor = getStationCursorInBounds(maxLat, maxLng, minLat, minLng);
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
            cursor = getStationCursorInBounds(maxLat, maxLng, minLat, minLng);
            found = cursor.getCount();
        }
        ArrayList<Station> stations = new ArrayList<>(found);
        cursor.moveToFirst();
        String str;
        while (!cursor.isAfterLast()) {
            str = cursor.getString(0) + ";" +
                    cursor.getString(1) + ";" +
                    cursor.getString(2) + ";" +
                    cursor.getString(3);
            stations.add(new Station(str));
            cursor.moveToNext();
        }
        cursor.close();

        Collections.sort(stations, new StationSorter(position));

        if (stations.size() > count) {
            return new ArrayList<>(stations.subList(0, count - 1));
        }

        return stations;
    }

    private Cursor getStationCursorInBounds(double maxLat, double maxLng, double minLat, double minLng) {
        final String sql = "Select " + NAME + ", " + LATITUDE + ", " + LONGITUDE + ", " + TYPE + " FROM " + TABLE_STATIONS
                + " WHERE (" + LATITUDE + " BETWEEN ? AND ?) AND (" + LONGITUDE + " BETWEEN ? AND ?);";

        final String[] selArgs = {String.valueOf(minLat), String.valueOf(maxLat),
                String.valueOf(minLng), String.valueOf(maxLng)};

        return db.rawQuery(sql, selArgs);
    }

    private Cursor getStationIdCursorInBounds(double maxLat, double maxLng, double minLat, double minLng) {
        final String sql = "Select " + ID + " FROM " + TABLE_STATIONS
                + " WHERE (" + LATITUDE + " BETWEEN ? AND ?) AND (" + LONGITUDE + " BETWEEN ? AND ?);";

        final String[] selArgs = {String.valueOf(minLat), String.valueOf(maxLat),
                String.valueOf(minLng), String.valueOf(maxLng)};

        return db.rawQuery(sql, selArgs);
    }

    public long[] getStationsInBounds(double maxLat, double maxLng, double minLat, double minLng) {

        final Cursor cursor = getStationIdCursorInBounds(maxLat, maxLng, minLat, minLng);
        long[] result = new long[cursor.getCount()];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result[cursor.getPosition()] = cursor.getLong(0);
            cursor.moveToNext();
        }
        cursor.close();

        return result;
    }
    
    public Station getStationDataById(long id) {
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
            station = new Station(str);
        }
        return station;
    }

//    public ArrayList<Station> getStations(StationType type) {
//        final String[] columns = {NAME, LATITUDE, LONGITUDE, TYPE};
//        final String selection = TYPE + "=?";
//        final String selectionArgs[] = {type.getTypeStr()};
//        Cursor cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null);
//        ArrayList<Station> stations = new ArrayList<>(cursor.getCount());
//        String str; //name;45.243829;-122.193847;current
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            str = cursor.getString(0) + ";" +
//                    cursor.getString(1) + ";" +
//                    cursor.getString(2) + ";" +
//                    cursor.getString(3);
//            stations.add(new Station(str));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return stations;
//    }

//    public Observable<Station> getStationsObservable(StationType type) {
//        final String[] columns = {NAME, LATITUDE, LONGITUDE, TYPE};
//        final String selection = TYPE + "=?";
//        final String selectionArgs[] = {type.getTypeStr()};
//        Cursor cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null);
//        Station[] stations = new Station[cursor.getCount()];
//        String str; //name;45.243829;-122.193847;current
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            str = cursor.getString(0) + ";" +
//                    cursor.getString(1) + ";" +
//                    cursor.getString(2) + ";" +
//                    cursor.getString(3);
//            stations[cursor.getPosition()] = new Station(str);
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return Observable.from(stations);
//    }

    @Override
    public void close() {
        db.close();
    }

    private class StationSorter implements Comparator<Station> {

        private final MXLatLng position;

        StationSorter(MXLatLng position) {
            this.position = position;
        }

        @Override
        public int compare(Station lhs, Station rhs) {
            if (position == null) {
                return 0;
            }
            int lhsDistance = lhs.getPosition().distanceToPoint(position);
            int rhsDistance = rhs.getPosition().distanceToPoint(position);

            if (lhsDistance == rhsDistance) {
                return 0;
            }
            return lhsDistance < rhsDistance ? -1 : 1;
        }
    }
}
