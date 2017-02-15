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
    MainActivity activity;
    SensorManager sensorManager;
    Sensor sensorAccel;

    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            for (int i = 0; i < 2; i++) {
                valuesAccel[i] = event.values[i];
            }
        }

    };

    public PositionCheck(MainActivity a) {
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
