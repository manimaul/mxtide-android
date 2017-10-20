package com.mxmariner.andxtidelib.remote

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.mxmariner.andxtidelib.*
import com.mxmariner.util.copyRawResourceFile
import io.reactivex.disposables.CompositeDisposable
import java.io.File

inline fun <T:Any, R> whenNotNull(input: T?, callback: (T)->R): R? {
    return input?.let(callback)
}

class HarmonicsDatabaseService : Service() {

    private val myBinder = MyBinder()
    private var harmonicsDatabase: HarmonicsDatabase? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        compositeDisposable.clear()
        harmonicsDatabase?.close()
        harmonicsDatabase = null
        return super.onUnbind(intent)
    }

    private inner class MyBinder : IHarmonicsDatabaseService.Stub() {
        @Throws(RemoteException::class)
        override fun loadDatabaseAsync(callback: IRemoteServiceCallback) {
            harmonicsDatabase?.let {
                callback.onComplete(0)
            } ?: {
                val tcdName = "harmonics.tcd"
                val tcd = File(filesDir, tcdName)
                copyRawResourceFile(this@HarmonicsDatabaseService, R.raw.harmonics_dwf_20161231_free_tcd, tcd)
                HarmonicsDatabase.openOrCreateAsync(this@HarmonicsDatabaseService, tcd)
                        .subscribe( {
                            harmonicsDatabase = it
                            callback.onComplete(0)
                        }, {
                            callback.onComplete(1)
                        } )
            }()
        }

        @Throws(RemoteException::class)
        override fun setUnits(unitType: UnitType) {
            XtideJni.INSTANCE.setUnitsS(unitType)
        }

        @Throws(RemoteException::class)
        override fun getStationsCountInBounds(type: StationType, north: Double, east: Double, south: Double, west: Double): Int {
            return harmonicsDatabase?.getStationsCountInBounds(type, north, east, south, west) ?: 0
        }

        @Throws(RemoteException::class)
        override fun getStationsInBounds(type: StationType, maxLat: Double, maxLng: Double, minLat: Double, minLng: Double): List<RemoteStation> {
            return harmonicsDatabase?.getStationsInBounds(type, maxLat, maxLng, minLat, minLng) ?: emptyList()

        }

        @Throws(RemoteException::class)
        override fun getClosestStations(type: StationType, lat: Double, lng: Double, count: Int): List<RemoteStation> {
            return harmonicsDatabase?.getClosestStationsIds(type, lat, lng, count) ?: emptyList()

        }

        @Throws(RemoteException::class)
        override fun getDataForTime(stationId: Long, dateEpoch: Long, options: Int): RemoteStationData? {
            return harmonicsDatabase?.getStationDetailById(stationId)?.let {
                buildRemoteStationData(it, dateEpoch, options)
            }
        }
    }
}


private fun buildRemoteStationData(station: Station, epoch: Long, optionalData: Int): RemoteStationData {
    val name = station.name

    val remoteStationData = RemoteStationData(station.id,
            station.name,
            XtideJni.INSTANCE.getStationTimestamp(name, epoch),
            station.position.latitude,
            station.position.longitude,
            station.type)

    /* optional data */
    if (optionalData and REQUEST_OPTION_PLAIN_DATA != 0)
        remoteStationData.optionalPlainData = XtideJni.INSTANCE.getStationPlainDataSa(name, epoch)

    if (optionalData and REQUEST_OPTION_RAW_DATA != 0)
        remoteStationData.optionalRawData = XtideJni.INSTANCE.getStationRawDataSa(name, epoch)

    if (optionalData and REQUEST_OPTION_PREDICTION != 0)
        remoteStationData.optionalPrediction = XtideJni.INSTANCE.getStationPredictionS(name, epoch)

    if (optionalData and REQUEST_OPTION_ABOUT != 0)
        remoteStationData.optionalAbout = XtideJni.INSTANCE.getStationAbout(name, epoch)

    if (optionalData and REQUEST_OPTION_GRAPH_SVG != 0)
        remoteStationData.optionalGraphSvg = XtideJni.INSTANCE.getStationGraphSvg(name, epoch)

    if (optionalData and REQUEST_OPTION_CLOCK_SVG != 0)
        remoteStationData.optionalClockSvg = XtideJni.INSTANCE.getStationClockSvg(name, epoch)

    return remoteStationData
}
