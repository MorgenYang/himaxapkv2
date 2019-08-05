package com.ln.himaxtouch.TouchTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManualEdgeActivity extends Activity {

    Manual manual;
    RWlog rWlog = new RWlog();
    int width;
    int height;
    private int minTop;
    private int minBottom;
    private int minLeft;
    private int minRight;
    private int bottom;
    private int right;
    boolean checked;
    String time;
    String points = "";
    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
        time = df.format(new Date());

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics rmetrics = new DisplayMetrics();
        display.getRealMetrics(rmetrics);
        SharedPreferences getData = getSharedPreferences("data", MODE_PRIVATE);
        checked = getData.getBoolean("vir_key", true);
        if (!checked) {
            width = rmetrics.widthPixels;
            height = rmetrics.heightPixels;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        minTop = height;
        minBottom = 0;
        minLeft = width;
        minRight = 0;
        bottom = 0;
        right = 0;
        manual = new Manual(this);
        setContentView(manual);
    }

    class Manual extends View {

        private int mov_x;
        private int mov_y;
        private Bitmap bitmap;
        private Canvas canvas;
        DecimalFormat df = new DecimalFormat("0.00");

        Paint paint = new Paint();
        int red;
        int green;
        int blue;
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        String bgc = data.getString("bg_color_text", "");
        double lcd_width = Double.parseDouble(data.getString("lcd_width_text", ""));
        double lcd_height = Double.parseDouble(data.getString("lcd_height_text", ""));
        double a = width / lcd_width;
        double b = height / lcd_height;

        public Manual(Context context) {
            super(context);
            paint = new Paint(Paint.DITHER_FLAG);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas();
            canvas.setBitmap(bitmap);
        }

        @Override
        public void onDraw(Canvas canvas) {
            red = Integer.parseInt(bgc.substring(0, 3));
            green = Integer.parseInt(bgc.substring(3, 6));
            blue = Integer.parseInt(bgc.substring(6));
            canvas.drawColor(Color.rgb(red, green, blue));

            paint.setColor(Color.RED);
            paint.setStrokeWidth(1);
            paint.setTextSize(30);

            canvas.drawText("Width * Height:" + "(" + width + " * " + height + ")", 50, 100, paint);
            canvas.drawText("Point:" + "(" + mov_x + "," + mov_y + ")", 50, 135, paint);
            canvas.drawText("minTop:" + df.format(minTop / b) + "(mm)/" + minTop + "(pix)", 50, 170, paint);
            canvas.drawText("minBottom:" + df.format((height - minBottom) / b) + "(mm)/" + (height - minBottom) + "(pix)", 50, 205, paint);
            canvas.drawText("minLeft:" + df.format(minLeft / a) + "(mm)/" + minLeft + "(pix)", 50, 240, paint);
            canvas.drawText("minRight:" + df.format((width - minRight) / a) + "(mm)/" + (width - minRight) + "(pix)", 50, 275, paint);
            canvas.drawText("bottom:" + df.format(bottom / a) + "(mm)/" + bottom + "(pix)", 50, 310, paint);
            canvas.drawText("right:" + df.format(right / a) + "(mm)/" + right + "(pix)", 50, 345, paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                invalidate();
            }
            mov_x = (int) event.getX();
            mov_y = (int) event.getY();
            if (minTop > mov_y) {
                minTop = mov_y;
            }
            if (minLeft > mov_x) {
                minLeft = mov_x;
            }
            if (minBottom < mov_y) {
                minBottom = mov_y;
                bottom = mov_y;
            }
            if (minRight < mov_x) {
                minRight = mov_x;
                right = mov_x;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        DecimalFormat df = new DecimalFormat("0.00");
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        double lcd_width = Double.parseDouble(data.getString("lcd_width_text", ""));
        double lcd_height = Double.parseDouble(data.getString("lcd_height_text", ""));
        double a = width / lcd_width;
        double b = height / lcd_height;
        list.add("Width*Height:(" + width + "*" + height + ")\n");
        list.add("minTop:" + df.format(minTop / b) + "(mm)/" + minTop + "(pix)\n");
        list.add("minLeft:" + df.format(minLeft / a) + "(mm)/" + minLeft + "(pix)\n");
        list.add("minBottom:" + df.format((height - minBottom) / b) + "(mm)/" + (height - minBottom) + "(pix)\n");
        list.add("minRight:" + df.format((width - minRight) / a) + "(mm)/" + (width - minRight) + "(pix)\n");
        list.add("bottom:" + df.format(bottom / b) + "(mm)/" + bottom + "(pix)\n");
        list.add("right:" + df.format(right / a) + "(mm)/" + right + "(pix)\n");
        if (!list.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Save this test data?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < list.size(); i++) {
                                points = points + list.get(i) + "\n";
                            }
                            rWlog.write(points, "meg_", time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("meg", time);
                            editor.commit();
                            Toast.makeText(ManualEdgeActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            ManualEdgeActivity.this.finish();
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ManualEdgeActivity.this.finish();
                        }
                    }).show();
        } else ManualEdgeActivity.this.finish();
    }
}