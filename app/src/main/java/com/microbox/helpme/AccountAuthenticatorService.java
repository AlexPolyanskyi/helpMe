package com.microbox.helpme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.microbox.helpme.utils.Authenticator;

/**
 * Created by user on 06.11.2014.
 */
public class AccountAuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
