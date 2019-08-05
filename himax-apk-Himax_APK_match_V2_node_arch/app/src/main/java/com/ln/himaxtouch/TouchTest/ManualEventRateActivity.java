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

public class ManualEventRateActivity extends Activity {
    RWlog rWlog = new RWlog();
    Manual sen;
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

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics rmetrics = new DisplayMetrics();
        display.getRealMetrics(rmetrics);
        SharedPreferences getData = getSharedPreferences("data",MODE_PRIVATE);
        checked = getData.getBoolean("vir_key",true);
        if (!checked){
            width = rmetrics.widthPixels;
            height = rmetrics.heightPixels;
        }else {
            width = display.getWidth();
            height = display.getHeight();
        }
        sen = new Manual(this);
        setContentView(sen);
    }

    class Manual extends View {
        private int mov_x;
        private int mov_y;
        private Bitmap bitmap;
        private Canvas canvas;

        long current;
        long pre;

        Paint paint = new Paint();
        int red;
        int green;
        int blue;
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        String bgc = data.getString("bg_color_text","");

        double lcd_width = Double.parseDouble(data.getString("lcd_width_text",""));
        double lcd_height = Double.parseDouble(data.getString("lcd_height_text",""));
        double a = width/lcd_width;
        double b = height/lcd_height;

        public Manual(Context context) {
            super(context);
            paint=new Paint(Paint.DITHER_FLAG);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas=new Canvas();
            canvas.setBitmap(bitmap);
        }

        @Override
        public void onDraw(Canvas canvas){
            red = Integer.parseInt(bgc.substring(0,3));
            green = Integer.parseInt(bgc.substring(3,6));
            blue = Integer.parseInt(bgc.substring(6));
            canvas.drawColor(Color.rgb(red, green, blue));

            paint.setColor(Color.RED);
            paint.setStrokeWidth(1);
            paint.setTextSize(30);

            canvas.drawText("Point:"+"("+mov_x+","+mov_y+")",50,100,paint);
            canvas.drawText("Event Rate:"+"("+(current - pre)+","+"p/s)",300,100,paint);
            canvas.drawText("Min Event Rate:"+"("+"p/s)",300,135,paint);
            canvas.drawText("Max Event Rate:"+"("+"p/s)",300,170,paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            canvas.drawBitmap(bitmap,0,0,null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            if (event.getAction()== MotionEvent.ACTION_MOVE) {
                pre = event.getEventTime();
                list.add(pre+"");
                invalidate();
            }
            if (event.getAction()== MotionEvent.ACTION_DOWN) {
                invalidate();
            }
            if (event.getAction()== MotionEvent.ACTION_UP) {
                invalidate();
            }
            current = event.getEventTime();
            mov_x=(int) event.getX();
            mov_y=(int) event.getY();
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
                                if (i == list.size()-1){
                                    points = points + list.get(i);
                                }else
                                    points = points + list.get(i) +"\n";
                            }
//                            write(points);
                            rWlog.write(points,"rat_",time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("rate", time);
                            editor.commit();
                            Toast.makeText(ManualEventRateActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            ManualEventRateActivity.this.finish();
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ManualEventRateActivity.this.finish();
                        }
                    }).show();
        }else ManualEventRateActivity.this.finish();
    }
}