package com.vilas.aidlorientationlibrary;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class PhoneOrientation {

    private int orientation;
    private static PhoneOrientation getInstance;

    public PhoneOrientation() {
    }

    //Providing global point of access
    public static PhoneOrientation getSingleTonInstance() {
        if (getInstance == null) {
            getInstance = new PhoneOrientation();
        }
        return getInstance;
    }

    public SensorEventListener getEventListener() {
        return sensorEventListener;
    }

    public int getDeviceOrientationData() {
        return orientation;
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] mGravity;
        float[] mGeomagnetic;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float[] rotationMatrix  = new float[9];
                float []orientationAngles = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, orientationAngles, mGravity, mGeomagnetic);
                if (success) {
                    float []orientationData = new float[3];
                    SensorManager.getOrientation(rotationMatrix, orientationData);
                    float xAxis = (float) Math.toDegrees(orientationData[1]);
                    float yAxis = (float) Math.toDegrees(orientationData[2]);

                    orientation = Configuration.ORIENTATION_UNDEFINED;
                    if ((yAxis <= 25) && (yAxis >= -25) && (xAxis >= -160)) {
                        orientation = Configuration.ORIENTATION_PORTRAIT;
                    } else if ((yAxis < -25) && (xAxis >= -20)) {
                        orientation = Configuration.ORIENTATION_LANDSCAPE;
                    } else if ((yAxis > 25) && (xAxis >= -20)) {
                        orientation = Configuration.ORIENTATION_LANDSCAPE;
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}


