package com.example.veronika.ball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by veronika on 26/03/2017.
 */

public class Labyrinth {
    static class Point {
        float x, y;
        Point(float nx, float ny) {
            x = nx;
            y = ny;
        }
    }
    static class Vector {
        float x, y;
        Vector(float nx, float ny) {
            x = nx;
            y = ny;
        }
        void normalize() {
            float len = (float) Math.hypot(x, y);
            x /= len;
            y /= len;
        }
        Vector makeOrt() {
            return new Vector(-y, x);
        }
        void reduceToProjection(Vector basis) {
            // basis shall have length 1
            float prod = x * basis.x + y * basis.y;
            x = prod * basis.x;
            y = prod * basis.y;
        }
    }
    static class Wall {
        Point begin, end;
        Vector par_vec, ort_vec;
        Wall(float nx0, float ny0, float nx1, float ny1) {
            begin = new Point(nx0, ny0);
            end = new Point(nx1, ny1);
            par_vec = new Vector(nx1 - nx0, ny1 - ny0);
            par_vec.normalize();
            ort_vec = par_vec.makeOrt();
        }
    };
    ArrayList <Wall> walls;
    ArrayList <Point> holes;
    Point start, finish;
    Point size;
    CollisionsCalculator collisionsCalc = new CollisionsCalculator();

    public void draw(Canvas canvas, Ball ball, int width, int height) {

        //PREPARE TO DRAW
        final float min_dim = Math.min(width, height);
        final float x_viewPoint = (float) width * 100.0f / min_dim;
        final float y_viewPoint = (float) height * 100.0f / min_dim;
        final int x_view_min = (int) Math.floor(ball.getX() - x_viewPoint * 0.5f);
        final int x_view_max = (int) Math.ceil(ball.getX() + x_viewPoint * 0.5f);
        final int y_view_min = (int) Math.floor(ball.getY() - y_viewPoint * 0.5f);
        final int y_view_max = (int) Math.ceil(ball.getY() + y_viewPoint * 0.5f);
        ArrayList <Wall> vis_walls = getVisibleWalls(x_view_min, y_view_min, x_view_max, y_view_max);
        ArrayList<Point> vis_holes = getVisibleHoles(x_view_min, y_view_min, x_view_max, y_view_max);

        //WALLS
        Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(0xffbf1faf);
        wallPaint.setStrokeWidth(min_dim * 0.02f);
        for (int i = 0; i < vis_walls.size(); ++i) {
            Wall wall = vis_walls.get(i);
            float x0 = (wall.begin.x - x_view_min) * (float) width  / x_viewPoint;
            float y0 = (wall.begin.y - y_view_min) * (float) height / y_viewPoint;
            float x1 = (wall.end.x   - x_view_min) * (float) width  / x_viewPoint;
            float y1 = (wall.end.y   - y_view_min) * (float) height / y_viewPoint;
            canvas.drawLine(x0, y0, x1, y1, wallPaint);
        }

        //START AND FINISH POINTS
        Paint positionsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final float point_radius = min_dim / (2*10.0f);
        float x_start  = (start.x - x_view_min)  * (float) width  / x_viewPoint;
        float y_start  = (start.y - y_view_min)  * (float) height / y_viewPoint;
        float x_finish = (finish.x - x_view_min) * (float) width  / x_viewPoint;
        float y_finish = (finish.y - y_view_min) * (float) height / y_viewPoint;
        Log.i("Labyrinth.draw", String.format("start=%.2f:%.2f finish=%.2f:%.2f", x_start, y_start, x_finish, y_finish));
        positionsPaint.setColor(0xffaa0011);
        canvas.drawCircle(x_start, y_start, point_radius, positionsPaint);
        positionsPaint.setColor(0xffaa0088);
        canvas.drawCircle(x_finish, y_finish, point_radius, positionsPaint);

        //HOLES
        Paint holesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        positionsPaint.setColor(0xff110055);
        final float hole_radius = min_dim /10.0f;
        for (int i = 0; i < vis_holes.size(); ++i) {
            Point hole = vis_holes.get(i);
            float hx = (hole.x - x_view_min)  * (float) width  / x_viewPoint;
            float hy = (hole.y - y_view_min)  * (float) height  / y_viewPoint;
            canvas.drawCircle(hx, hy, hole_radius, holesPaint);
        }
    }

