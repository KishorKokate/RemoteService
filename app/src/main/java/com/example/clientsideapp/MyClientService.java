package com.example.clientsideapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyClientService extends Service {
    public MyClientService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}