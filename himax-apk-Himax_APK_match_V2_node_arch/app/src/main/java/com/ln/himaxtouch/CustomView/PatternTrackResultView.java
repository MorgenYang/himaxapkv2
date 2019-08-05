package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 903622 on 2018/4/19.
 */

public class PatternTrackResultView extends View {

    private static class PointRecord {
        public ArrayList<PointF> mPoints = new ArrayList<PointF>();
        public int mTouchStatus = 0;
        public int mPointId = 0;
        public PointRecord(int id) {
           mPointId = id;
        }

    }

    private int mTestStatus = 0;
    private int mCountAcionDown = 0;
    private int mCountSubFingerLeave = 0;
    private int mCountMainFingerLeave = 0;
    private int mActionDown = 1;
    public boolean isNeedDetectFastLine = false;
    private int mPreviousPointCount = 0;

    private ArrayList<PointRecord> mPoints = new ArrayList<PointRecord>();

    private Paint mPaint;
    private Paint mPaintText;
    public ObjectivePatternView.Cell[][] mPatternCells;
    private int mLayoutWidth;
    private int mLayoutLength;

    private boolean isPassTest = true;

    public PatternTrackResultView(Context context, int layout_width, int layout_length, int countActionDown, boolean isDetectFastLine) {
        super(context);
        mPaint = new Paint();
        this.setBackgroundColor(Color.parseColor("#00000000"));
        this.setTranslationX(0);
        this.setTranslationY(0);
        mLayoutWidth = layout_width;
        mLayoutLength = layout_length;
        mActionDown = countActionDown;
        isNeedDetectFastLine = isDetectFastLine;

        mPaintText = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.parseColor("#FF6633"));
        mPaintText.setTextSize(100);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTypeface(Typeface.create("Arial",Typeface.BOLD));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mTestStatus == 2) {
            for(int i=0;i<mPatternCells.length;i++) {
                for(int j=0;j<mPatternCells[0].length;j++) {
                    if(mPatternCells[i][j].isTouched) {
                        mPaint.setColor(Color.parseColor("#999999"));
                        mPaint.setStyle(Paint.Style.STROKE);
                        ObjectivePatternView.Cell c = mPatternCells[i][j];
                        if(!mPatternCells[i][j].isShouldBeTouched && !isNeedDetectFastLine) {
                            isPassTest = false;
                            mPaint.setColor(Color.parseColor("#FFAA33"));
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawLine(c.x_start, c.y_start, c.x_start+c.cell_width, c.y_start, mPaint);
                            canvas.drawLine(c.x_start, c.y_start+c.cell_width, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start+c.cell_width, c.y_start, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start, c.y_start, c.x_start, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start, c.y_start, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start+c.cell_width, c.y_start, c.x_start, c.y_start+c.cell_width, mPaint);
                        } else {
                            mPaint.setColor(Color.parseColor("#FFFFFF"));
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawLine(c.x_start, c.y_start, c.x_start+c.cell_width, c.y_start, mPaint);
                            canvas.drawLine(c.x_start, c.y_start+c.cell_width, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start+c.cell_width, c.y_start, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
                            canvas.drawLine(c.x_start, c.y_start, c.x_start, c.y_start+c.cell_width, mPaint);
                        }
                    }
                }
            }
        }
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);

        for(int i=0; i<mPoints.size(); i++) {
            PointRecord pr = mPoints.get(i);
            for(int j=1; j<pr.mPoints.size(); j++) {
                PointF p1 = pr.mPoints.get(j - 1);
                PointF p2 = pr.mPoints.get(j);
                canvas.drawCircle(p1.x,p1.y,1,mPaint);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
            }
        }

        if(mTestStatus == 2) {
            if(isNeedDetectFastLine) {
                if(!isPassTest && (mCountMainFingerLeave > 0 || mCountSubFingerLeave > 0)) {
                    mPaintText.setColor(Color.parseColor("#FFA488"));
                    drawCenter(canvas, mPaintText, "NG");
                } else {
                    mPaintText.setColor(Color.parseColor("#DDFF77"));
                    drawCenter(canvas, mPaintText, "PASS");
                }
            } else {
                if(!isPassTest) {
                    mPaintText.setColor(Color.parseColor("#FFA488"));
                    drawCenter(canvas, mPaintText, "NG");
                } else {
                    mPaintText.setColor(Color.parseColor("#DDFF77"));
                    drawCenter(canvas, mPaintText, "PASS");
                }
            }
        }
    }

    public void recordEvent(MotionEvent event) {
        int id_designed = mCountAcionDown*10 + event.getPointerId(event.getActionIndex());
        int evAction = event.getAction() & MotionEvent.ACTION_MASK;

        if(mTestStatus == 2 && mCountMainFingerLeave == 0) {
            return;
        }

        switch (evAction) {
            case MotionEvent.ACTION_DOWN: {
                PointRecord p = new PointRecord(id_designed);
                p.mTouchStatus = 1;
                mPoints.add(p);
                if(isNeedDetectFastLine && mPreviousPointCount <= event.getPointerCount()) {
                    mPreviousPointCount = event.getPointerCount();
                }
                if(mCountMainFingerLeave > 0 && isPassTest) {
                    isPassTest = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                mCountAcionDown++;
                PointRecord pr = null;
                for(PointRecord p : mPoints) {
                    if(p.mPointId == id_designed) {
                        pr = p;
                    }
                }
                if(pr!=null) {
                    pr.mTouchStatus = 2;
                }

                if(mCountAcionDown == mActionDown) {
                    mTestStatus = 2;
                }

                if(isNeedDetectFastLine && isPassTest) {
                    mCountMainFingerLeave++;
                }

                labelTouchedCell();
                this.invalidate();
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (mPoints.size() > 0) {
                    PointRecord p = new PointRecord(id_designed);
                    p.mTouchStatus = 1;
                    mPoints.add(p);
                }
                if(isNeedDetectFastLine && isPassTest) {
                    if(mPreviousPointCount <= event.getPointerCount()) {
                        mPreviousPointCount = event.getPointerCount();
                    } else {
                        isPassTest = false;
                    }
                }
                if(mCountSubFingerLeave > 0 && isPassTest) {
                    isPassTest = false;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                PointRecord pr = null;
                for (PointRecord p : mPoints) {
                    if (p.mPointId == id_designed) {
                        pr = p;
                    }
                }
                if (pr != null) {
                    pr.mTouchStatus = 2;
                }
                if(isNeedDetectFastLine && isPassTest) {
                    mCountSubFingerLeave++;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < event.getPointerCount(); i++) {
                    PointRecord pr = null;
                    for (PointRecord p : mPoints) {
                        if (p.mPointId == (mCountAcionDown*10 + event.getPointerId(i))) {
                            pr = p;
                        }
                    }
                    if (pr == null) {
                        continue;
                    }
                    for (int j = 0; j < event.getHistorySize(); j++) {
                        pr.mPoints.add(new PointF(event.getHistoricalX(i, j), event.getHistoricalY(i, j)));
                    }
                }
                if(isNeedDetectFastLine && isPassTest) {
                    if(mPreviousPointCount <= event.getPointerCount()) {
                        mPreviousPointCount = event.getPointerCount();
                    } else {
                        if(mCountSubFingerLeave == 0/* || mCountMainFingerLeave == 0*/) {
                            isPassTest = false;
                        }
                    }
                }
                this.invalidate();
                break;
            }
            default:
                break;
        }
    }

    public void labelTouchedCell() {
        for(PointRecord pr : mPoints) {
            for(int i = 0; i<pr.mPoints.size()-1; i++) {
                PointF p1 = pr.mPoints.get(i);
                PointF p2 = pr.mPoints.get(i+1);
                checkLineCrossEachCell(p1, p2);
            }
        }
    }

    private void checkLineCrossEachCell(PointF p1, PointF p2) {
        float resolution = mPatternCells[0][0].cell_width;

        int rowP1 = (int) (p1.x/resolution);
        int colP1 = (int) (p1.y/resolution);
        int rowP2 = (int) (p2.x/resolution);
        int colP2 = (int) (p2.y/resolution);

        rowP1 = (rowP1 > mPatternCells.length-1) ? mPatternCells.length-1 : rowP1;
        colP1 = (colP1 > mPatternCells[0].length-1) ? mPatternCells[0].length-1 : colP1;
        rowP2 = (rowP2 > mPatternCells.length-1) ? mPatternCells.length-1 : rowP2;
        colP2 = (colP2 > mPatternCells[0].length-1) ? mPatternCells[0].length-1 : colP2;

        mPatternCells[rowP1][colP1].isTouched = true;

        if(rowP1 > rowP2) {
            int temp = rowP2;
            rowP2 = rowP1;
            rowP1 = temp;
        }
        if(colP1 > colP2) {
            int temp = colP2;
            colP2 = colP1;
            colP1 = temp;
        }

        for(int i=rowP1; i<=rowP2; i++) {
            for(int j=colP1; j<=colP2; j++) {
                ObjectivePatternView.Cell cell = mPatternCells[i][j];

                if(cell.isTouched) {continue;}

                float x_start = cell.x_start;
                float cell_width = cell.cell_width;
                float y_start = cell.y_start;

                boolean resultTop = calculateCrossPoint(p1.x, p1.y, p2.x, p2.y, x_start, y_start, x_start+cell_width, y_start);
                if(resultTop) {
                    cell.isTouched = true;
                    continue;
                }

                boolean resultRight = calculateCrossPoint(p1.x, p1.y, p2.x, p2.y, x_start+cell_width, y_start, x_start+cell_width, y_start+cell_width);
                if(resultRight) {
                    cell.isTouched = true;
                    continue;
                }

                boolean resultBottom = calculateCrossPoint(p1.x, p1.y, p2.x, p2.y, x_start, y_start+cell_width, x_start+cell_width, y_start+cell_width);
                if(resultBottom) {
                    cell.isTouched = true;
                    continue;
                }

                boolean resultLeft = calculateCrossPoint(p1.x, p1.y, p2.x, p2.y, x_start, y_start, x_start, y_start+cell_width);
                if(resultLeft) {
                    cell.isTouched = true;
                    continue;
                }
            }
        }
    }

    private boolean calculateCrossPoint(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float x = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float y = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4)) / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        int x_cross = (int)(x+0.5);
        int y_cross = (int)(y+0.5);

        boolean result = true;
        if(x3<x4) {
            if(!(x_cross>=x3 && x_cross<=x4)) {
                result = false;
            }
        } else {
            if(!(x_cross>=x4 && x_cross<=x3)) {
                result = false;
            }
        }
        if(y3<y4) {
            if(!(y_cross>=y3 && y_cross<=y4)) {
                result = false;
            }
        } else {
            if(!(y_cross>=y4 && y_cross<=y3)) {
                result = false;
            }
        }
        return result;
    }

    private Rect r = new Rect();
    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
}
