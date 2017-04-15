package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by veronika on 15/04/2017.
 */

public class Drawer extends View {
    public static Ball ball = new Ball();
    float[] values;

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("trace", "DrawBall/2()");
    }

    void coordChange () {
        values = MainActivity.pc.valuesAccel;
        //Log.i("Drawer.coordChange", String.format("%.3f %.3f", values[0], values[1]));
        Drawer.ball.coordChange(values[0], values[1], getWidth(), getHeight());
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        //Log.i("trace", "onDraw() called");
        super.onDraw(canvas);
        ball.draw(canvas);
    }
}
