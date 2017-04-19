package com.example.veronika.ball;

/**
 * Created by veronika on 19/04/2017.
 */

public class CollisionsCalculator {

    float[] wallTouchDetails (Labyrinth.Wall wall, float x, float y) {
        float a = wall.end.y - wall.begin.y;
        float b = wall.begin.x - wall.end.x;
        float c = wall.end.y * (wall.end.x - wall.begin.x) -
                wall.begin.x * (wall.end.y - wall.begin.y);
        float distance = (float) (Math.abs(a*x + b*y + c) / Math.hypot(a, b));
        float touchX = (b* (b*x - a*y) - a*c)/(a*a+b*b);
        float touchY = (a*(-b*x + a*y) - b*c)/(a*a+b*b);
        float[] returnValue = {distance, touchX, touchY};
        return returnValue;
    }
}
