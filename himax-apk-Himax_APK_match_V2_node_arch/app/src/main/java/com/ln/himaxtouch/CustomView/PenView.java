package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

public class PenView extends View {


    private Paint mHoverPaint = new Paint();
    private TextPaint mPaintText = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    private Paint mContactPaint = new Paint();
    private Paint mFingerPaint = new Paint();

    private PointInfo mHover = new PointInfo();

    public Map<Integer, ArrayList<PointInfo>> mFingerPoints;
    public ArrayList<PointInfo> mPenPoints = new ArrayList<PointInfo>();

    public boolean isShowHover = true;

    private static class PointInfo {
        float x = 0;
        float y = 0;
        int action_statue = 0;
        float presure = 0;
        int action_button = 0;
    }

    public PenView(Context context) {
        super(context);
        this.setBackgroundColor(Color.WHITE);
        mHoverPaint.setColor(Color.YELLOW);
        mHoverPaint.setAlpha(40);
        mContactPaint.setColor(Color.BLUE);
        mFingerPaint.setColor(Color.RED);

        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(23);

        //init
        mHover.action_statue = MotionEvent.ACTION_HOVER_EXIT;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        mHover.x = event.getX();
        mHover.y = event.getY();
        mHover.action_statue = event.getAction();
        this.invalidate();

        return super.onHoverEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mPenPoints.size() > 0) {
            for(int i=0; i<mPenPoints.size(); i++) {
                mContactPaint.setStrokeWidth(mPenPoints.get(i).presure * 4);
                canvas.drawCircle(mPenPoints.get(i).x, mPenPoints.get(i).y, mPenPoints.get(i).presure*2, mContactPaint);
                if(i < mPenPoints.size()-1 && mPenPoints.get(i).action_statue == MotionEvent.ACTION_MOVE) {
                    canvas.drawLine(mPenPoints.get(i).x, mPenPoints.get(i).y,mPenPoints.get(i+1).x, mPenPoints.get(i+1).y, mContactPaint);
                }

            }
        }

        if(isShowHover) {
            if (mHover.action_statue != MotionEvent.ACTION_HOVER_EXIT) {
                canvas.drawCircle(mHover.x, mHover.y, 30, mHoverPaint);
                canvas.drawText("Hover", mHover.x - 30, mHover.y + 60, mPaintText);
            }
        }

    }

    public void onMyTouchEvent(MotionEvent event) {
        if(event.getToolType(0) != MotionEvent.TOOL_TYPE_STYLUS) {
            return;
        }
        int evAction = event.getAction() & MotionEvent.ACTION_MASK;
        switch (evAction) {
            case MotionEvent.ACTION_DOWN:
                if(mPenPoints.size() > 0) {
                    PointInfo p = mPenPoints.get(mPenPoints.size()-1);
                    p.action_statue = evAction;
                    mPenPoints.add(p);
                } else {
                    PointInfo p = new PointInfo();
                    p.action_statue = evAction;
                    p.x = event.getX();
                    p.y = event.getY();
                    p.presure = event.getPressure();
                    mPenPoints.add(p);
                }
                break;

            case MotionEvent.ACTION_MOVE: {
                for(int i=0;i<event.getHistorySize();i++) {
                    PointInfo p = new PointInfo();
                    p.presure = event.getHistoricalPressure(0, i);
                    p.x = event.getHistoricalX(0, i);
                    p.y = event.getHistoricalY(0, i);
                    p.action_statue = evAction;
                    mPenPoints.add(p);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(mPenPoints.size() > 0) {
                    PointInfo p = mPenPoints.get(mPenPoints.size()-1);
                    p.action_statue = evAction;
                    mPenPoints.add(p);
                }
                break;
            }
                default:
                    break;
        }
        this.invalidate();
    }
}
