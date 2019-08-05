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

public class SensitivityActivity extends Activity {
    RWlog rWlog = new RWlog();
    Sensitivity sen;
    int width;
    int height;
    int count = 0;
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
        sen = new Sensitivity(this);
        setContentView(sen);
    }

    class Sensitivity extends View {
        private int mov_x;
        private int mov_y;
        private Bitmap bitmap;
        private Canvas canvas;

        Paint paint = new Paint();
        int red;
        int green;
        int blue;
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        String bgc = data.getString("bg_color_text","");
        double test_bar_radius = Double.parseDouble(data.getString("sen_gap_text",""));
        double lcd_width = Double.parseDouble(data.getString("lcd_width_text",""));
        double a = width/lcd_width;
        int edge = (int) (test_bar_radius*a);

        public Sensitivity(Context context) {
            super(context);
            paint=new Paint(Paint.DITHER_FLAG);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas=new Canvas();
            canvas.setBitmap(bitmap);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
        }

        @Override
        public void onDraw(Canvas canvas){
            red = Integer.parseInt(bgc.substring(0,3));
            green = Integer.parseInt(bgc.substring(3,6));
            blue = Integer.parseInt(bgc.substring(6));
            canvas.drawColor(Color.rgb(red, green, blue));

            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);

            //draw line
            canvas.drawLine(edge,edge,width-edge,edge,paint);
            canvas.drawLine(width-edge,edge,width-edge,height-edge,paint);
            canvas.drawLine(width-edge,height-edge,edge,height-edge,paint);
            canvas.drawLine(edge,height-edge,edge,(height-2*edge)/6+edge,paint);
            canvas.drawLine(edge,(height-2*edge)/6+edge,5*(width-2*edge)/6+edge,(height-2*edge)/6+edge,paint);
            canvas.drawLine(5*(width-2*edge)/6+edge,(height-2*edge)/6+edge,5*(width-2*edge)/6+edge,5*(height-2*edge)/6+edge,paint);
            canvas.drawLine(5*(width-2*edge)/6+edge,5*(height-2*edge)/6+edge,(width-2*edge)/6+edge,5*(height-2*edge)/6+edge,paint);
            canvas.drawLine((width-2*edge)/6+edge,5*(height-2*edge)/6+edge,(width-2*edge)/6+edge,2*(height-2*edge)/6+edge,paint);
            canvas.drawLine((width-2*edge)/6+edge,2*(height-2*edge)/6+edge,4*(width-2*edge)/6+edge,2*(height-2*edge)/6+edge,paint);
            canvas.drawLine(4*(width-2*edge)/6+edge,2*(height-2*edge)/6+edge,4*(width-2*edge)/6+edge,4*(height-2*edge)/6+edge,paint);
            canvas.drawLine(4*(width-2*edge)/6+edge,4*(height-2*edge)/6+edge,2*(width-2*edge)/6+edge,4*(height-2*edge)/6+edge,paint);
            canvas.drawLine(2*(width-2*edge)/6+edge,4*(height-2*edge)/6+edge,2*(width-2*edge)/6+edge,3*(height-2*edge)/6+edge,paint);
            canvas.drawLine(2*(width-2*edge)/6+edge,3*(height-2*edge)/6+edge,3*(width-2*edge)/6+edge,3*(height-2*edge)/6+edge,paint);

            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            canvas.drawBitmap(bitmap,0,0,null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            if (event.getAction()== MotionEvent.ACTION_MOVE) {
                canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
                list.add(mov_x+"");
                list.add(mov_y+"");
                invalidate();
            }
            if (event.getAction()== MotionEvent.ACTION_DOWN) {
                count++;
                if (count > 1){
                    list.add("65535");
                    list.add("65535");
                    Toast.makeText(SensitivityActivity.this,"Broken line", Toast.LENGTH_SHORT).show();
                }
                invalidate();
            }
            if (event.getAction()== MotionEvent.ACTION_UP) {
                invalidate();
            }
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
                                if (i == (list.size() - 1)) {
                                    points += list.get(i);
                                } else if (i % 2 == 1) {
                                    points = points + list.get(i) + "\n";
                                } else points = points + list.get(i) + ",";
                            }
//                            write(points);
                            rWlog.write(points,"sen_",time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("sen", time);
                            editor.putString("senCount",count+"");
                            editor.commit();
                            Toast.makeText(SensitivityActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            SensitivityActivity.this.finish();
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SensitivityActivity.this.finish();
                        }
                    }).show();
        }else SensitivityActivity.this.finish();
    }
}