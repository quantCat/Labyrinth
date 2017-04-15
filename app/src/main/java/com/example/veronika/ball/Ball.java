package com.example.veronika.ball;

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
    private float vx;
    private float vy;
    private float Radius;
    //static float ball_x;
    //static float ball_y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void coordChange (float dX, float dY, int width, int height) {
        Radius = 0.3f*width/2;
        vx+=dX/2;
        vy+=dY/2;
        vx *= 0.9; //friction
        vy *= 0.9;
        x+=vx;
        y+=vy;
       //x = max(x, Radius);
       // y = max(y, Radius);
        x = min(x, width - Radius);
        y = min(y, height - Radius);
        if (x <= 0 + Radius) {
            vx = 0;
            x = 0 + Radius;
        } else if (x >= width - Radius) {
            vx = 0;
            x = width - Radius;
        }

        if (y <= 0 + Radius) {
            vy = 0;
            y = 0 + Radius;
        } else if (y >= height - Radius) {
            vy = 0;
            y = height - Radius;
        }
        //Log.i("trace", String.format("coordChange: x=%.3f y=%.3f width=%d height=%d", x, y, width, height));
        //ball_x = (2*x - width)/width;
        //ball_y = (2*y - height)/height;
    }




    public void draw(Canvas canvas) {
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(0xff808000);
        canvas.drawCircle(x, y, 50, ballPaint);
    }

}
