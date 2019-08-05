package com.ln.himaxtouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 902995 on 2018/1/29.
 */

public class himax_config {
    public static String mSaveDir_str = "HimaxAPK";
    public static String mTouchTest_tmp_dir_str = mSaveDir_str + File.separator + "tttmp";
    public static String mTouchTest_out_dir_str = mSaveDir_str + File.separator + "TouchTestOut";
    public String mlist_item_cname_header = "com.ln.himaxtouch.";
    public static String list_item[] = {"Touch Monitor", "Linearity", "Motion Events", "Read Motion Event", "Device Information", "Background Self define Graph", "Background Color self adjust", "Setup",
            "Touch Point", "Upgrde FW", "OSC Hopping", "Self Test", "Re-Sense", "Register R/W", "Multi Register R/W", "Touch Test", "Object test", "P sensor test", "Rawdata Record"};
    public static String mlist_item_cname[] = {"TouchMonitorActivity", "Line", "Touchevent", "Readevent", "Phonestate", "BackgroundDefGraph", "BackgroundColor", "SetupActivity",
            "Touch", "UpgradeFW", "osc_hopping", "SelfTestActivity", "nothing", "RegisterRWActivity", "MultiRegisterRWActivity", "TouchTestActivity", "ObjectiveTest.ObjectiveTestActivity", "PSensorTestActivity", "RawdataRecord.RawdataRecordPage"};

    public static String mNodeDir = "/proc/android_touch";
    public static String mSenseNodePath = "/proc/android_touch/SenseOnOff";

    public int mx_res;
    public int my_res;

    public static String mHXPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HimaxAPK/";


    //Driver node SharedPreference key list
    public static String sDriverFolder = "SETUP_DIR_NODE";
    public static String sDriverDiag = "SETUP_DIAG_NODE";

    //Monitor SharedPreference key list
    public static String sMonitorDiagOptionsKey = "diag_name";
    public static String sMonitorDiagOptionsValue = "diag_value";
    public static String sMonitorTransformOptionsKey = "transform_name";
    public static String sMonitorTransformOptionsValue = "transform_value";


    public himax_config(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mx_res = metrics.widthPixels;
        my_res = metrics.heightPixels;
    }

    public static void mLogPrinter(char loglv, String msg) {
        switch (loglv) {
            case 'e':
                Log.e("HXTP_E", msg);
                break;
            case 'd':
                Log.d("HXTP_D", msg);
                break;
            default:
                Log.i("HXTP_I", msg);
                break;
        }
    }

    public static void mWaitingTime(int million) {
        try {
            Thread.sleep(million);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            himax_config.mLogPrinter('e', e.toString());
        }
    }

    public static void mToast(Context context, String printStr) {
        Toast.makeText(context, printStr, Toast.LENGTH_LONG).show();
    }

    public static int mCountCharStr(char mark, String input) {
        int result = -1;
        int tmp_idx = input.indexOf(mark);
        Log.d("HXTP", "input = " + input);
        if (tmp_idx < 0) {
            himax_config.mLogPrinter('d', "No mark=" + mark);
            return -1;
        }
        String tmp_str = input.substring(tmp_idx, input.length());
        do {
            tmp_idx = tmp_str.indexOf(mark);
            Log.d("HXTP", "tmp_idx = " + Integer.toString(tmp_idx));
            if (tmp_idx > 0) {
                tmp_str = tmp_str.substring(tmp_idx + 1, tmp_str.length());
                result = result < 0 ? 1 : result + 1;
                Log.d("HXTP", "result = " + Integer.toString(result));
            } else
                break;
        } while (tmp_idx > 0);

        return result;
    }

    public static String[] mSeparateStrSpace(String input) {
        int count_space = mCountCharStr(' ', input);
        int tmp_start_addr = 0;
        int tmp_end_addr = 0;
        String tmp_str = input.substring(tmp_start_addr, input.length());
        String result[] = new String[count_space > 0 ? count_space : 1];

        if (count_space < 0) {
            result[0] = input;
            return result;
        }

        for (int i = 0; i < result.length; i++) {
            tmp_end_addr = tmp_str.indexOf(' ');
            result[i] = tmp_str.substring(tmp_start_addr, tmp_end_addr);
            Log.d("HXTP", "i=" + Integer.toString(i) + "," + result[i]);
            tmp_start_addr = tmp_end_addr + 1;
        }

        return result;
    }

    public static String mGetSharedSettting(Context context, String Key, String defaultstr) {
        String result = null;
        SharedPreferences sh_settings;
        sh_settings = context.getSharedPreferences("HIAPK", 0);
        result = sh_settings.getString(Key, defaultstr);
        return result;
    }

    public static int mSetSharedSettting(Context context, String Key, String input_str) {
        int result = 0;
        SharedPreferences sh_settings;
        sh_settings = context.getSharedPreferences("HIAPK", 0);
        SharedPreferences.Editor temp_pe = sh_settings.edit();
        temp_pe.putString(Key, input_str);
        temp_pe.commit();

        return result;
    }

    public static int mStoreData(String input_data, String name) {
        int result = 0;
        FileOutputStream out;

        String blank = "\n";
        String path = mHXPath + name + ".txt";

        File file = new File(path);
        try {
            out = new FileOutputStream(file, true);
            out.write(blank.getBytes());
            out.write(input_data.getBytes());
            out.write(blank.getBytes());

            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("HXTPE", "Fail to save");
        }
        return result;
    }

    //    public int mSetRes(Context context)
//    {
//        int result = 0;
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        context.getClass().getWin.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        return result;
//    }
    /* System loading too heavy to use this function*/
    public static Bitmap mCompressImage(Bitmap image, int KB_size) {
        double compress_percent = 1.0;

        Bitmap result_img = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bais;

        /* get original image */
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        Log.d("HXTP", "baos.toByteArray().length=" + Integer.toString(baos.toByteArray().length));
        if (baos.toByteArray().length / 1024 > KB_size) {
            compress_percent = (((double) KB_size / (double) (baos.toByteArray().length / 1024)) * 100);
            Log.d("HXTP", "compress_percent=" + Double.toString(compress_percent));
        }


        image.compress(Bitmap.CompressFormat.JPEG, (int) compress_percent, baos);


        bais = new ByteArrayInputStream(baos.toByteArray());

        result_img = BitmapFactory.decodeStream(bais);
        return result_img;
    }

    public static Bitmap mScaleImage(Bitmap image, int dts_x, int dts_y) {
        Bitmap result_img = null;
        result_img = Bitmap.createScaledBitmap(image, dts_x, dts_y, true);
        return result_img;
    }
}
