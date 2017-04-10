package com.example.veronika.ball;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView tvText;
    PositionCheck pc;
    Timer timer;
    StringBuilder sb = new StringBuilder();
    GLSurfaceView glSurfaceView;
    static Ball ball = new Ball();
    float[] values;
    Labyrinth labyrinth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("trace", "MainActivity.onCreate");
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvText = (TextView) findViewById(R.id.tvText);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        pc = new PositionCheck(this);
        if (!supportES2()) {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        OpenGLRenderer rr = new OpenGLRenderer(this);
        rr.activity = this;
        glSurfaceView.setRenderer(rr);
        setContentView(glSurfaceView);
        labyrinth = new Labyrinth();
        labyrinth.readWalls(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        pc.onResume();

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread (new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                        coordChange();
                    }
                });
            }
        };
        timer.schedule(task, 0, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        pc.onPause();
        timer.cancel();
    }

    private boolean supportES2() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return (configurationInfo.reqGlEsVersion >= 0x20000);
    }

    void coordChange () {
        values = pc.valuesAccel;
        ball.coordChange(values[0], values[1], glSurfaceView.getWidth(), glSurfaceView.getHeight());
        glSurfaceView.invalidate();
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