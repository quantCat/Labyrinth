package com.example.veronika.ball;

import android.util.Log;

/**
 * Created by veronika on 12/02/2017.
 */

public class DrawBall {
    MainActivity activity;
    public static Ball ball = new Ball();
    float[] values;

    public DrawBall(MainActivity ac) {
        Log.i("trace", "DrawBall/2()");
        activity = ac;
    }

    void coordChange () {
        values = activity.pc.valuesAccel;
        ball.coordChange(values[0], values[1], activity.glSurfaceView.getWidth(), activity.glSurfaceView.getHeight());
        activity.glSurfaceView.invalidate();
    }
}
