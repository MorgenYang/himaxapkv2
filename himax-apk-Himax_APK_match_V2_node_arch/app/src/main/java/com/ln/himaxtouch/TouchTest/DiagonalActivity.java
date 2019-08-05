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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiagonalActivity extends Activity {
    Diagonal dia;
    RWlog rWlog = new RWlog();
    int width;
    int height;
    boolean checked;
    String time;
    String points = "";
    List<String> list = new ArrayList<String>();
    int diaCount = 0;

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
        dia = new Diagonal(this);
        setContentView(dia);
    }

    class Diagonal extends View {

        private int mov_x;
        private int mov_y;
        private Bitmap bitmap;
        private Canvas canvas;
        Paint paint = new Paint();
        int red;
        int green;
        int blue;
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        String bgc = data.getString("bg_color_text", "");

        double lcd_width = Double.parseDouble(data.getString("lcd_width_text", ""));
        double a = width / lcd_width;

        //get acc info
        double lineation_center_threshold = Double.parseDouble(data.getString("lineation_center_threshold_text", ""));
        double cy = Math.sqrt(height * height + width * width) * lineation_center_threshold * a / width;
        double cx = Math.sqrt(height * height + width * width) * lineation_center_threshold * a / height;

        public Diagonal(Context context) {
            super(context);
            paint = new Paint(Paint.DITHER_FLAG);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas();
            canvas.setBitmap(bitmap);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            red = Integer.parseInt(bgc.substring(0, 3));
            green = Integer.parseInt(bgc.substring(3, 6));
            blue = Integer.parseInt(bgc.substring(6));
            canvas.drawColor(Color.rgb(red, green, blue));
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);

            canvas.drawLine(0, 0, width, height, paint);
            canvas.drawLine(0, (int) cy, width - (int) cx, height, paint);//左下

            canvas.drawLine((int) cx, 0, width, height - (int) cy, paint);//右上

            canvas.drawLine(width - (int) cx, 0, 0, height - (int) cy, paint);//右上

            canvas.drawLine(width, (int) cy, (int) cx, height, paint);//右下

            canvas.drawLine(width, 0, 0, height, paint);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                list.add(mov_x + "");
                list.add(mov_y + "");
                canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (diaCount > 0) {
                    list.add("65535");
                    list.add("65535");
                }
                diaCount++;
                if (diaCount > 2) {
                    Toast.makeText(DiagonalActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
                invalidate();
            }
            mov_x = (int) event.getX();
            mov_y = (int) event.getY();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!list.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Save this test data?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < list.size(); i++) {
                                if (i == (list.size() - 1)) {
                                    points += list.get(i);
                                } else if (i % 2 == 1) {
                                    points = points + list.get(i) + "\n";
                                } else points = points + list.get(i) + ",";
                            }
//                            write(points);
                            rWlog.write(points, "dia_", time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("dia", time);
                            editor.putString("diaCount", diaCount + "");
                            editor.commit();
                            Toast.makeText(DiagonalActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            DiagonalActivity.this.finish();
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DiagonalActivity.this.finish();
                        }
                    }).show();
        } else DiagonalActivity.this.finish();
    }
}