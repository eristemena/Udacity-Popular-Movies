package com.ngoprekweb.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopularMovieAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PopularMovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PopularMovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
