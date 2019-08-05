package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;

import com.ln.himaxtouch.ObjectiveTest.SnrModel;

import java.text.DecimalFormat;

/**
 * Created by 903622 on 2018/3/30.
 */

public class TestResultView extends View {

    private Paint paint;
    private TextPaint paint_text;
    private SnrModel model;

    public TestResultView(Context context, SnrModel model) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint_text = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint_text.setColor(Color.WHITE);
        paint_text.setTextSize(23);
        this.model = model;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int text_width = 300;
        int text_height = 200;
        float x = model.mTouchedX;
        float y = model.mTouchedY;

        if (x + text_width > canvas.getWidth()) {
            x -= (text_width + 50);
        }
        if (y + text_height > canvas.getHeight()) {
            y -= (text_height + 50);
        }


        DecimalFormat df = new DecimalFormat("#.00");

        canvas.drawCircle(model.mTouchedX, model.mTouchedY, 50, paint);
        canvas.drawText("Signal: ", x + 60, y + 30, paint_text);
        canvas.drawText(df.format(model.mResult_Signal) + "", x + 200, y + 30, paint_text);
        canvas.drawText("Noise: ", x + 60, y + 60, paint_text);
        canvas.drawText(df.format(model.mResult_Noise) + "", x + 200, y + 60, paint_text);
        canvas.drawText("SNR: ", x + 60, y + 90, paint_text);
        canvas.drawText(df.format(model.mResult_SNR) + "", x + 200, y + 90, paint_text);
        canvas.drawText("SNR (MAX): ", x + 60, y + 120, paint_text);
        canvas.drawText(df.format(model.mResult_SNR_MAX) + "", x + 200, y + 120, paint_text);
    }

}