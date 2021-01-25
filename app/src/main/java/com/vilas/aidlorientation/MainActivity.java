package com.vilas.aidlorientation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vilas.aidlorientationlibrary.IOrientationAidlInterface;
import com.vilas.aidlorientationlibrary.PhoneOrientation;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    public static final String TAG = "MainActivity";
    private IOrientationAidlInterface iOrientationAidlInterface;
    private SensorManager sensorManager;
    private Sensor vectorSensor;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Handler handler;
    private boolean flag = false;
    private TextView text_x, text_y, text_z, text_orientation_screen;
    private static final String UNDEFINED = "Undefine";
    private static final String PORTRAIT = "Portrait";
    private static final String LANDSCAPE = "Landscape";

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iOrientationAidlInterface = IOrientationAidlInterface.Stub.asInterface(service);
            try {
                int orientation = iOrientationAidlInterface.getOrientation();
                orientationPortraitOrLandscape(orientation);
                Log.e(TAG, "orientation" + orientation);
            } catch (RemoteException e) {
                Log.e(TAG, "orientation" + e);
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        serviceBind();
    }

    private void init() {
        //Get an instance of sensor service ,and use that to get an instance of particular sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        text_x = findViewById(R.id.text_x);
        text_y = findViewById(R.id.text_y);
        text_z = findViewById(R.id.text_z);
        text_orientation_screen = findViewById(R.id.text_orientation_screen);

        handler = new Handler();
    }

    private void serviceBind() {
        Intent implicitIntent = new Intent("com.vilas.service.AIDL");

        Intent explicitIntent = convertImplicitIntentToExplicitIntent(implicitIntent, this);
        bindService(convertImplicitIntentToExplicitIntent(explicitIntent, this), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(PhoneOrientation.getSingleTonInstance().getEventListener(), accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(PhoneOrientation.getSingleTonInstance().getEventListener(), magnetometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, vectorSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        handler.post(processSensors);
    }

    @Override
    protected void onPause() {
        //unregister listener
        sensorManager.unregisterListener(PhoneOrientation.getSingleTonInstance().getEventListener());
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(processSensors);
        super.onPause();
    }

    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            flag = true;
            //read sensor data after 8ms.
            handler.postDelayed(this, 8);
        }
    };


    private void orientationPortraitOrLandscape(int orientation) {
        switch (orientation) {
            case 0:
                text_orientation_screen.setText(UNDEFINED);
                Log.e(TAG, String.valueOf(Configuration.ORIENTATION_UNDEFINED));
                break;
            case 1:
                text_orientation_screen.setText(PORTRAIT);
                Log.e(TAG, String.valueOf(Configuration.ORIENTATION_PORTRAIT));
                break;
            case 2:
                text_orientation_screen.setText(LANDSCAPE);
                Log.e(TAG, String.valueOf(Configuration.ORIENTATION_LANDSCAPE));
                break;
        }
    }

    public Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();

        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag) {
            // Update UI for one reading every 8 millisecond...
            text_x.setText(String.format("Sensor Data of X%s", event.values[0]));
            text_y.setText(String.format("Sensor Data of Y%s", event.values[1]));
            text_z.setText(String.format("Sensor Data of Z%s", event.values[2]));

            flag = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}