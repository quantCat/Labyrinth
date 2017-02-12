package com.example.veronika.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by veronika on 12/02/2017.
 */

public class DrawBall extends View {
    public DrawBall(Context context) {
        super(context);
        Log.i("trace", "DrawBall/1()");
    }
    public DrawBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("trace", "DrawBall/2()");
    }
    protected void onDraw(Canvas canvas) {
        Log.i("trace", "onDraw() called");
        super.onDraw(canvas);
        //// Create a LinearLayout in which to add the ImageView
        //mLinearLayout = new LinearLayout(this);
        //drawPath = new Path();
        //drawPaint = new Paint();
        //drawPaint.setColor(paintColor);
        //drawPaint.setAntiAlias(true);
        //drawPaint.setStrokeWidth(20);
        //drawPaint.setStyle(Paint.Style.STROKE);
        //drawPaint.setStrokeJoin(Paint.Join.ROUND);
        //drawPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xff808000);
        canvas.drawCircle(200, 200, 50, mTextPaint);
    }
}
