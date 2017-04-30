package com.example.veronika.ball;

/**
 * Created by veronika on 13/02/2017.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class PositionCheck {
    StringBuilder sb = new StringBuilder();
    public float[] valuesAccel = new float[2];
    GameActivity activity;
    SensorManager sensorManager;
    Sensor sensorAccel;

    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            valuesAccel[0] = -event.values[0]; // X is reversed
            valuesAccel[1] = event.values[1];
            //Log.i("PositionCheck", String.format("valuesAccel: %.3f %.3f", valuesAccel[0], valuesAccel[1]));
        }

    };

    public PositionCheck(GameActivity a) {
        activity = a;
        sensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void onResume() {
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        sensorManager.unregisterListener(listener);
    }

}
