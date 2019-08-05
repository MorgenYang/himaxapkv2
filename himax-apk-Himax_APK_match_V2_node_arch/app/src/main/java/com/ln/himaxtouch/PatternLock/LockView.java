package com.ln.himaxtouch.PatternLock;

/**
 * Created by 903622 on 2018/6/6.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.ln.himaxtouch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzj on 2016/11/15.
 */
public class LockView extends View {

    private String TAG = "LockView";

    private Point[][] points = new Point[3][3];
    private boolean isInit, isSelectPoint, isFinsh, moveNoPint;

    private float width, height, offsetsX, offsetsY, bitmapR, eventX, eventY;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private Matrix matrix = new Matrix();

    private List<Point> pointList = new ArrayList<>();

    private Bitmap pointNormal, pointPressed, pointError, linePressed, lineError;

    private onPatternChangeListener onPatternChangeListener;

    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoints();
        }
        canvasPoints(canvas);

        if (pointList.size() > 0) {

            Point a = pointList.get(0);
            for (int i = 0; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                lineCanvas(canvas, a, b);
                a = b;
            }

            if (moveNoPint) {
                lineCanvas(canvas, a, new Point(eventX, eventY));
            }
        }
    }

    private void lineCanvas(Canvas canvas, Point a, Point b) {
        float lineLenght = (float) Point.distance(a, b);

        float degrees = getDegrees(a, b);

        canvas.rotate(degrees, a.x, a.y);

        if (a.state == Point.STATE_PRESSED) {
            matrix.setScale(lineLenght / linePressed.getWidth(), 1);
            matrix.postTranslate(a.x - linePressed.getWidth() / 2, a.y - linePressed.getHeight() / 2);
            canvas.drawBitmap(linePressed, matrix, paint);
        } else {
            matrix.setScale(lineLenght / lineError.getWidth(), 1);
            matrix.postTranslate(a.x - lineError.getWidth() / 2, a.y - lineError.getHeight() / 2);
            canvas.drawBitmap(lineError, matrix, paint);
        }
        canvas.rotate(-degrees, a.x, a.y);
    }

    private void canvasPoints(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_NORMAL) {
                    canvas.drawBitmap(pointNormal, point.x - bitmapR, point.y - bitmapR, paint);
                } else if (point.state == Point.STATE_PRESSED) {
                    canvas.drawBitmap(pointPressed, point.x - bitmapR, point.y - bitmapR, paint);
                } else {
                    canvas.drawBitmap(pointError, point.x - bitmapR, point.y - bitmapR, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        eventX = event.getX();
        eventY = event.getY();
        moveNoPint = false;
        isFinsh = false;
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (onPatternChangeListener != null) {
                    onPatternChangeListener.onPatternStart(true);
                }
                resetPoint();
                point = checkSelectPoint();
                if (point != null) {
                    isSelectPoint = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelectPoint) {
                    point = checkSelectPoint();

                    if (point == null) {
                        moveNoPint = true;
                    } else {
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinsh = true;
                isSelectPoint = false;

                break;


        }
        if (!isFinsh && isSelectPoint && point != null) {

            if (crossPoint(point)) {
                moveNoPint = true;
            } else {
                point.state = Point.STATE_PRESSED;
                pointList.add(point);
            }
        }
        if (isFinsh) {
            if (pointList.size() == 1) {
                resetPoint();
            } else if (pointList.size() > 1 && pointList.size() < 4) {
                errorPoint();
                if (onPatternChangeListener != null) {
                    onPatternChangeListener.onPatternChange(null);
                }
            } else if (pointList.size() >= 4) {
                String password = "";
                if (onPatternChangeListener != null) {
                    for (int i = 0; i < pointList.size(); i++) {
                        password = password + pointList.get(i).index;
                    }
                    onPatternChangeListener.onPatternChange(password);
                }
            }
        }
        postInvalidate();
        return true;
    }

    public float getDegrees(Point pointA, Point pointB) {

        return (float) Math.toDegrees(Math.atan2(pointB.y - pointA.y, pointB.x - pointA.x));

    }

    private boolean crossPoint(Point point) {
        if (pointList.contains(point)) {
            return true;
        } else {
            return false;
        }
    }

    public void resetPoint() {
        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            point.state = Point.STATE_NORMAL;
        }
        pointList.clear();
    }

    public void errorPoint() {
        for (Point point : pointList) {
            point.state = Point.STATE_ERROR;
        }
    }

    private Point checkSelectPoint() {

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.isCoincide(point.x, point.y, bitmapR, eventX, eventY)) {

                    return point;
                }
            }
        }
        return null;
    }

    private void initPoints() {
        width = getWidth();
        height = getHeight();

        if (width > height) {
            offsetsX = (width - height) / 2;
            width = height;
        } else {
            offsetsY = (height - width) / 2;
            height = width;
        }

        points[0][0] = new Point(offsetsX + width / 4, offsetsY + width / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + width / 4);
        points[0][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 4);
        points[1][0] = new Point(offsetsX + width / 4, offsetsY + width / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + width / 2);
        points[1][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 2);
        points[2][0] = new Point(offsetsX + width / 4, offsetsY + width - width / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + width - width / 4);
        points[2][2] = new Point(offsetsX + width - width / 4, offsetsY + width - width / 4);

        pointNormal = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.point_normal), 70, 70, false);
        pointPressed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.point_pressed), 95, 95, false);
        pointError = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.point_error), 70, 70, false);
        linePressed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.error_line), 5, 5, false);
        lineError = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.normal_line), 2, 2, false);

        bitmapR = pointNormal.getWidth() / 2;


        int index = 1;
        for (Point[] points : this.points) {
            for (Point point : points) {
                point.index = index;
                index++;
            }
        }


        isInit = true;
    }

    public static class Point {

        public static int STATE_NORMAL = 0;

        public static int STATE_PRESSED = 1;

        public static int STATE_ERROR = 2;
        public float x, y;
        public int index = 0, state = 0;

        public Point() {

        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }


        public static boolean isCoincide(float pointX, float pointY, float bitmapR, float eventX, float eventY) {
            return Math.sqrt((pointX - eventX) * (pointX - eventX) + (pointY - eventY) * (pointY - eventY)) < bitmapR;
        }


        public static double distance(Point a, Point b) {
            return Math.sqrt(Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y));
        }

    }

    public static interface onPatternChangeListener {
        void onPatternChange(String passwordstr);

        void onPatternStart(boolean isClick);
    }

    public void setPatternChangeListener(onPatternChangeListener onPatternChangeListener) {
        if (onPatternChangeListener != null) {
            this.onPatternChangeListener = onPatternChangeListener;
        }
    }
}