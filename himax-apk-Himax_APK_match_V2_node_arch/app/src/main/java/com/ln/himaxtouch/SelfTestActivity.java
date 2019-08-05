package com.ln.himaxtouch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SelfTestActivity extends Activity implements View.OnClickListener {
    public String proc_dir_node;
    public String proc_selftest_node;
    public String proc_diag_node;
    public String proc_register_node;
    SharedPreferences sh_settings;

    public static TextView SelfTestCondition;
    public Button SelfTestStartBtn;
    private static himax_config mHXC;

    static {
        System.loadLibrary("HimaxAPK");
    }
    //

    //Native function
    public static native String writeCfg(String[] stringArray);

    public static native String readCfg(String[] stringArray);

    private static Activity this_act = null;
    private static String output_file_name = "";

    private static int mself_test_result = 0;

    private final static int mWorkProcessing = 0;
    private final static int mWorkProcessEnd = 1;
    private final static int mWorkProcessFault = 2;

    private static class WorkingHadler extends Handler {
        WorkingHadler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case mWorkProcessing:
                    run_selftest();
                    break;
                case mWorkProcessFault:
                    Log.e("HXTP", "Processing Fault!\n");
                    break;
                case mWorkProcessEnd:
                    mReorcd("aaa");
                    break;
                default:
                    Log.e("HXTP", "Nothing to be done!\n");
                    break;
            }
        }
    }

    private static WorkingHadler mWorkingHandler;
    private static HandlerThread mWorkingThread;

    private final static int mMainProcessing = 0;
    private final static int mMainProcessEndSucc = 1;
    private final static int mMainProcessEndFail = 2;
    private final static int mMainProcessFinish = 3;
    private final static int mMainProcessFault = -1;

    private static Handler mMain = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case mMainProcessing:
                    SelfTestCondition.setText("Testing");
                    SelfTestCondition.setTextColor(Color.BLUE);
                    break;
                case mMainProcessEndSucc:
                    SelfTestCondition.setTextColor(Color.GREEN);
                    SelfTestCondition.setText("Pass");
                    break;
                case mMainProcessEndFail:
                    SelfTestCondition.setTextColor(Color.RED);
                    SelfTestCondition.setText("Fail");
                    break;
                case mMainProcessFault:
                    SelfTestCondition.setTextColor(Color.GRAY);
                    SelfTestCondition.setText("Something Wrong!");
                    break;
                case mMainProcessFinish:
                    Log.d("HXTP", "End of Activity");
                    this_act.finish();
                    break;
                default:
                    Log.e("HXTP", "Nothing to be done!\n");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_test);

        mHXC = new himax_config(this);
        this_act = this;

        SelfTestCondition = (TextView) findViewById(R.id.SelfTestResult);
        SelfTestCondition.setTextColor(Color.BLUE);

        SelfTestStartBtn = (Button) findViewById(R.id.SelfTestStartBtn);
        SelfTestStartBtn.setOnClickListener(this);

        mWorkingThread = new HandlerThread("RunningSelfTest");
        mWorkingThread.start();

        mWorkingHandler = new WorkingHadler(mWorkingThread.getLooper());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.self_test, menu);
        return true;
    }

    private static void run_selftest() {
        int result = -1;
        Message msg = Message.obtain();
        Message main_msg = Message.obtain();
        msg.what = mMainProcessing;
        mMain.sendMessage(msg);
        result = read_Self_test();
        if (result == 1) {
            main_msg.what = mMainProcessEndFail;
            mMain.sendMessage(main_msg);
        } else if (result == 0) {
            main_msg.what = mMainProcessEndSucc;
            mMain.sendMessage(main_msg);
        } else {
            Log.e("HXTP", "Fail End! End Test!");
            main_msg.what = mMainProcessFinish;
            mMain.sendMessage(main_msg);

        }

    }

    @Override
    public void onClick(View v) {
        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_selftest_node = proc_dir_node + sh_settings.getString("SETUP_SELFTEST_NODE", "self_test");
        proc_diag_node = proc_dir_node + sh_settings.getString("SETUP_DIAG_NODE", "/diag");
        proc_register_node = proc_dir_node + sh_settings.getString("SETUP_REGISTER_NODE", "register");


        if (v == SelfTestStartBtn) {
            Message msg = Message.obtain();
            msg.what = mWorkProcessing;
            mWorkingHandler.sendMessage(msg);
        }


    }

    private static void mReorcd(String input) {
        Date curDate = new Date(System.currentTimeMillis());
        File vSDCard = null;
        File vPath = null;
        FileWriter vFile = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        vPath = new File(mHXC.mHXPath);
        if (!vPath.exists()) {
            vPath.mkdir();
        }

        if (!vPath.exists()) {
            Toast.makeText(this_act, "Error : Can't create " + mHXC.mHXPath, Toast.LENGTH_LONG).show();
            return;
        }


        output_file_name += formatter.format(curDate);
        output_file_name += ".txt";
        try {
            vFile = new FileWriter(mHXC.mHXPath + output_file_name);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Toast.makeText(this_act, "Error : Can't create Log File", Toast.LENGTH_LONG).show();
            return;
        }


        try {
            vFile.write("===================================================================== \n");
            if (mself_test_result == 0) {
                vFile.write("Himax Self Test Result : PASS \n");
            } else {
                vFile.write("Himax Self Test Result : NG \n");
            }
            vFile.write("===================================================================== \n");
            vFile.write("Raw Data : \n");
            vFile.write(input);
            vFile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            Toast.makeText(this_act, "Error : Log File Write Fail", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this_act, "Self Test Complete.", Toast.LENGTH_LONG).show();
    }


    private static int read_Self_test() {
        String result = "";


        Log.d("HXTP", "Entering read_Self_test");
        String[] string_slf_test_cmd_read = {"/proc/android_touch/self_test"};
        result = readCfg(string_slf_test_cmd_read);

        Log.d("HXTP", "readSelfTest con:" + result);

        if (result.matches("[\\w\\W\\s\\S.]*Fail[\\w\\W\\s\\S.]*")) {
            Log.d("HXTP", "Fail");
            mself_test_result = 1;
            return 1;
        } else if (result.matches("[\\w\\W\\s\\S.]*Pass[\\w\\W\\s\\S.]*")) {
            Log.d("HXTP", "Pass");
            mself_test_result = 0;
            return 0;
        } else {
            return read_Self_test();
        }
    }

}