    void readLabyrinth(Context context) {
        walls = new ArrayList<>();
        holes = new ArrayList<>();
        try {
            BufferedReader bufferedReader = null;
            try {
                InputStream inputStream = context.getResources().openRawResource(R.raw.map2);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    StringReader lr = new StringReader(line);
                    Scanner lrs = new Scanner(lr);
                    if (!lrs.hasNext()) {
                        continue;
                    }
                    String kind = lrs.next();
                    if (kind.equals("//")) {
                        continue;
                    }
                    if (kind.equals("w")) {
                        int x0 = lrs.nextInt();
                        int x1 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        int y1 = lrs.nextInt();
                        walls.add(new Wall(x0, x1, y0, y1));
                    }

                    if (kind.equals("start")) {
                        int x0 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        start = new Point(x0, y0);
                    }

                    if (kind.equals("finish")) {
                        int x0 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        finish = new Point(x0, y0);
                    }

                    if (kind.equals("size")) {
                        int x0 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        size = new Point(x0, y0);
                    }

                    if (kind.equals("h") || kind.equals("hole")) {
                        int x0 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        holes.add(new Point(x0, y0));
                    }

                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (Resources.NotFoundException nfex) {
            nfex.printStackTrace();
        }
    }


    boolean wallIsTouched(Wall wall, float ball_x, float ball_y) {
        float[] wallDetails = collisionsCalc.wallTouchDetailsA(wall, ball_x, ball_y);
        float distance = wallDetails[2];
        float touchX = wallDetails[0], touchY = wallDetails[1];
        float minx = Math.min(wall.begin.x, wall.end.x), maxx = Math.max(wall.begin.x, wall.end.x);
        float miny = Math.min(wall.begin.y, wall.end.y), maxy = Math.max(wall.begin.y, wall.end.y);
        return ((distance < 10.0f) && (touchX <= maxx + 0.01f)  && (touchX >= minx - 0.01f)
                                   && (touchY <= maxy + 0.01f)  && (touchY >= miny - 0.01f));
    }

    boolean pointIsTouched (Point point, float ball_x, float ball_y) {
        return (Math.hypot(point.x - ball_x, point.y - ball_y) < 10.0f);
    }

    void checkWallTouchAndReact(Drawer drawer) {
        Ball ball = drawer.ball;
        ArrayList<Wall> vis_walls = getWallsVisibleAtScreen(ball.getX(), ball.getY(),
                                        drawer.getWidth(), drawer.getHeight());
        for (int i = 0; i < vis_walls.size(); i++) {
            Wall wall = vis_walls.get(i);
            // NB each collisionWithX() can change ball position => don't cache it
            if (pointIsTouched(wall.begin, ball.getX(), ball.getY())) {
                ball.collisionWithPoint(wall.begin, false, wall);
            }
            else if (pointIsTouched(wall.end, ball.getX(), ball.getY())) {
                ball.collisionWithPoint(wall.end, true, wall);
            }
            else if (wallIsTouched(wall, ball.getX(), ball.getY())) {
                ball.collisionWithWall(wall);
            }
        }
    }

    boolean checkHoleTouch (Drawer drawer) {
        Ball ball = drawer.ball;
        ArrayList<Point> vis_holes = getHolesVisibleAtScreen(ball.getX(), ball.getY(),
                drawer.getWidth(), drawer.getHeight());
        for (int i = 0; i < vis_holes.size(); i++) {
            Point hole = vis_holes.get(i);
            if (pointIsTouched(hole, ball.getX(), ball.getY())) {
                return true;
            }
        }
        return false;
    }

    ArrayList<Wall> getWallsVisibleAtScreen(float x, float y, int width, int height) {
        final float min_dim = Math.min(width, height);
        final float x_viewPoint = (float) width * 100.0f / min_dim;
        final float y_viewPoint = (float) height * 100.0f / min_dim;
        final int x_view_min = (int) Math.floor(x - x_viewPoint * 0.5f);
        final int x_view_max = (int) Math.ceil(x + x_viewPoint * 0.5f);
        final int y_view_min = (int) Math.floor(y - y_viewPoint * 0.5f);
        final int y_view_max = (int) Math.ceil(y + y_viewPoint * 0.5f);
        ArrayList <Wall> vis_walls = getVisibleWalls(x_view_min, y_view_min, x_view_max, y_view_max);
        return vis_walls;
    }

    ArrayList<Point> getHolesVisibleAtScreen(float x, float y, int width, int height) {
        final float min_dim = Math.min(width, height);
        final float x_viewPoint = (float) width * 100.0f / min_dim;
        final float y_viewPoint = (float) height * 100.0f / min_dim;
        final int x_view_min = (int) Math.floor(x - x_viewPoint * 0.5f);
        final int x_view_max = (int) Math.ceil(x + x_viewPoint * 0.5f);
        final int y_view_min = (int) Math.floor(y - y_viewPoint * 0.5f);
        final int y_view_max = (int) Math.ceil(y + y_viewPoint * 0.5f);
        ArrayList<Point> vis_holes = getVisibleHoles(x_view_min, y_view_min, x_view_max, y_view_max);
        return vis_holes;
    }

    ArrayList <Wall> getVisibleWalls(float x0, float y0, float x1, float y1) {
        ArrayList<Wall> visible = new ArrayList<>();
        for (int i = 0; i < walls.size(); i++) {
            Wall current_wall = walls.get(i);
            if (cross(current_wall.begin.x, current_wall.end.x, x0, x1) &&
                    cross(current_wall.begin.y, current_wall.end.y, y0, y1)) {
                visible.add(current_wall);
            }
        }
        return visible;
    }

    ArrayList <Point> getVisibleHoles(float x0, float y0, float x1, float y1) {
        ArrayList<Point> visible = new ArrayList<>();
        for (int i = 0; i < holes.size(); i++) {
            Point current_hole = holes.get(i);
            if (current_hole.x > x0 &&
                current_hole.x < x1 &&
                current_hole.y > y0 &&
                current_hole.y < y1 ) {
                visible.add(current_hole);
            }
        }
        return visible;
    }

    boolean cross (float fb, float fe, float sb, float se) {
        return ( ( Math.min(fb, fe) < Math.max(sb, se) ) != ( Math.max(fb, fe) < Math.min(sb, se) ) );
    }
}
