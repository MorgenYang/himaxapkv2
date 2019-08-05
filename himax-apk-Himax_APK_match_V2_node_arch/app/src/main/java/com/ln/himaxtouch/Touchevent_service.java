package com.ln.himaxtouch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Einim on 2016/10/5.
 */
public class Touchevent_service extends Service implements View.OnTouchListener {

    int count = 0;
    String blank = "\n";
    String[] record = new String[5000];
    int check2 = 0;
    int[] check = new int[10];
    int[] x = new int[10];
    int[] y = new int[10];
    Long tsLong;
    String timeformat;
    View view;
    private WindowManager mWindowManager;
    // linear layout will use to detect touch event
    private LinearLayout touchLayout;

    NotificationCompat.Builder builder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Himax", "onStartCommand() executed");


        /*view = new View(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Toast.makeText(this,"Enter Service",Toast.LENGTH_SHORT).show();
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int index;
                        int id;

                        if (mTouchEventRecordCHK == 0) {
                            switch (event.getActionMasked()) {
                                case MotionEvent.ACTION_UP:

                                    int savecheck = 0;
                                    File dir;
                                    File file;
                                    index = event.getActionIndex();
                                    id = event.getPointerId(index);
                                    tsLong = SystemClock.elapsedRealtime();
                                    x[id] = (int) event.getX(event.findPointerIndex(id));
                                    y[id] = (int) event.getY(event.findPointerIndex(id));
                                    check[id] = 0;
                                    timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                                    //record[count]="Time: "+df.format(tsLong)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
                                    //record[count]="Time: "+(tsLong/1000)+"."+(tsLong%1000)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
                                    record[count] = "Time: " + timeformat + "  Point:" + id + " Up (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                                    if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                                        dir = getApplicationContext().getExternalFilesDir(null);
                                    } else {
                                        dir = getApplicationContext().getFilesDir();
                                    }
                                    file = new File(dir, "Move_event_log.txt");
                                    FileOutputStream out;
                                    for (int a = 0; a <= count; a++) {
                                        //text.append(record[a]);
                                        try {
                                            out = new FileOutputStream(file, true);
                                            out.write(record[a].getBytes());
                                            if (a == count) {
                                                out.write(blank.getBytes());
                                                out.write(blank.getBytes());
                                            }
                                            out.flush();
                                            out.close();
                                            savecheck = 0;
                                        } catch (Exception e) {
                                            savecheck = 1;
                                        }
                                    }
                                    if (savecheck == 0) {
                                        Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();
                                    } else if (savecheck == 1) {
                                        Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
                                    }
                                    mTouchEventRecordCHK = 1;
                                    count = 0;
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    index = event.getActionIndex();
                                    id = event.getPointerId(index);
                                    tsLong = SystemClock.elapsedRealtime();
                                    timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                                    for (int a = 0; a < 10; a++) {
                                        if (check[a] == 1) {
                                            x[a] = (int) event.getX(event.findPointerIndex(a));
                                            y[a] = (int) event.getY(event.findPointerIndex(a));
                                            record[count] = "Time: " + timeformat + "  Point:" + a + " Move (x,y)=(" + x[a] + "," + y[a] + ")" + "\n";
                                            count = count + 1;
                                        }
                                    }
                                    break;
                                case MotionEvent.ACTION_DOWN:
                                    index = event.getActionIndex();
                                    id = event.getPointerId(index);
                                    tsLong = SystemClock.elapsedRealtime();
                                    timeformat = String.format("%d.%3d", tsLong / 1000, tsLong % 1000);
                                    x[id] = (int) event.getX(event.findPointerIndex(id));
                                    y[id] = (int) event.getY(event.findPointerIndex(id));
                                    check[id] = 1;
                                    record[count] = "Time: " + timeformat + "  Point:" + id + " Down (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                                    count = count + 1;
                                    break;

                                case MotionEvent.ACTION_POINTER_DOWN:
                                    index = event.getActionIndex();
                                    id = event.getPointerId(index);
                                    tsLong = SystemClock.elapsedRealtime();
                                    timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                                    x[id] = (int) event.getX(event.findPointerIndex(id));
                                    y[id] = (int) event.getY(event.findPointerIndex(id));
                                    check[id] = 1;
                                    record[count] = "Time: " + timeformat + "  Point:" + id + " PointerDown (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                                    count = count + 1;
                                    break;
                                case MotionEvent.ACTION_POINTER_UP:
                                    index = event.getActionIndex();
                                    id = event.getPointerId(index);
                                    tsLong = SystemClock.elapsedRealtime();
                                    timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                                    x[id] = (int) event.getX(event.findPointerIndex(id));
                                    y[id] = (int) event.getY(event.findPointerIndex(id));
                                    check[id] = 0;
                                    record[count] = "Time: " + timeformat + "  Point:" + id + " PointerUp (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                                    count = count + 1;
                                    break;
                            }
                        }
                        return false;
                    }
                });

            }
        });*/


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.d("Himax", "onCreate() executed");
        ;


