package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

/**
 * Created by 903622 on 2018/4/17.
 */

public class TrackPatternTestView extends View {

    public String mPathToSave;
    public String mProgress;
    public String mMessage;

    private TextPaint mPaintText;

    public TrackPatternTestView(Context context, String pathToSave, String progress, String msg) {
        super(context);

        mPathToSave = pathToSave;
        mProgress = progress;
        mMessage = msg;

        mPaintText = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(20);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        this.setBackgroundColor(Color.parseColor("#00000000"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenter(canvas, mPaintText, "File Path: " + mPathToSave, 100);
        drawCenter(canvas, mPaintText, "Status: " + mProgress, 130);
        drawCenter(canvas, mPaintText, mMessage, 180);
    }


    private Rect r = new Rect();

    private void drawCenter(Canvas canvas, Paint paint, String text, float shiftY) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y + shiftY, paint);
    }
}
