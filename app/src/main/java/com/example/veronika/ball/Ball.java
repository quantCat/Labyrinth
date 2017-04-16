package com.example.veronika.ball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by veronika on 25/02/2017.
 */

class Ball {

    protected static int color = Color.BLACK;
    private float x;
    private float y;
    private float vx; // speed by X
    private float vy; // speed by Y
    Labyrinth labyrinth = null;
    final float Radius = 10.0f;
    final float MAX_SPEED = 20.0f;

    public void initPosition() {
        x = labyrinth.start.x;
        y = labyrinth.start.y;
        vx = 0;
        vy = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void coordChange (float dX, float dY) {
        vx += dX * 0.05;
        vy += dY * 0.05;
        vx *= 0.9; //friction
        vy *= 0.9;
        if (vx >  MAX_SPEED) { vx =  MAX_SPEED; }
        if (vx < -MAX_SPEED) { vx = -MAX_SPEED; }
        if (vy >  MAX_SPEED) { vy =  MAX_SPEED; }
        if (vy < -MAX_SPEED) { vy = -MAX_SPEED; }

        x+=vx;
        y+=vy;
        if (x < Radius) { x = Radius; }
        if (x > labyrinth.size.x - Radius) { x = labyrinth.size.x - Radius; }
        if (y < Radius) { y = Radius; }
        if (y > labyrinth.size.y - Radius) { y = labyrinth.size.y - Radius; }

        //Log.i("trace", String.format("Ball.coordChange: x=%.3f y=%.3f vx=%.3f vy=%.3f", x, y, vx, vy));
        //Log.i("trace", String.format("Ball.coordChange: x=%.3f y=%.3f width=%d height=%d", x, y, width, height));
    }

    public void draw(Canvas canvas, int width, int height) {
        final int min_dim = Math.min(width, height);
        final float ball_radius = min_dim / 10.0f;
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(0xff808000);
        canvas.drawCircle(width/2, height/2, ball_radius, ballPaint);
    }

}
