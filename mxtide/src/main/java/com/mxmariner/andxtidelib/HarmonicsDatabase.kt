package com.mxmariner.andxtidelib


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MergeCursor
import android.database.sqlite.SQLiteDatabase
import android.os.RemoteException
import android.util.Log
import com.mxmariner.andxtidelib.remote.RemoteStation
import com.mxmariner.andxtidelib.remote.StationType
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.io.File
import java.util.*

private const val TABLE_STATIONS = "stations"
private const val NAME = "name"
private const val ID = "id"
private const val TYPE = "type"
private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"
private const val CREATE_TABLE_STATIONS = "CREATE TABLE $TABLE_STATIONS ($ID INTEGER PRIMARY KEY AUTOINCREMENT,$NAME TEXT UNIQUE,$TYPE TEXT,$LATITUDE NUMERIC, $LONGITUDE NUMERIC);"
private const val CREATE_INDEX = "CREATE INDEX idx_stations ON $TABLE_STATIONS ($LATITUDE,$LONGITUDE);"

class HarmonicsDatabase private constructor(val db: SQLiteDatabase) : Closeable {

    fun getClosestStationsIds(type: StationType, lat: Double, lng: Double, count: Int): List<RemoteStation> {
        var maxLat = lat + 1
        var maxLng = lng + 1
        var minLat = lat - 1
        var minLng = lng - 1

        var cursor = getStationIdCursorInBounds(type, maxLat, maxLng, minLat, minLng)
        var found = cursor.count
        var cardinals = 4
        while (found < count && cardinals > 0) {
            cardinals = 0
            cursor.close()
            if (maxLat < 90.0) {
                maxLat++
                cardinals++
            }
            if (maxLng < 180.0) {
                maxLng++
                cardinals++
            }
            if (minLat > -90.0) {
                minLat--
                cardinals++
            }
            if (minLng > -180.0) {
                minLng--
                cardinals++
            }
            cursor = getStationIdCursorInBounds(type, maxLat, maxLng, minLat, minLng)
            found = cursor.count
        }
        val remoteStations = ArrayList<RemoteStation>(found)
        cursor.moveToFirst()
        var remoteStation: RemoteStation?
        while (!cursor.isAfterLast) {
            remoteStation = getRemoteStationById(cursor.getLong(0))
            if (remoteStation != null) {
                remoteStations.add(remoteStation)
            }
            cursor.moveToNext()
        }
        cursor.close()

        Collections.sort(remoteStations, StationSorter(lat, lng))

        return remoteStations.subList(0, Math.min(found, count))
    }

    private fun getStationIdCursorInBounds(type: StationType, north: Double, e: Double, south: Double, w: Double): Cursor {
        var east = e
        var west = w
        east = clip(east, -180.0, 180.0)
        west = clip(west, -180.0, 180.0)
        if (east < west) {
            val westCursor = getStationIdCursorInBounds(type, north, 180.0, south, west)
            val eastCursor = getStationIdCursorInBounds(type, north, east, south, -180.0)
            return MergeCursor(arrayOf(westCursor, eastCursor))
        }

        val sql = "Select $ID FROM $TABLE_STATIONS  WHERE (type=?) AND ($LATITUDE BETWEEN ? AND ?) AND ($LONGITUDE BETWEEN ? AND ?);"

        val selArgs = arrayOf(type.typeStr, south.toString(), north.toString(), west.toString(), east.toString())

        return db.rawQuery(sql, selArgs)
    }

