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

public class VerticalActivity extends Activity {
    RWlog rWlog = new RWlog();
    Vertical ver;
    int width;
    int height;
    boolean checked;
    String time;
    String points = "";
    List<String> list = new ArrayList<String>();
    int verCount = 0;

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

        ver = new Vertical(this);
        setContentView(ver);
    }

    class Vertical extends View {
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

        double lcd_width = Double.parseDouble(data.getString("lcd_width_text",""));
        double lcd_height = Double.parseDouble(data.getString("lcd_height_text",""));
        double a = width/lcd_width;
        double b = height/lcd_height;
        double lineation_center_threshold = Double.parseDouble(data.getString("lineation_center_threshold_text",""));
        double lineation_edge_threshold = Double.parseDouble(data.getString("lineation_edge_threshold_text",""));
        double radius = Double.parseDouble(data.getString("test_bar_radius_text",""));
        int center = (int) (lineation_center_threshold*a);
        int firstLineX = (int)(radius*a);
        int edgeX = (int)(radius*a);
        int d = (int)((width - 2*firstLineX)/4);
        int edgeY = (int)(lineation_edge_threshold * a);

        public Vertical(Context context) {
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

            canvas.drawLine(firstLineX,0,firstLineX,height,paint);
            canvas.drawLine(firstLineX+edgeY,0,firstLineX+edgeY,height,paint);
            canvas.drawLine(firstLineX-edgeY,0,firstLineX-edgeY,height,paint);

            canvas.drawLine(firstLineX+d,0,firstLineX+d,height,paint);
            canvas.drawLine(firstLineX+d-edgeY,0,firstLineX+d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+d-center,edgeX,firstLineX+d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+d-edgeY,height-edgeX,firstLineX+d-edgeY,height,paint);
            canvas.drawLine(firstLineX+d-edgeY,height-edgeX,firstLineX+d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+d-center,edgeX,firstLineX+d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+d+edgeY,0,firstLineX+d+edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+d+center,edgeX,firstLineX+d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+d+edgeY,height-edgeX,firstLineX+d+edgeY,height,paint);
            canvas.drawLine(firstLineX+d+edgeY,height-edgeX,firstLineX+d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+d+center,edgeX,firstLineX+d+edgeY,edgeX,paint);

            canvas.drawLine(firstLineX+2*d,0,firstLineX+2*d,height,paint);
            canvas.drawLine(firstLineX+2*d-edgeY,0,firstLineX+2*d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+2*d-center,edgeX,firstLineX+2*d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+2*d-edgeY,height-edgeX,firstLineX+2*d-edgeY,height,paint);
            canvas.drawLine(firstLineX+2*d-edgeY,height-edgeX,firstLineX+2*d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+2*d-center,edgeX,firstLineX+2*d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+2*d+edgeY,0,firstLineX+2*d+edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+2*d+center,edgeX,firstLineX+2*d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+2*d+edgeY,height-edgeX,firstLineX+2*d+edgeY,height,paint);
            canvas.drawLine(firstLineX+2*d+edgeY,height-edgeX,firstLineX+2*d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+2*d+center,edgeX,firstLineX+2*d+edgeY,edgeX,paint);

            canvas.drawLine(firstLineX+3*d,0,firstLineX+3*d,height,paint);
            canvas.drawLine(firstLineX+3*d-edgeY,0,firstLineX+3*d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+3*d-center,edgeX,firstLineX+3*d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+3*d-edgeY,height-edgeX,firstLineX+3*d-edgeY,height,paint);
            canvas.drawLine(firstLineX+3*d-edgeY,height-edgeX,firstLineX+3*d-center,height-edgeX,paint);
            canvas.drawLine(firstLineX+3*d-center,edgeX,firstLineX+3*d-edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+3*d+edgeY,0,firstLineX+3*d+edgeY,edgeX,paint);
            canvas.drawLine(firstLineX+3*d+center,edgeX,firstLineX+3*d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+3*d+edgeY,height-edgeX,firstLineX+3*d+edgeY,height,paint);
            canvas.drawLine(firstLineX+3*d+edgeY,height-edgeX,firstLineX+3*d+center,height-edgeX,paint);
            canvas.drawLine(firstLineX+3*d+center,edgeX,firstLineX+3*d+edgeY,edgeX,paint);

            canvas.drawLine(width - firstLineX,0,width - firstLineX,height,paint);
            canvas.drawLine(width - firstLineX + edgeY,0,width - firstLineX + edgeY,height,paint);
            canvas.drawLine(width - firstLineX - edgeY,0,width - firstLineX - edgeY,height,paint);

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
                if (verCount > 0){
                    list.add("65535");
                    list.add("65535");
                }
                verCount++;
                if (verCount > 5){
                    Toast.makeText(VerticalActivity.this,"should be only 5 lines", Toast.LENGTH_SHORT).show();
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
                            rWlog.write(points,"ver_",time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("ver", time);
                            editor.putString("verCount",verCount+"");
                            editor.commit();
                            Toast.makeText(VerticalActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            VerticalActivity.this.finish();
                        }
                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VerticalActivity.this.finish();
                        }
                    }).show();
        }else VerticalActivity.this.finish();
    }
}