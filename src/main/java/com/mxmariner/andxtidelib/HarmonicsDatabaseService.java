package com.mxmariner.andxtidelib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.mxmariner.util.MXTools;

import java.io.File;

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
        public long[] getStationsInBounds(double maxLat, double maxLng, double minLat, double minLng) throws RemoteException {
            if (harmonicsDatabase == null) {
                return new long[0];
            }

            return harmonicsDatabase.getStationsInBounds(maxLat, maxLng, minLat, minLng);
        }

        @Override
        public long[] getClosestStations(double lat, double lng, int count) throws RemoteException {
            if (harmonicsDatabase == null) {
                return new long[0];
            }
            
            return harmonicsDatabase.getClosestStationsIds(lat, lng, count);
        }

        @Override
        public IRemoteStationData getDataForTime(long stationId, long dateEpoch) throws RemoteException {
            if (harmonicsDatabase == null) {
                return null;
            }
            
            Station station = harmonicsDatabase.getStationById(stationId);
            if (station != null) {
                return new RemoteStationData(station.getDataForTime(dateEpoch));
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