    fun getStationsCountInBounds(type: StationType, north: Double, east: Double, south: Double, west: Double): Int {
        val cursor = getStationIdCursorInBounds(type, north, east, south, west)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getStationsInBounds(type: StationType, north: Double, east: Double, south: Double, west: Double): List<RemoteStation> {

        val cursor = getStationIdCursorInBounds(type, north, east, south, west)
        val remoteStations = ArrayList<RemoteStation>(cursor.count)
        cursor.moveToFirst()
        var remoteStation: RemoteStation?
        while (!cursor.isAfterLast) {
            remoteStation = getRemoteStationById(cursor.getLong(0))
            if (remoteStation != null) {
                remoteStations.add(remoteStation)
            }
            cursor.moveToNext()
        }
        cursor.close()

        return remoteStations
    }

    fun getRemoteStationById(id: Long): RemoteStation? {
        var remoteStation: RemoteStation? = null
        val columns = arrayOf(LATITUDE, LONGITUDE, TYPE)
        val selection = ID + "=?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null)
        if (cursor.moveToFirst()) {
            StationType.CREATOR.typeWithString(cursor.getString(2))?.let { stationType ->
                remoteStation = RemoteStation(id, cursor.getDouble(0), cursor.getDouble(1), stationType)
            }
        }
        cursor.close()
        return remoteStation

    }

    fun getStationDetailById(id: Long): Station? {
        var station: Station? = null
        val columns = arrayOf(NAME, LATITUDE, LONGITUDE, TYPE)
        val selection = ID + "=?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(TABLE_STATIONS, columns, selection, selectionArgs, null, null, null)
        var str: String? = null
        if (cursor.moveToFirst()) {
            str = cursor.getString(0) + ";" +
                    cursor.getString(1) + ";" +
                    cursor.getString(2) + ";" +
                    cursor.getString(3)
        }
        cursor.close()
        if (str != null) {
            station = Station(str, id)
        }
        return station
    }

    override fun close() {
        db.close()
    }

    private inner class StationSorter(private val lat: Double, private val lng: Double) : Comparator<RemoteStation> {

        override fun compare(lhs: RemoteStation, rhs: RemoteStation): Int {

            var lhsDistance = 0
            var rhsDistance = 0
            try {
                lhsDistance = distanceToPoint(lat, lng, lhs.latitude, lhs.longitude)
                rhsDistance = distanceToPoint(lat, lng, rhs.latitude, rhs.longitude)
            } catch (ignored: RemoteException) {
            }

            if (lhsDistance == rhsDistance) {
                return 0
            }

            return if (lhsDistance < rhsDistance) -1 else 1
        }
    }

    companion object {
        val TAG = HarmonicsDatabase::class.java.simpleName



        fun openOrCreateAsync(context: Context, tcdHarmonicsFile: File): Observable<HarmonicsDatabase> {

            return Observable.create(ObservableOnSubscribe<HarmonicsDatabase> { subscriber ->
                Log.i(TAG, "name = " + tcdHarmonicsFile.name)
                val dbFile = File(context.filesDir, tcdHarmonicsFile.name + ".s3db")

                XtideJni.INSTANCE.loadHarmonics(tcdHarmonicsFile.absolutePath)

                val stationsDb: SQLiteDatabase
                if (dbFile.exists()) {
                    stationsDb = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
                    subscriber.onNext(HarmonicsDatabase(stationsDb))
                    subscriber.onComplete()
                } else {
                    stationsDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
                    stationsDb.execSQL(CREATE_TABLE_STATIONS)
                    stationsDb.execSQL(CREATE_INDEX)
                    var t = System.currentTimeMillis()

                    var dt = System.currentTimeMillis() - t
                    Log.d(TAG, "loadHarmonics() - " + dt)
                    t = System.currentTimeMillis()
                    val stationIndex = XtideJni.INSTANCE.stationIndex
                    dt = System.currentTimeMillis() - t
                    Log.d(TAG, "getStationIndex() - " + dt)
                    val stationSet = HashSet<String>(stationIndex.size)
                    var station: Station
                    val stationValues = ContentValues(4)
                    stationsDb.beginTransaction()
                    for (staStr in stationIndex) {
                        station = Station(staStr, -1L)
                        if (!stationSet.contains(station.name)) {
                            stationValues.put(NAME, station.name)
                            stationValues.put(LATITUDE, station.position.latitude)
                            stationValues.put(LONGITUDE, station.position.longitude)
                            stationValues.put(TYPE, station.type.typeStr)
                            stationSet.add(station.name)
                            stationsDb.insert(TABLE_STATIONS, null, stationValues)
                        }
                    }

                    stationsDb.setTransactionSuccessful()
                    stationsDb.endTransaction()

                    val database = HarmonicsDatabase(stationsDb)
                    subscriber.onNext(database)
                    subscriber.onComplete()
                }
            }).subscribeOn(Schedulers.io())
        }

        /**
         * Clips a number to the specified minimum and maximum values.
         *
         * @param n        The number to clip
         * @param minValue Minimum allowable value
         * @param maxValue Maximum allowable value
         * @return The clipped value.
         */
        private fun clip(n: Double, minValue: Double, maxValue: Double): Double {
            return Math.min(Math.max(n, minValue), maxValue)
        }
    }
}
