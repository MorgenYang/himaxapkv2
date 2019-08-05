package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import com.ln.himaxtouch.ObjectiveTest.ObjectiveTestController;
import com.ln.himaxtouch.ObjectiveTest.SnrModel;

import java.text.DecimalFormat;

/**
 * Created by 903622 on 2018/3/30.
 */

public class SNRTestResultView extends View {

    private Paint paint;
    private TextPaint paint_text;
    private TextPaint paint_text_fail;
    private SnrModel model;
    private String gLog = new String();
    private ObjectiveTestController.OTCSaveLog otc = new ObjectiveTestController.OTCSaveLog();

    public SNRTestResultView(Context context, SnrModel model) {
        super(context);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint_text = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint_text.setColor(Color.WHITE);
        paint_text.setTextSize(23);

        paint_text_fail = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint_text_fail.setColor(Color.RED);
        paint_text_fail.setTextSize(23);
        this.model = model;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int text_width = 300;
        int text_height = 310;
        float x = model.mTouchedX;
        float y = model.mTouchedY;

        if (x + text_width > canvas.getWidth()) {
            x -= (text_width + 50);
        }
        if (y + text_height > canvas.getHeight()) {
            y -= (text_height + 50);
        }
        gLog = "";

        DecimalFormat df = new DecimalFormat("#.00");

        canvas.drawCircle(model.mTouchedX, model.mTouchedY, 50, paint);
        canvas.drawText("Signal: ", x + 60, y + 30, paint_text);
        canvas.drawText(df.format(model.mResult_Signal) + "", x + 200, y + 30, paint_text);
        gLog += "Signal," + df.format(model.mResult_Signal) + "\n";
        canvas.drawText("Noise: ", x + 60, y + 60, paint_text);
        canvas.drawText(df.format(model.mResult_Noise) + "", x + 200, y + 60, paint_text);
        gLog += "Noise," + df.format(model.mResult_Noise) + "\n";
        canvas.drawText("SNR: ", x + 60, y + 90, paint_text);
        canvas.drawText(df.format(model.mResult_SNR) + "", x + 200, y + 90, paint_text);
        gLog += "SNR," + df.format(model.mResult_SNR) + "\n";
        canvas.drawText("SNR (MAX): ", x + 60, y + 120, paint_text);
        canvas.drawText(df.format(model.mResult_SNR_MAX) + "", x + 200, y + 120, paint_text);
        gLog += "SNR(MAX)," + df.format(model.mResult_SNR_MAX) + "\n";

        if (model.mMaxSignalForBP / 2 < model.mMaxDCForBP) {
            canvas.drawText("[BP]Signal: ", x + 60, y + 150, paint_text);
            canvas.drawText(model.mMaxSignalForBP + "", x + 200, y + 150, paint_text);
            gLog += "[BP]Signal," + df.format(model.mMaxSignalForBP) + "\n";
            canvas.drawText("[BP]Dc: ", x + 60, y + 180, paint_text);
            canvas.drawText(model.mMaxDCForBP + "", x + 200, y + 180, paint_text);
            gLog += "[BP]Dc," + df.format(model.mMaxDCForBP) + "\n";
            canvas.drawText("[BP]S/D(%): ", x + 60, y + 210, paint_text);
            model.mMaxDCForBP = (model.mMaxDCForBP == 0) ? 1 : model.mMaxDCForBP;
            int per = (int) ((long) model.mMaxSignalForBP * 100 / model.mMaxDCForBP);
            if (per >= 60) {
                canvas.drawText(per + " %", x + 200, y + 210, paint_text_fail);
            } else {
                canvas.drawText(per + " %", x + 200, y + 210, paint_text);
            }
            gLog += "[BP]S/D(%)," + per + " %" + "\n";
        }
        gLog += "RawBaseData," + "\n";
        for (int i = 0; i < model.mRowNum; i++) {
            for (int j = 0; j < model.mColNum; j++) {
                gLog += (model.mRawBaseData[i][j] + ",");
                if (j == model.mColNum - 1)
                    gLog += "\n";

            }
        }
        Log.d("HXTP", "aaa:" + "ondraw" + gLog);
        otc.SetString(gLog);
        otc.UpdateString();
        //otc.SaveLog(gLog);
    }

}