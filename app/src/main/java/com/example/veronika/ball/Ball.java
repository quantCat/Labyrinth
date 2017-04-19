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
    CollisionsCalculator collisionsCalc = new CollisionsCalculator();

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

    public float getVx() { return vx; }

    public float getVy() { return vy; }

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

    public void collision (Labyrinth.Wall wall) {
        //vector 1, l=1, parallel to the wall
        Log.i("Ball.collision", String.format("at the beginning: x=%.2f y=%.2f vx=%.2f vy=%.2f wall: %d:%d - %d:%d",
                    x, y, vx, vy, wall.begin.x, wall.begin.y, wall.end.x, wall.end.y));
        float wall_vec_x = wall.end.x - wall.begin.x;
        float wall_vec_y = wall.end.y - wall.begin.y;
        final float wall_len_tmp = (float) Math.hypot(wall_vec_x, wall_vec_y);
        wall_vec_x /= wall_len_tmp;
        wall_vec_y /= wall_len_tmp;

        //vector 2, l=1, perpendicular to the wall
        float ort_vec_x = wall_vec_y;
        float ort_vec_y = -wall_vec_x;
        if (ort_vec_x*vx + ort_vec_y*vy < 0) {
            ort_vec_x = -wall_vec_y;
            ort_vec_y = wall_vec_x;
        }

        float spd_along_wall = vx * wall_vec_x + vy * wall_vec_y;
        wall_vec_x *= spd_along_wall; wall_vec_y *= spd_along_wall;
        vx = wall_vec_x;
        vy = wall_vec_y;

        //Now - ball position correction
        float[] wallDetails = collisionsCalc.wallTouchDetails(wall, x, y);
        float distance = wallDetails[0];
        float touchX = wallDetails[1], touchY = wallDetails[2];
        x = touchX - ort_vec_x * Radius;
        y = touchY - ort_vec_y * Radius;
        Log.i("Ball.collision", String.format("at the end: x=%.2f y=%.2f vx=%.2f vy=%.2f",
                    x, y, vx, vy));
    }
}
