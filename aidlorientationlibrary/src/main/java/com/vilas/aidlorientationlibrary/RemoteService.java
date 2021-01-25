package com.vilas.aidlorientationlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class RemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iOrientationAidlInterface;
    }

    IOrientationAidlInterface.Stub iOrientationAidlInterface = new IOrientationAidlInterface.Stub() {
        @Override
        public int getOrientation() {
            return PhoneOrientation.getSingleTonInstance().getDeviceOrientationData();
        }
    };
}
