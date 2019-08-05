package com.ln.himaxtouch.TouchTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class ManualTwoPointActivity extends Activity {
    RWlog rWlog = new RWlog();
    Manual sen;
    int[] check = new int[10];
    int[] x = new int[10];
    int[] y = new int[10];
    int width;
    int height;
    boolean checked;
    String lcd_width;

    DecimalFormat df = new DecimalFormat("0.00");
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
        lcd_width = getData.getString("lcd_width_text","");
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
        public Manual(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas){

            float t =720;
            float b =width/t;
            float c = Float.parseFloat(lcd_width)/width;

            Paint paint2 = new Paint();
            paint2.setColor(Color.RED);
            paint2.setStrokeWidth(3);
            paint2.setTextSize(40*b);

            Paint[] paint = new Paint[10];
            float textsize=30*b;
            for(int a=0;a<10;a++){
                paint[a] = new Paint();
                paint[a].setTextSize(textsize);
                paint[a].setAlpha(220);
            }
            paint[0].setColor(Color.YELLOW);
            paint[1].setColor(Color.BLUE);
            paint[2].setColor(Color.RED);
            paint[3].setColor(Color.WHITE);
            paint[4].setColor(Color.GREEN);
            paint[5].setColor(Color.BLACK);
            paint[6].setColor(Color.MAGENTA);
            paint[7].setColor(Color.CYAN);
            paint[8].setColor(Color.LTGRAY);
            paint[9].setColor(Color.DKGRAY);

            float r =50*b;
            float xa=-(r-(100*b));
            float xa2 =-(r+(100*b));
            float ya=textsize+r;
            float ya2=-(textsize+r-(30*b));
            double distance = 0;

            for(int a=0;a<2;a++){
                if(check[a]==1){
                    if(x[a]<150){
                        xa2=xa;
                    }
                    else if(x[a]>(width-150)){
                    }
                    else{
                        xa2=(-r);
                    }
                    if(y[a]>(height-300)){
                        ya=ya2;
                    }
                    canvas.drawText("("+x[a]+","+y[a]+")", (float)x[a]+xa2, (float)y[a]+ya, paint[a]);
                    canvas.drawCircle(x[a], y[a],r, paint[a]);
                    if (a ==1) {
                        distance = Math.sqrt((x[a]-x[a-1])*(x[a]-x[a-1])+(y[a]-y[a-1])*(y[a]-y[a-1]))*b;
                        canvas.drawText("distance:" + df.format(distance) +"(pixel)/" +df.format(distance*c) + "(mm)", 100, 100, paint2);
                        list.add(df.format(distance));
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            int index;
            int id;
            switch(event.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    index =event.getActionIndex();
                    id = event.getPointerId(index);
                    check[id] = 0;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    for(int a=0;a<event.getPointerCount();a++){
                        index = event.getActionIndex();
                        id = event.getPointerId(index);
                        check[id] = 0;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    for(int a=0;a<event.getPointerCount();a++){
                        if(check[a]==1){
                            x[a] = (int)event.getX(event.findPointerIndex(a));
                            y[a] = (int)event.getY(event.findPointerIndex(a));
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    for(int a=0;a<event.getPointerCount();a++){
                        index = event.getActionIndex();
                        id = event.getPointerId(index);
                        x[id] = (int) event.getX(event.findPointerIndex(id));
                        y[id] = (int) event.getY(event.findPointerIndex(id));
                        check[id] = 1;
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    index =event.getActionIndex();
                    id = event.getPointerId(index);
                    x[id] = (int)event.getX(event.findPointerIndex(id));
                    y[id] = (int)event.getY(event.findPointerIndex(id));
                    check[id] = 1;
                    break;
            }
            sen.invalidate();
            return true;
        }
    }

    @Override
    public void onBackPressed(){
        if (!list.isEmpty()){
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
                            rWlog.write(points,"two_",time);
                            SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
                            editor.putString("mtwo", time);
                            editor.commit();
//                            Toast.makeText(ManualTwoPointActivity.this, points, Toast.LENGTH_SHORT).show();
                            Toast.makeText(ManualTwoPointActivity.this, "data saved!", Toast.LENGTH_SHORT).show();
                            ManualTwoPointActivity.this.finish();
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ManualTwoPointActivity.this.finish();
                        }
                    }).show();
        }else ManualTwoPointActivity.this.finish();
    }
}