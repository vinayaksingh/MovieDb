package com.vin.moviedb.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by vin on 12/3/17.
 */

public class MovieSyncAdapterService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter sMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MovieSyncAdapterService", "onCreate - MovieSyncAdapterService");
        synchronized (sSyncAdapterLock) {
            if (sMovieSyncAdapter == null) {
                sMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSyncAdapter.getSyncAdapterBinder();
    }
}