        touchLayout = new LinearLayout(getBaseContext());
        // set layout width 30 px and height is equal to full screen
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        // set color if you want layout visible on screen
        touchLayout.setBackgroundColor(Color.GRAY);
        touchLayout.getBackground().setAlpha(210);
        // set on touch listened


        // fetch window manager object
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                300, // width of layout 30 px
                WindowManager.LayoutParams.MATCH_PARENT, // height is equal to full screen
                WindowManager.LayoutParams.TYPE_PHONE, // Type Phone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.RIGHT | Gravity.TOP;
        Log.i("Himax", "add View");

        mWindowManager.addView(touchLayout, mParams);

        touchLayout.setOnTouchListener(this);


        Intent intent2 = new Intent(this, Touchevent_service.class);
        //intent2.setAction();
        NotificationManager nm = (NotificationManager)
                getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder =
                new NotificationCompat.Builder(getBaseContext());

        PendingIntent test2;
        test2 = PendingIntent.getService(getBaseContext(), 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Himax APK")
                .setContentText("Now is getting motion events")
                .addAction(R.drawable.ic_launcher, "Stop", test2)
                .setOngoing(true)
        ;
        nm.notify(2, builder.build());

        super.onCreate();


    }

    @Override
    public IBinder onBind(Intent i) {

        return null;
    }

    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (touchLayout != null) mWindowManager.removeView(touchLayout);
        }
        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        super.onDestroy();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index;
        int id;

        Log.d("himax", "Enter service on Touch");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:

                int savecheck = 0;
                File dir;
                File file;
                index = event.getActionIndex();
                id = event.getPointerId(index);
                tsLong = SystemClock.elapsedRealtime();
                x[id] = (int) event.getX(event.findPointerIndex(id));
                y[id] = (int) event.getY(event.findPointerIndex(id));
                check[id] = 0;
                timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                //record[count]="Time: "+df.format(tsLong)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
                //record[count]="Time: "+(tsLong/1000)+"."+(tsLong%1000)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
                record[count] = "Time: " + timeformat + "  Point:" + id + " Up (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                    dir = getApplicationContext().getExternalFilesDir(null);
                } else {
                    dir = getApplicationContext().getFilesDir();
                }
                file = new File(dir, "Move_event_log.txt");
                FileOutputStream out;
                for (int a = 0; a <= count; a++) {
                    //text.append(record[a]);
                    try {
                        out = new FileOutputStream(file, true);
                        out.write(record[a].getBytes());
                        if (a == count) {
                            out.write(blank.getBytes());
                            out.write(blank.getBytes());
                        }
                        out.flush();
                        out.close();
                        savecheck = 0;
                    } catch (Exception e) {
                        savecheck = 1;
                    }
                }
                if (savecheck == 0) {
                    Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();
                } else if (savecheck == 1) {
                    Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
                }
                check2 = 1;
                count = 0;
                Log.d("Himx", "up");
                break;
            case MotionEvent.ACTION_MOVE:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                tsLong = SystemClock.elapsedRealtime();
                timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                for (int a = 0; a < 10; a++) {
                    if (check[a] == 1) {
                        x[a] = (int) event.getX(event.findPointerIndex(a));
                        y[a] = (int) event.getY(event.findPointerIndex(a));
                        record[count] = "Time: " + timeformat + "  Point:" + a + " Move (x,y)=(" + x[a] + "," + y[a] + ")" + "\n";
                        count = count + 1;
                    }
                }
                Log.d("Himx", "move");
                break;
            case MotionEvent.ACTION_DOWN:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                tsLong = SystemClock.elapsedRealtime();
                timeformat = String.format("%d.%3d", tsLong / 1000, tsLong % 1000);
                x[id] = (int) event.getX(event.findPointerIndex(id));
                y[id] = (int) event.getY(event.findPointerIndex(id));
                check[id] = 1;
                record[count] = "Time: " + timeformat + "  Point:" + id + " Down (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                count = count + 1;
                Log.d("Himx", "down");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                tsLong = SystemClock.elapsedRealtime();
                timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                x[id] = (int) event.getX(event.findPointerIndex(id));
                y[id] = (int) event.getY(event.findPointerIndex(id));
                check[id] = 1;
                record[count] = "Time: " + timeformat + "  Point:" + id + " PointerDown (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                count = count + 1;
                Log.d("Himx", "point down");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                index = event.getActionIndex();
                id = event.getPointerId(index);
                tsLong = SystemClock.elapsedRealtime();
                timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
                x[id] = (int) event.getX(event.findPointerIndex(id));
                y[id] = (int) event.getY(event.findPointerIndex(id));
                check[id] = 0;
                record[count] = "Time: " + timeformat + "  Point:" + id + " PointerUp (x,y)=(" + x[id] + "," + y[id] + ")" + "\n";
                count = count + 1;
                Log.d("Himx", "point up");
                break;
        }


        return true;

    }

}
