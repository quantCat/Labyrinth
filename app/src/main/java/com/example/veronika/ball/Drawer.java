package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by veronika on 15/04/2017.
 */

public class Drawer extends View {
    Paint paint;
    Matrix matrix;
    Rect rectSrcForBackground, rectDestForBackground;
    Bitmap farther_background, nearer_background;
    Ball ball = null;
    Labyrinth labyrinth = null;
    float[] values;

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("trace", "Drawer()");
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setARGB(1, 230, 230, 250);
        farther_background = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        Bitmap nearer_background_original = BitmapFactory.decodeResource(context.getResources(), R.drawable.planks);
        nearer_background = Bitmap.createScaledBitmap(nearer_background_original,
                nearer_background_original.getWidth()/2, nearer_background_original.getHeight()/2,
                false);
        rectSrcForBackground = new Rect(0, 0, nearer_background.getWidth(), nearer_background.getHeight());
        rectDestForBackground = new Rect(0, 0, nearer_background.getWidth(), nearer_background.getHeight());
        ball = new Ball();
    }

    void coordChange () {
        values = GameActivity.pc.valuesAccel;
        //Log.i("Drawer.coordChange", String.format("%.3f %.3f", values[0], values[1]));
        ball.coordChange(values[0], values[1]);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        //Log.i("trace", "onDraw() called");
        int width = getWidth();
        int height = getHeight();
        super.onDraw(canvas);

        //canvas.drawColor(Color.rgb(230,230,250));
        final int fbgw = farther_background.getWidth();
        for(int x = (int)((fbgw - ball.getX() * 2 % fbgw) - fbgw); x < width; x += fbgw) {
            for (int y = (int) ((fbgw - ball.getY() * 2 % fbgw) - fbgw); y < height; y += fbgw) {
                canvas.drawBitmap(farther_background, x, y, paint);
            }
        }

        labyrinth.draw(getContext(), canvas, this, paint, ball, width, height);
        ball.draw(getContext(), canvas, width, height);
    }

    public boolean isGameFinished() {
        float l = (float) Math.hypot(labyrinth.finish.x - ball.getX(),
                labyrinth.finish.y - ball.getY());
        return (l < ball.Radius);
    }
}
