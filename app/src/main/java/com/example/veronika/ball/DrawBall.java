package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by veronika on 12/02/2017.
 */

public class DrawBall extends View {
    public static Ball ball = new Ball();
    float[] values;

    public DrawBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("trace", "DrawBall/2()");
    }

    void coordChange () {
        values = MainActivity.pc.valuesAccel;
        ball.coordChange(values[0], values[1], getWidth(), getHeight());
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        //Log.i("trace", "onDraw() called");
        super.onDraw(canvas);
        ball.draw(canvas);
    }
}
