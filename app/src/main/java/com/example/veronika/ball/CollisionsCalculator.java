package com.example.veronika.ball;

/**
 * Created by veronika on 19/04/2017.
 */

public class CollisionsCalculator {

    float[] wallTouchDetails (Labyrinth.Wall wall, float x, float y) {
        final float a = wall.begin.y - wall.end.y;
        final float b = wall.end.x - wall.begin.x;
        final float c = - wall.begin.y * b - wall.begin.x * a;
        final float h = (float) Math.hypot(a, b);
        float distance = (float) (Math.abs(a*x + b*y + c) / h);
        float touchX = (b* (b*x - a*y) - a*c)/(h*h);
        float touchY = (a*(-b*x + a*y) - b*c)/(h*h);
        float[] returnValue = {distance, touchX, touchY};
        return returnValue;
    }

    float[] normalizeVector (float vX, float vY) {
        final float vlen_tmp = len_vector(vX, vY);
        vX /= vlen_tmp;
        vY /= vlen_tmp;
        float[] vector = {vX, vY};
        return vector;
    }

    float len_vector (float x, float y) {
        return (float) Math.hypot(x, y);
    }
}
