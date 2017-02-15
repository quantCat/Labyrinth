package com.example.veronika.ball;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView tvText;
    PositionCheck pc;
    Timer timer;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("trace", "MainActivity.onCreate");
        setContentView(R.layout.activity_main);

        tvText = (TextView) findViewById(R.id.tvText);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        pc = new PositionCheck(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pc.onResume();

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pc.onPause();
        timer.cancel();
    }

    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: " + format(pc.valuesAccel));
        //.append("\n\nAccel motion: " + format(valuesAccelMotion))
        //.append("\nAccel gravity : " + format(valuesAccelGravity))
        //.append("\n\nLin accel : " + format(valuesLinAccel))
        //.append("\nGravity : " + format(valuesGravity));
        tvText.setText(sb);
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f", values[0], values[1]);
    }
}