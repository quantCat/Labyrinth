package com.example.veronika.ball;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by veronika on 25/02/2017.
 */

class Ball {

    protected static int color = Color.BLACK;
    private float x;
    private float y;
    private float Radius = 50;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void coordChange (float dX, float dY, int width, int height) {
        x+=dX;
        y+=dY;
        x = max(x, Radius);
        y = max(y, Radius);
        x = min(x, width - Radius);
        y = min(y, height - Radius);
        Log.i("trace", String.format("coordChange: %.3f %.3f", x, y));
    }

    public void draw(Canvas canvas) {
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(0xff808000);
        canvas.drawCircle(x, y, 50, ballPaint);
    }

}
