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
    private float prevX;
    private float prevY;
    private float vx; // speed by X
    private float vy; // speed by Y
    Labyrinth labyrinth = null;
    final float Radius = 10.0f;
    final float MAX_SPEED = 20.0f;
    CollisionsCalculator collisionsCalc = new CollisionsCalculator();

    public void initPosition() {
        x = labyrinth.start.x;
        y = labyrinth.start.y;
        prevX = labyrinth.start.x;
        prevY = labyrinth.start.y;
        vx = 0;
        vy = 0;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getOldX() { return prevX; }
    public float getOldY() { return prevY; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }

    public void coordChange (float dX, float dY) {
        prevX = x;
        prevY = y;
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

    public void collisionWithWall (Labyrinth.Wall wall) {
        //vector 1, l=1, parallel to the wall
//        Log.i("Ball.collisionWithWall", String.format("at the beginning: x=%.2f y=%.2f vx=%.2f vy=%.2f wall: %d:%d - %d:%d",
 //                   x, y, vx, vy, wall.begin.x, wall.begin.y, wall.end.x, wall.end.y));
        float wall_vec_x = wall.end.x - wall.begin.x;
        float wall_vec_y = wall.end.y - wall.begin.y;
        float[] wall_vec = collisionsCalc.normalizeVector(wall_vec_x, wall_vec_y);
        wall_vec_x = wall_vec[0]; wall_vec_y = wall_vec[1];

        //vector 2, l=1, perpendicular to the wall
        float ort_vec_x = wall_vec_y;
        float ort_vec_y = -wall_vec_x;
        if (ort_vec_x*vx + ort_vec_y*vy < 0) {
            ort_vec_x = -wall_vec_y;
            ort_vec_y = wall_vec_x;
        }

        float spd_along_wall = vx * wall_vec_x + vy * wall_vec_y;
        vx = wall_vec_x * spd_along_wall;
        vy = wall_vec_y * spd_along_wall;

        //Now - ball position correction
        float[] wallDetails = collisionsCalc.wallTouchDetails(wall, x, y);
        float touchX = wallDetails[1], touchY = wallDetails[2];
        x = touchX - ort_vec_x * Radius;
        y = touchY - ort_vec_y * Radius;
 //       Log.i("Ball.collisionWithWall", String.format("at the end: x=%.2f y=%.2f vx=%.2f vy=%.2f",
 //                   x, y, vx, vy));
    }

    public void collisionWithPoint (Labyrinth.Point point) {
        float iw[] = collisionsCalc.normalizeVector(vy, -vx); //perpendicular to the ball moving vector
        float im_wall_vecX = iw[0];
        float im_wall_vecY = iw[1];

        float radius_vectorX = point.x - x;
        float radius_vectorY = point.y - y;
        float radius_vector[] = collisionsCalc.normalizeVector(radius_vectorX, radius_vectorY);
        radius_vectorX = radius_vector[0]; radius_vectorY = radius_vector[1];

        float ort_vec_x = radius_vectorY;
        float ort_vec_y = -radius_vectorX;
        if (ort_vec_x*vx + ort_vec_y*vy < 0) {
            ort_vec_x = -radius_vectorY;
            ort_vec_y = radius_vectorY;
        }

        float left_spd = vx * ort_vec_x + vy * ort_vec_y;
        vx = ort_vec_x * left_spd;
        vy = ort_vec_y * left_spd;


        //Position correction
        Labyrinth.Wall imagineWall = new Labyrinth.Wall(x, y, x + im_wall_vecX, y + im_wall_vecY);
        float[] wallDetails = collisionsCalc.wallTouchDetails(imagineWall, point.x, point.y);
        float distance = wallDetails[0];

        float moveX = im_wall_vecX * distance;
        float moveY = -im_wall_vecY * distance;
        //x -= moveX;
        //y -= moveY;
    }
}
