package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by veronika on 25/02/2017.
 */

class Ball {

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
        prevX = x;
        prevY = y;
        vx = 0;
        vy = 0;
    }

    public void initPosition(float x0, float y0) {
        x = x0;
        y = y0;
        prevX = x0;
        prevY = y0;
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
        prevX = x;
        prevY = y;
        vx += dX * 0.05;
        vy += dY * 0.05;
        vx *= 0.95; //friction
        vy *= 0.95;
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

    public void draw(Context context, Canvas canvas, int width, int height) {
        final int min_dim = Math.min(width, height);
        final float ball_radius = min_dim / 10.0f;
        Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //ballPaint.setColor(0xff808000);
        //canvas.drawCircle(width/2, height/2, ball_radius, ballPaint);
        Bitmap hamster = BitmapFactory.decodeResource(context.getResources(), R.drawable.hamster);
        Bitmap hamster1 = Bitmap.createScaledBitmap(hamster, (int)ball_radius*2, (int)ball_radius*2, false);
        float angle = (float)Math.atan2(vx, -vy);
        //RotateBitmap(hamster_drawable, angle);
        Matrix matrix = new Matrix();
        //-matrix.setScale(1,1);
        matrix.postRotate((float)(angle*180/Math.PI));
        if (hamster1.getWidth() <= 0 || hamster1.getHeight() <= 0) {
            throw new RuntimeException(String.format("Wrong hamster1 size: %d %d", hamster1.getWidth(), hamster1.getHeight()));
        }
        Bitmap hamster_drawable = Bitmap.createBitmap(hamster1, 0, 0, hamster1.getWidth(), hamster1.getHeight(), matrix, false);
        //Rect rectDest = new Rect(0, 0, hamster.getWidth(), hamster.getHeight());
        drawFromCenter(canvas, hamster_drawable, width/2, height/2, ballPaint);
    }

    public void drawFromCenter (Canvas canvas, Bitmap bitmap, float centerX, float centerY, Paint paint) {
        float r = bitmap.getWidth()/2;
        canvas.drawBitmap(bitmap, centerX - r, centerY - r, paint);
    }

  /*  public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }*/

    public void collisionWithWall (Labyrinth.Wall wall) {
        //vector 1, l=1, parallel to the wall
        Log.i("Ball.collisionWithWall",
                String.format("at the beginning: x=%.2f y=%.2f vx=%.2f vy=%.2f wall: %.2f:%.2f - %.2f:%.2f",
                x, y, vx, vy, wall.begin.x, wall.begin.y, wall.end.x, wall.end.y));
        float[] touchDetails = collisionsCalc.wallTouchDetailsA(wall, x, y);
        float[] prevTouchDetails = collisionsCalc.wallTouchDetailsA(wall, prevX, prevY);
        if (prevTouchDetails[2] < Radius) {
            //throw new RuntimeException("Ball is already too close");
        }
        Log.i("Ball.collisionWithWall", String.format("touchDetails: prev: x=%.2f y=%.2f tx=%.2f ty=%.2f dev=%.2f"
                + "; curr: x=%.2f y=%.2f tx=%.2f ty=%.2f dev=%.2f",
                prevX, prevY, prevTouchDetails[0], prevTouchDetails[1], prevTouchDetails[3],
                x, y, touchDetails[0], touchDetails[1], touchDetails[3]));
        Labyrinth.Vector ort_against = new Labyrinth.Vector(prevX - prevTouchDetails[0], prevY - prevTouchDetails[1]);
        ort_against.normalize();

        x = touchDetails[0] + Radius * ort_against.x * 1.01f;
        y = touchDetails[1] + Radius * ort_against.y * 1.01f;

        float[] newTouchDetails = collisionsCalc.wallTouchDetailsA(wall, x, y);
        if (prevTouchDetails[3] * newTouchDetails[3] < 0) {
            Log.w("Ball.collisionWithWall", String.format("Tunneled! prev: x=%.2f y=%.2f tx=%.2f ty=%.2f dev=%.2f"
                            + "; new: x=%.2f y=%.2f tx=%.2f ty=%.2f dev=%.2f"
                            + "; wall=%.2f:%.2f--%.2f:%.2f"
                            + "; ort_vec=%.2f:%.2f",
                    prevX, prevY, prevTouchDetails[0], prevTouchDetails[1], prevTouchDetails[3],
                    x, y, newTouchDetails[0], newTouchDetails[1], newTouchDetails[3],
                    wall.begin.x, wall.begin.y, wall.end.x, wall.end.y,
                    ort_against.x, ort_against.y));
            //throw new RuntimeException("Tunneled");
        }

        float wall_vec_x = wall.par_vec.x;
        float wall_vec_y = wall.par_vec.y;

        float speed_along_wall = vx * wall_vec_x + vy * wall_vec_y;
        vx = wall_vec_x * speed_along_wall;
        vy = wall_vec_y * speed_along_wall;

        Log.i("Ball.collisionWithWall", String.format("at the end: x=%.2f y=%.2f vx=%.2f vy=%.2f",
                    x, y, vx, vy));
    }

    public void collisionWithPoint (Labyrinth.Point point, boolean is_end, Labyrinth.Wall wall) {
        float x0 = prevX, x1 = x, y0 = prevY, y1 = y;
        // Reduce contact zone to 0.01 wide
        while ((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1) > 0.0001) {
            float midX = (x0 + x1) / 2, midY = (y0 + y1) / 2;
            float dist = (float) Math.hypot(point.x - midX, point.y - midY);
            if (dist > Radius) {
                x0 = midX;
                y0 = midY;
            } else {
                x1 = midX;
                y1 = midY;
            }
        }
        x = x0;
        y = y0;
        boolean pos_direction = (0 < wall.par_vec.x * vx + wall.par_vec.y * vy);
        if (pos_direction == is_end) {
            Labyrinth.Vector speed = new Labyrinth.Vector(vx, vy);
            speed.reduceToProjection(wall.par_vec);
            vx = speed.x;
            vy = speed.y;
        } else {
            vx = 0;
            vy = 0;
        }
    }
}
