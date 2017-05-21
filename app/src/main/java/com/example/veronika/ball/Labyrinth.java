package com.example.veronika.ball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.Log;
import android.widget.Toast;

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
    ArrayList<Point> stars;
    Point start, finish;
    Point size;
    CollisionsCalculator collisionsCalc = new CollisionsCalculator();
    int stars_collected = 0;

    public void draw(Context context, Canvas canvas, Drawer drawer, Paint nbgPaint,
                     Ball ball, int width, int height) {

        //PREPARE TO DRAW
        final float min_dim = Math.min(width, height);
        final float lab_x_size = (float) width * 100.0f / min_dim;
        final float lab_y_size = (float) height * 100.0f / min_dim;
        final int lab_x_view_min = (int) Math.floor(ball.getX() - lab_x_size * 0.5f);
        final int lab_x_view_max = (int) Math.ceil(ball.getX() + lab_x_size * 0.5f);
        final int lab_y_view_min = (int) Math.floor(ball.getY() - lab_y_size * 0.5f);
        final int lab_y_view_max = (int) Math.ceil(ball.getY() + lab_y_size * 0.5f);
        ArrayList <Wall> vis_walls = getVisibleWalls(lab_x_view_min, lab_y_view_min, lab_x_view_max, lab_y_view_max);
        ArrayList<Point> vis_holes = getVisibleHoles(lab_x_view_min, lab_y_view_min, lab_x_view_max, lab_y_view_max);
        ArrayList<Point> vis_stars = getVisibleStars(lab_x_view_min, lab_y_view_min, lab_x_view_max, lab_y_view_max);

        // Nearer background (only within labyrinth)
        int screen_x_lab_min = (int)Math.floor((0 - lab_x_view_min) * (float) width  / lab_x_size);
        int screen_y_lab_min = (int)Math.ceil((0 - lab_y_view_min) * (float) height  / lab_y_size);
        int screen_x_lab_max = (int)Math.floor((size.x - lab_x_view_min) * (float) width  / lab_x_size);
        int screen_y_lab_max = (int)Math.ceil((size.y - lab_y_view_min) * (float) height  / lab_y_size);
        int screen_nbg_x = drawer.nearer_background.getWidth();
        int screen_nbg_y = drawer.nearer_background.getHeight();
        for (int xx = screen_x_lab_min; xx <= screen_x_lab_max; xx += screen_nbg_x) {
            int clip_xx = Math.min(xx + screen_nbg_x, screen_x_lab_max);
            for (int yy = screen_y_lab_min; yy <= screen_y_lab_max; yy += screen_nbg_y) {
                int clip_yy = Math.min(yy + screen_nbg_y, screen_y_lab_max);
                canvas.clipRect(xx, yy, clip_xx, clip_yy, Region.Op.REPLACE);
                canvas.drawBitmap(drawer.nearer_background, xx, yy, nbgPaint);
            }
        }
        canvas.clipRect(0, 0, width, height, Region.Op.REPLACE);

        //WALLS
        Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wallPaint.setColor(0xffbf1faf);
        wallPaint.setStrokeWidth(min_dim * 0.02f);
        for (int i = 0; i < vis_walls.size(); ++i) {
            Wall wall = vis_walls.get(i);
            float screen_x_begin = (wall.begin.x - lab_x_view_min) * (float) width  / lab_x_size;
            float screen_y_begin = (wall.begin.y - lab_y_view_min) * (float) height / lab_y_size;
            float screen_x_end = (wall.end.x   - lab_x_view_min) * (float) width  / lab_x_size;
            float screen_y_end = (wall.end.y   - lab_y_view_min) * (float) height / lab_y_size;
            canvas.drawLine(screen_x_begin, screen_y_begin, screen_x_end, screen_y_end, wallPaint);
        }

        //START AND FINISH POINTS
        Paint positionsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final float point_radius = min_dim / (2*10.0f);
        float screen_x_start  = (start.x - lab_x_view_min)  * (float) width  / lab_x_size;
        float screen_y_start  = (start.y - lab_y_view_min)  * (float) height / lab_y_size;
        float screen_x_finish = (finish.x - lab_x_view_min) * (float) width  / lab_x_size;
        float screen_y_finish = (finish.y - lab_y_view_min) * (float) height / lab_y_size;
        Log.i("Labyrinth.draw", String.format("start=%.2f:%.2f finish=%.2f:%.2f",
                screen_x_start, screen_y_start, screen_x_finish, screen_y_finish));
        positionsPaint.setColor(0xffaa0011);
        canvas.drawCircle(screen_x_start, screen_y_start, point_radius, positionsPaint);
        positionsPaint.setColor(0xffaa0088);
        canvas.drawCircle(screen_x_finish, screen_y_finish, point_radius, positionsPaint);

        //HOLES
        Paint holesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        holesPaint.setColor(0xff110055);
        final float hole_radius = min_dim / 10.0f;
        for (int i = 0; i < vis_holes.size(); ++i) {
            Point hole = vis_holes.get(i);
            float screen_x_hole = (hole.x - lab_x_view_min)  * (float) width / lab_x_size;
            float screen_y_hole = (hole.y - lab_y_view_min)  * (float) height / lab_y_size;
            canvas.drawCircle(screen_x_hole, screen_y_hole, hole_radius, holesPaint);
        }

        //STARS
        Paint starsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        holesPaint.setColor(0xff110055);
        Bitmap star_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.star);
        Bitmap star_drawable = Bitmap.createScaledBitmap(star_bitmap, (int)hole_radius*2, (int)hole_radius*2, false);
        for (int i = 0; i < vis_stars.size(); ++i) {
            Point star = vis_stars.get(i);
            float screen_x_star = (star.x - lab_x_view_min)  * (float) width / lab_x_size;
            float screen_y_star = (star.y - lab_y_view_min)  * (float) height / lab_y_size;
            drawFromCenter(canvas, star_drawable, screen_x_star, screen_y_star, starsPaint);
        }
    }

    public void drawFromCenter (Canvas canvas, Bitmap bitmap, float centerX, float centerY, Paint paint) {
        float r = bitmap.getWidth()/2;
        canvas.drawBitmap(bitmap, centerX - r, centerY - r, paint);
    }

    void readLabyrinth(Context context, int id) {
        walls = new ArrayList<>();
        holes = new ArrayList<>();
        stars = new ArrayList<>();
        try {
            BufferedReader bufferedReader = null;
            try {
                InputStream inputStream = context.getResources().openRawResource(id);
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

                    if (kind.equals("s") || kind.equals("star")) {
                        int x0 = lrs.nextInt();
                        int y0 = lrs.nextInt();
                        stars.add(new Point(x0, y0));
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

    boolean checkAndReactStarTouch (Drawer drawer) {
        Ball ball = drawer.ball;
        ArrayList<Point> vis_stars = getStarsVisibleAtScreen(ball.getX(), ball.getY(),
                drawer.getWidth(), drawer.getHeight());
        for (int i = 0; i < vis_stars.size(); i++) {
            Point hole = vis_stars.get(i);
            if (pointIsTouched(hole, ball.getX(), ball.getY())) {
                stars.remove(i);
                stars_collected++;
                return true;
            }
        }
        return false;
    }

    boolean checkHoleTouch(Drawer drawer) {
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

    ArrayList<Point> getStarsVisibleAtScreen(float x, float y, int width, int height) {
        final float min_dim = Math.min(width, height);
        final float x_viewPoint = (float) width * 100.0f / min_dim;
        final float y_viewPoint = (float) height * 100.0f / min_dim;
        final int x_view_min = (int) Math.floor(x - x_viewPoint * 0.5f);
        final int x_view_max = (int) Math.ceil(x + x_viewPoint * 0.5f);
        final int y_view_min = (int) Math.floor(y - y_viewPoint * 0.5f);
        final int y_view_max = (int) Math.ceil(y + y_viewPoint * 0.5f);
        ArrayList<Point> vis_stars = getVisibleStars(x_view_min, y_view_min, x_view_max, y_view_max);
        return vis_stars;
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

    ArrayList <Point> getVisibleStars(float x0, float y0, float x1, float y1) {
        ArrayList<Point> visible = new ArrayList<>();
        for (int i = 0; i < stars.size(); i++) {
            Point current_star = stars.get(i);
            if (current_star.x > x0 &&
                    current_star.x < x1 &&
                    current_star.y > y0 &&
                    current_star.y < y1 ) {
                visible.add(current_star);
            }
        }
        return visible;
    }

    boolean cross (float fb, float fe, float sb, float se) {
        return ( ( Math.min(fb, fe) < Math.max(sb, se) ) != ( Math.max(fb, fe) < Math.min(sb, se) ) );
    }
}
