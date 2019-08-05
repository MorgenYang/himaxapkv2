package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.ln.himaxtouch.DataMonitor.DataMonitorConfig;

/**
 * Created by 903622 on 2018/6/8.
 */

public class MonitorGridView extends View {

    private Paint mPaint;
    private int mRow;
    private int mCol;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mTransformType;
    private boolean isTransform = false;

    public MonitorGridView(Context context, int rowNum, int colNum, int width, int height, int transformType, boolean isShowAreaInfo) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#99000000"));
        this.setBackgroundColor(Color.parseColor("#00000000"));
        this.setTranslationX(0);
        this.setTranslationY(0);

        mLayoutWidth = width;
        mLayoutHeight = height;


        mTransformType = transformType;

        if((mLayoutWidth > mLayoutHeight && colNum > rowNum) || (mLayoutHeight > mLayoutWidth && rowNum > colNum)) {
            isTransform = true;
            mRow = colNum;
            mCol = rowNum;
        } else {
            mRow = rowNum;
            mCol = colNum;
        }

        if(isShowAreaInfo) {
            mCol += 2;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width_step = (float)mLayoutWidth/(float)(mRow);
        float length_step = (float)mLayoutHeight/(float)(mCol);

//        switch (mTransformType) {
//            case DataMonitorConfig.TRANSFORM_DATA_ONE: {
//                for(int i=1;i<mRow;i++) {
//                    float x = width_step*(float)i;
//                    canvas.drawLine(x, length_step, x, mLayoutHeight, mPaint);
//                }
//                for(int j=1;j<mCol;j++) {
//                    float y = length_step*(float)j;
//                    canvas.drawLine(width_step, y, mLayoutWidth, y, mPaint);
//                }
//            } break;
//            case DataMonitorConfig.TRANSFORM_DATA_TWO: {
//                for(int i=1;i<mRow;i++) {
//                    float x = width_step*(float)i;
//                    canvas.drawLine(x, 0, x, mLayoutHeight-length_step, mPaint);
//                }
//                for(int j=1;j<mCol;j++) {
//                    float y = length_step*(float)j;
//                    canvas.drawLine(width_step, y, mLayoutWidth, y, mPaint);
//                }
//            } break;
//            case DataMonitorConfig.TRANSFORM_DATA_THREE: {
//                for(int i=1;i<mRow;i++) {
//                    float x = width_step*(float)i;
//                    canvas.drawLine(x, length_step, x, mLayoutHeight, mPaint);
//                }
//                for(int j=1;j<mCol;j++) {
//                    float y = length_step*(float)j;
//                    canvas.drawLine(0, y, mLayoutWidth-width_step, y, mPaint);
//                }
//            } break;
//            case DataMonitorConfig.TRANSFORM_DATA_FOUR: {
//                for(int i=1;i<mRow;i++) {
//                    float x = width_step*(float)i;
//                    canvas.drawLine(x, 0, x, mLayoutHeight-length_step, mPaint);
//                }
//                for(int j=1;j<mCol;j++) {
//                    float y = length_step*(float)j;
//                    canvas.drawLine(0, y, mLayoutWidth-width_step, y, mPaint);
//                }
//            } break;
//            default:
//                break;
//        }

        for(int i=1;i<mRow;i++) {
            float x = width_step*(float)i;
            canvas.drawLine(x, 0, x, mLayoutHeight, mPaint);
        }
        for(int j=1;j<mCol;j++) {
            float y = length_step*(float)j;
            canvas.drawLine(0, y, mLayoutWidth, y, mPaint);
        }


    }
}
