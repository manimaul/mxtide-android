package com.mxmariner.andxtidelib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.util.MXTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


public class HarmonicsDatabaseService extends Service {

    //region FIELDS  *******************************************************************************

    private MyBinder myBinder = new MyBinder();
    private HarmonicsDatabase harmonicsDatabase;
    private DbOpenSubscriber dbOpenSubscriber;

    //endregion

    //region LIFECYCLE *****************************************************************************

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (dbOpenSubscriber != null) {
            dbOpenSubscriber.unsubscribe();
        }
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
                MXTools.copyRawResourceFile(HarmonicsDatabaseService.this, R.raw.harmonics_dwf_20141224_free_tcd, tcd);
                dbOpenSubscriber = new DbOpenSubscriber(callback);
                HarmonicsDatabase.openOrCreateAsync(HarmonicsDatabaseService.this, tcd)
                        .subscribe(dbOpenSubscriber);
            }
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
        public RemoteStationData getDataForTime(long stationId, long dateEpoch) throws RemoteException {
            if (harmonicsDatabase == null) {
                return null;
            }

            StationDetail stationDetail = harmonicsDatabase.getStationDetailById(stationId);
            if (stationDetail != null) {
                return new RemoteStationData(stationDetail.getDataForTime(dateEpoch));
            }

            return null;
        }
    }

    //endregion ************************************************************************************

    //region SUBSCRIBERS ***************************************************************************

    private class DbOpenSubscriber extends Subscriber<HarmonicsDatabase> {
        final IRemoteServiceCallback callback;

        private DbOpenSubscriber(IRemoteServiceCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onCompleted() {
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
        public void onNext(HarmonicsDatabase database) {
            harmonicsDatabase = database;
        }
    }

    @Override
    public void onDestroy() {
        if (dbOpenSubscriber != null) {
            dbOpenSubscriber.unsubscribe();
        }
        super.onDestroy();
    }

    //endregion ************************************************************************************

}
