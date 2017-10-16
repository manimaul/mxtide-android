package com.mxmariner.andxtidelib.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.mxmariner.andxtidelib.HarmonicsDatabase;
import com.mxmariner.andxtidelib.IHarmonicsDatabaseService;
import com.mxmariner.andxtidelib.IRemoteServiceCallback;
import com.mxmariner.andxtidelib.R;
import com.mxmariner.andxtidelib.Station;
import com.mxmariner.andxtidelib.XtideJni;
import com.mxmariner.util.MXTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class HarmonicsDatabaseService extends Service {

    //region FIELDS  *******************************************************************************

    private MyBinder myBinder = new MyBinder();
    private HarmonicsDatabase harmonicsDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //endregion

    //region LIFECYCLE *****************************************************************************

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        compositeDisposable.clear();
        if (harmonicsDatabase != null) {
            harmonicsDatabase.close();
            harmonicsDatabase = null;
        }
        return super.onUnbind(intent);
    }

    //endregion ************************************************************************************

    //region INNER CLASSES *************************************************************************

    private class MyBinder extends IHarmonicsDatabaseService.Stub {
        @Override
        public void loadDatabaseAsync(final IRemoteServiceCallback callback) throws RemoteException {
            if (harmonicsDatabase != null) {
                callback.onComplete(0);
            } else {
                String tcdName = "harmonics-dwf-20141224-free.tcd";
                File tcd = new File(getFilesDir(), tcdName);
                MXTools.copyRawResourceFile(HarmonicsDatabaseService.this, R.raw.harmonics_dwf_20161231_free_tcd, tcd);
                HarmonicsDatabase.openOrCreateAsync(HarmonicsDatabaseService.this, tcd)
                        .subscribe(new DbOpenSubscriber(callback));
            }
        }

        @Override
        public void setUnits(UnitType unitType) throws RemoteException {
            XtideJni.getInstance().setUnitsS(unitType);
        }

        @Override
        public int getStationsCountInBounds(StationType type, double north, double east, double south, double west) throws RemoteException {
            if (harmonicsDatabase != null) {
                return harmonicsDatabase.getStationsCountInBounds(type, north, east, south, west);
            }

            return 0;
        }

        @Override
        public List<RemoteStation> getStationsInBounds(StationType type, double maxLat, double maxLng, double minLat, double minLng) throws RemoteException {
            if (harmonicsDatabase == null) {
                return new ArrayList<>();
            }

            return harmonicsDatabase.getStationsInBounds(type, maxLat, maxLng, minLat, minLng);
        }

        @Override
        public List<RemoteStation> getClosestStations(StationType type, double lat, double lng, int count) throws RemoteException {
            if (harmonicsDatabase == null) {
                return new ArrayList<>();
            }

            return harmonicsDatabase.getClosestStationsIds(type, lat, lng, count);
        }

        @Override
        public RemoteStationData getDataForTime(long stationId, long dateEpoch, int options) throws RemoteException {
            if (harmonicsDatabase == null) {
                return null;
            }

            Station station = harmonicsDatabase.getStationDetailById(stationId);
            if (station != null) {
                return buildRemoteStationData(station, dateEpoch, options);
            }

            return null;
        }
    }

    public static RemoteStationData buildRemoteStationData(Station station, long epoch, int optionalData) {
        String name = station.getName();

        RemoteStationData remoteStationData = new RemoteStationData();
        remoteStationData.id = station.getId();
        remoteStationData.name = station.getName();
        remoteStationData.dataTimeStamp = XtideJni.getInstance().getStationTimestamp(name, epoch);
        remoteStationData.latitude = station.getPosition().getLatitude();
        remoteStationData.longitude = station.getPosition().getLongitude();
        remoteStationData.stationType = station.getType();

        /* optional data */
        if ((optionalData & RemoteStationData.REQUEST_OPTION_PLAIN_DATA) != 0)
            remoteStationData.plainData = XtideJni.getInstance().getStationPlainDataSa(name, epoch);

        if ((optionalData & RemoteStationData.REQUEST_OPTION_RAW_DATA) != 0)
            remoteStationData.rawData = XtideJni.getInstance().getStationRawDataSa(name, epoch);

        if ((optionalData & RemoteStationData.REQUEST_OPTION_PREDICTION) != 0)
            remoteStationData.prediction = XtideJni.getInstance().getStationPredictionS(name, epoch);

        if ((optionalData & RemoteStationData.REQUEST_OPTION_ABOUT) != 0)
            remoteStationData.about = XtideJni.getInstance().getStationAbout(name, epoch);

        if ((optionalData & RemoteStationData.REQUEST_OPTION_GRAPH_SVG) != 0)
            remoteStationData.graphSvg = XtideJni.getInstance().getStationGraphSvg(name, epoch);

        if ((optionalData & RemoteStationData.REQUEST_OPTION_CLOCK_SVG) != 0)
            remoteStationData.clockSvg = XtideJni.getInstance().getStationClockSvg(name, epoch);

        return remoteStationData;
    }

    //endregion ************************************************************************************

    //region SUBSCRIBERS ***************************************************************************

    private class DbOpenSubscriber implements Observer<HarmonicsDatabase> {
        final IRemoteServiceCallback callback;

        private DbOpenSubscriber(IRemoteServiceCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onComplete() {
            try {
                callback.onComplete(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            try {
                callback.onComplete(1);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onSubscribe(@NonNull Disposable disposable) {
            compositeDisposable.add(disposable);
        }

        @Override
        public void onNext(HarmonicsDatabase database) {
            harmonicsDatabase = database;
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    //endregion ************************************************************************************

}
