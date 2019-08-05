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

public class HorizontalActivity extends Activity {
    Horizontal hor;
    RWlog rWlog = new RWlog();
    int width;
    int height;
    boolean checked;
    String time;
    String points = "";
    List<String> list = new ArrayList<String>();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
        time = df.format(new Date());
        //get display info
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
        hor = new Horizontal(this);
        setContentView(hor);
    }

    class Horizontal extends View {

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
        double lcd_height = Double.parseDouble(data.getString("lcd_height_text", ""));
        double a = width / lcd_width;
        double b = height / lcd_height;

        double lineation_center_threshold = Double.parseDouble(data.getString("lineation_center_threshold_text", ""));
        double lineation_edge_threshold = Double.parseDouble(data.getString("lineation_edge_threshold_text", ""));
        double radius = Double.parseDouble(data.getString("test_bar_radius_text", ""));
        int center = (int) (lineation_center_threshold * b);

        int firstLineY = (int) (radius * b);
        int edgeX = (int) (radius * a);
        int d = (int) ((height - 2 * firstLineY) / 4);
        int edgeY = (int) (lineation_edge_threshold * b);

        public Horizontal(Context context) {
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
            //first line
            canvas.drawLine(0, firstLineY, width, firstLineY, paint);
            canvas.drawLine(0, firstLineY + edgeY, width, firstLineY + edgeY, paint);
            canvas.drawLine(0, firstLineY - edgeY, width, firstLineY - edgeY, paint);
            //second line
            canvas.drawLine(0, firstLineY + d, width, firstLineY + d, paint);
            canvas.drawLine(0, firstLineY + d - edgeY, edgeX, firstLineY + d - edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + d - center, width - edgeX, firstLineY + d - center, paint);
            canvas.drawLine(width - edgeX, firstLineY + d - edgeY, width, firstLineY + d - edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + d - edgeY, width - edgeX, firstLineY + d - center, paint);
            canvas.drawLine(edgeX, firstLineY + d - center, edgeX, firstLineY + d - edgeY, paint);
            canvas.drawLine(0, firstLineY + d + edgeY, edgeX, firstLineY + d + edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + d + center, width - edgeX, firstLineY + d + center, paint);
            canvas.drawLine(width - edgeX, firstLineY + d + edgeY, width, firstLineY + d + edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + d + edgeY, width - edgeX, firstLineY + d + center, paint);
            canvas.drawLine(edgeX, firstLineY + d + center, edgeX, firstLineY + d + edgeY, paint);
            //third line
            canvas.drawLine(0, firstLineY + 2 * d, width, firstLineY + 2 * d, paint);
            canvas.drawLine(0, firstLineY + 2 * d - edgeY, edgeX, firstLineY + 2 * d - edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + 2 * d - center, width - edgeX, firstLineY + 2 * d - center, paint);
            canvas.drawLine(width - edgeX, firstLineY + 2 * d - edgeY, width, firstLineY + 2 * d - edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + 2 * d - edgeY, width - edgeX, firstLineY + 2 * d - center, paint);
            canvas.drawLine(edgeX, firstLineY + 2 * d - center, edgeX, firstLineY + 2 * d - edgeY, paint);
            canvas.drawLine(0, firstLineY + 2 * d + edgeY, edgeX, firstLineY + 2 * d + edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + 2 * d + center, width - edgeX, firstLineY + 2 * d + center, paint);
            canvas.drawLine(width - edgeX, firstLineY + 2 * d + edgeY, width, firstLineY + 2 * d + edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + 2 * d + edgeY, width - edgeX, firstLineY + 2 * d + center, paint);
            canvas.drawLine(edgeX, firstLineY + 2 * d + center, edgeX, firstLineY + 2 * d + edgeY, paint);
            //fourth line
            canvas.drawLine(0, firstLineY + 3 * d, width, firstLineY + 3 * d, paint);
            canvas.drawLine(0, firstLineY + 3 * d - edgeY, edgeX, firstLineY + 3 * d - edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + 3 * d - center, width - edgeX, firstLineY + 3 * d - center, paint);
            canvas.drawLine(width - edgeX, firstLineY + 3 * d - edgeY, width, firstLineY + 3 * d - edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + 3 * d - edgeY, width - edgeX, firstLineY + 3 * d - center, paint);
            canvas.drawLine(edgeX, firstLineY + 3 * d - center, edgeX, firstLineY + 3 * d - edgeY, paint);
            canvas.drawLine(0, firstLineY + 3 * d + edgeY, edgeX, firstLineY + 3 * d + edgeY, paint);
            canvas.drawLine(edgeX, firstLineY + 3 * d + center, width - edgeX, firstLineY + 3 * d + center, paint);
            canvas.drawLine(width - edgeX, firstLineY + 3 * d + edgeY, width, firstLineY + 3 * d + edgeY, paint);
            canvas.drawLine(width - edgeX, firstLineY + 3 * d + edgeY, width - edgeX, firstLineY + 3 * d + center, paint);
            canvas.drawLine(edgeX, firstLineY + 3 * d + center, edgeX, firstLineY + 3 * d + edgeY, paint);
            //fifth line
            canvas.drawLine(0, height - firstLineY, width, height - firstLineY, paint);
            canvas.drawLine(0, height - firstLineY + edgeY, width, height - firstLineY + edgeY, paint);
            canvas.drawLine(0, height - firstLineY - edgeY, width, height - firstLineY - edgeY, paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
                list.add(mov_x + "");
                list.add(mov_y + "");
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (count > 0) {
                    list.add("65535");
                    list.add("65535");
                }
                count++;
                if (count > 5) {
                    Toast.makeText(HorizontalActivity.this, "should be only 5 lines", Toast.LENGTH_SHORT).show();
                }
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
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
                            rWlog.write(points, "hor_", time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("hor", time);
                            editor.putString("horCount", count + "");
                            editor.commit();
                            Toast.makeText(HorizontalActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            HorizontalActivity.this.finish();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HorizontalActivity.this.finish();
                        }
                    }).show();
        } else HorizontalActivity.this.finish();
    }
}