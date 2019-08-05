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

import com.ln.himaxtouch.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccuracyActivity extends Activity {
    Accuracy acc;
    RWlog rWlog = new RWlog();
    int width;
    int height;
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
        acc = new Accuracy(this);
        setContentView(acc);
    }

    class Accuracy extends View {
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

        //        int accuracy_click_num = Integer.parseInt(data.getString("accuracy_click_num_text",""));
//        double lcd_height = Double.parseDouble(data.getString("lcd_height_text",""));
//        int accuracy_total_test_times = Integer.parseInt(data.getString("accuracy_total_test_times_text",""));
        double test_bar_radius = Double.parseDouble(data.getString("test_bar_radius_text", ""));
        double accuracy_center_threshold = Double.parseDouble(data.getString("accuracy_center_threshold_text", ""));
        double accuracy_edge_threshold = Double.parseDouble(data.getString("accuracy_edge_threshold_text", ""));
        double a = width / lcd_width;
        //ver x
        int firstVerLine = (int) (a * test_bar_radius);
        int secondVerLine = (int) ((width - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        int thirdVerLine = width / 2;
        int fourthVerLine = (int) (3 * (width - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        int fifthVerLine = (int) (width - a * test_bar_radius);
        //hor y
        int firstHorLine = (int) (a * test_bar_radius);
        int secondHorLine = (int) ((height - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        int thirdHorLine = height / 2;
        int fourthHorLine = (int) (3 * (height - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        int fifthHorLine = (int) (height - a * test_bar_radius);

        public Accuracy(Context context) {
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
            //set bgcolor
            red = Integer.parseInt(bgc.substring(0, 3));
            green = Integer.parseInt(bgc.substring(3, 6));
            blue = Integer.parseInt(bgc.substring(6));
            canvas.drawColor(Color.rgb(red, green, blue));
            //draw line
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            //ver
            canvas.drawLine(firstVerLine, 0, firstVerLine, height, paint);
            canvas.drawLine(secondVerLine, 0, secondVerLine, height, paint);
            canvas.drawLine(thirdVerLine, 0, thirdVerLine, height, paint);
            canvas.drawLine(fourthVerLine, 0, fourthVerLine, height, paint);
            canvas.drawLine(fifthVerLine, 0, fifthVerLine, height, paint);
            //hor
            canvas.drawLine(0, firstHorLine, width, firstHorLine, paint);
            canvas.drawLine(0, secondHorLine, width, secondHorLine, paint);
            canvas.drawLine(0, thirdHorLine, width, thirdHorLine, paint);
            canvas.drawLine(0, fourthHorLine, width, fourthHorLine, paint);
            canvas.drawLine(0, fifthHorLine, width, fifthHorLine, paint);
            //draw out circle
            canvas.drawCircle(firstVerLine, firstHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(firstVerLine, thirdHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(firstVerLine, fifthHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(secondVerLine, secondHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(secondVerLine, fourthHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(thirdVerLine, firstHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(thirdVerLine, thirdHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(thirdVerLine, fifthHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(fourthVerLine, secondHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(fourthVerLine, fourthHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(fifthVerLine, firstHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(fifthVerLine, thirdHorLine, (int) (test_bar_radius * a), paint);
            canvas.drawCircle(fifthVerLine, fifthHorLine, (int) (test_bar_radius * a), paint);
            //draw in circle
            canvas.drawCircle(firstVerLine, firstHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(firstVerLine, thirdHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(firstVerLine, fifthHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(secondVerLine, secondHorLine, (int) (accuracy_center_threshold * a), paint);
            canvas.drawCircle(secondVerLine, fourthHorLine, (int) (accuracy_center_threshold * a), paint);
            canvas.drawCircle(thirdVerLine, firstHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(thirdVerLine, thirdHorLine, (int) (accuracy_center_threshold * a), paint);
            canvas.drawCircle(thirdVerLine, fifthHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(fourthVerLine, secondHorLine, (int) (accuracy_center_threshold * a), paint);
            canvas.drawCircle(fourthVerLine, fourthHorLine, (int) (accuracy_center_threshold * a), paint);
            canvas.drawCircle(fifthVerLine, firstHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(fifthVerLine, thirdHorLine, (int) (accuracy_edge_threshold * a), paint);
            canvas.drawCircle(fifthVerLine, fifthHorLine, (int) (accuracy_edge_threshold * a), paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mov_x = (int) event.getX();
                mov_y = (int) event.getY();
                list.add(mov_x + "");
                list.add(mov_y + "");
                canvas.drawPoint(mov_x, mov_y, paint);
                invalidate();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                canvas.drawPoint(mov_x, mov_y, paint);
                invalidate();
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!list.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Save this test data?")
                    .setIcon(R.drawable.ic_launcher)
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
                            rWlog.write(points, "acc_", time);
//                            write(points);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("acc", time);
                            editor.commit();
                            Toast.makeText(AccuracyActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            AccuracyActivity.this.finish();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AccuracyActivity.this.finish();
                        }
                    }).show();
        } else {
            AccuracyActivity.this.finish();
        }
    }
}