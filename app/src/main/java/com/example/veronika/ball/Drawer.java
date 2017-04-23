package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by veronika on 15/04/2017.
 */

public class Drawer extends View {
    Paint paint;
    Matrix matrix;
    Rect rectSrc, rectDest;
    Bitmap bg, static_bg, static_bg_original;
    Ball ball = null;
    Labyrinth labyrinth = null;
    float[] values;

    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("trace", "Drawer()");
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setARGB(1, 230, 230, 250);
        bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        static_bg_original = BitmapFactory.decodeResource(context.getResources(), R.drawable.planks);
        static_bg = Bitmap.createScaledBitmap(static_bg_original, static_bg_original.getWidth() / 2, static_bg_original.getHeight() / 2, false);
        rectSrc = new Rect(0, 0, static_bg.getWidth(), static_bg.getHeight());
        rectDest = new Rect(0, 0, static_bg.getWidth(), static_bg.getHeight());
        ball = new Ball();
    }

    void coordChange () {
        values = MainActivity.pc.valuesAccel;
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
        for(int x = (int)((200 - ball.getX() * 2 % 200) - 200); x < width; x += 200)
            for(int y = (int)((200 - ball.getY() * 2 % 200) - 200); y < height; y += 200)
                canvas.drawBitmap(bg, x, y, paint);

        final float min_dim = Math.min(width, height);
        final float x_viewPoint = (float) width * 100.0f / min_dim;
        final float y_viewPoint = (float) height * 100.0f / min_dim;
        final int x_view_min = (int) Math.floor(ball.getX() - x_viewPoint * 0.5f);
        final int x_view_max = (int) Math.ceil(ball.getX() + x_viewPoint * 0.5f);
        final int y_view_min = (int) Math.floor(ball.getY() - y_viewPoint * 0.5f);
        final int y_view_max = (int) Math.ceil(ball.getY() + y_viewPoint * 0.5f);

        float x0 = (0 - x_view_min) * (float) width  / x_viewPoint;
        float y0 = (0 - y_view_min) * (float) height / y_viewPoint;
        float x1 = (100 - x_view_min) * (float) width  / x_viewPoint;
        float y1 = (100 - y_view_min) * (float) height / y_viewPoint;
        /*for(float x = x0; x < x1; x += 100 * min_dim) {
            for (float y = y0; y < y1; y += 100 * min_dim) {
                rectDest.set((int)x, (int)y, (int)(x + 100 * min_dim), (int)(y + 100 * min_dim));
                canvas.drawBitmap(static_bg, rectSrc, rectDest, paint);
            }
        }*/
        rectDest.set((int)x0, (int)y0, (int)(x1), (int)(y1));
        canvas.drawBitmap(static_bg, rectSrc, rectDest, paint);
        labyrinth.draw(canvas, ball, width, height);
        ball.draw(canvas, width, height);
    }

    public boolean isGameFinished() {
        float l = (float) Math.hypot(labyrinth.finish.x - ball.getX(),
                labyrinth.finish.y - ball.getY());
        return (l < ball.Radius);
    }
}
