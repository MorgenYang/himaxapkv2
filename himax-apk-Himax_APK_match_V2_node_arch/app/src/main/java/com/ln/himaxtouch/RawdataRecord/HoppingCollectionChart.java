package com.ln.himaxtouch.RawdataRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.NodeDataSource;

import java.util.ArrayList;

import com.ln.himaxtouch.R;

import static com.ln.himaxtouch.HimaxApplication.mICData;

/**
 * Created by Einim on 2017/10/26.
 */

public class HoppingCollectionChart extends Activity implements View.OnClickListener {
    private static final String TAG = "[HXTP]HopCollctChart";
    static TableLayout mTL;
    static EditText mEdit_frames;
    static TextView mText_freqNumber;
    static TextView mText_freq;
    static TextView mText_currentFrame;
    static TextView mText_currentFreq;
    static TextView mText_fileStorage;
    static TextView mText_status;

    static EditText mText_hopping_scale;
    static EditText mText_hopping_base;
    static EditText mText_hopping_last;

    static Button mBtn_start;
    static Button mBtn_back;

    static int mReady2Run = 0;

    private static final int OSC_HZ = 48 * 1000 * 1000;

    private static CSVAccess mCSV;
    private static Context mContext;
    private static int mTotalFreqN = 10;
    private static int base_freq_val = 230;
    private static final int GRAPHIC_ID = 999;
    private static double mhopping_all_freq[];

    private static int mhoppingscale;
    private static int mbasehopping;
    private static int mlasthopping;

    private static int mMAXEdge = -10000;

    static AlertDialog dialog_notouch;

    static NodeDataSource g_node_acc;

    private HandlerThread mWorkThread;
    private WorkHandler mWorkHandler;
    private final static int MSG_STARTPROCESS = 0;

    private static class WorkHandler extends Handler {
        WorkHandler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STARTPROCESS:
                    startProcess(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }

    private final static int MSG_UPDATE_REAL_TIME = 0;
    private final static int MSG_UPDATE_FINISH = 1;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_REAL_TIME:
                    mText_currentFrame.setText((msg.arg1 + 1) + "");
                    mText_currentFreq.setText(((Double) msg.obj) + "");
                    mText_status.setText("In Process");
                    break;
                case MSG_UPDATE_FINISH:
                    mText_fileStorage.setText(mCSV.mFileNameFTwo);
                    mText_status.setText("Finish");
                    drawGraphic(mContext, (int[]) msg.obj);

                    if (dialog_notouch.isShowing())
                        dialog_notouch.dismiss();

                    break;
                default:
                    break;
            }
        }
    };

    private void loadView() {
        mEdit_frames = (EditText) findViewById(R.id.Edit_frames);
        mText_hopping_base = (EditText) findViewById(R.id.Text_basehopping);
        mText_hopping_last = (EditText) findViewById(R.id.Text_lasthopping);
        mText_hopping_scale = (EditText) findViewById(R.id.Text_hoppingscale);
        mText_freqNumber = (TextView) findViewById(R.id.Text_freqNumber);
        mText_freq = (TextView) findViewById(R.id.Text_freq);
        mText_currentFrame = (TextView) findViewById(R.id.Text_currentFrame);
        mText_currentFreq = (TextView) findViewById(R.id.Text_currentFreq);
        mText_fileStorage = (TextView) findViewById(R.id.Text_fileStorage);
        mText_status = (TextView) findViewById(R.id.Text_status);
        mTL = (TableLayout) findViewById(R.id.tl);
        mBtn_start = (Button) findViewById(R.id.Btn_start);
        mBtn_back = (Button) findViewById(R.id.Btn_back);
        mBtn_start.setOnClickListener(this);
        mBtn_back.setOnClickListener(this);

        mEdit_frames.setText("3");
    }

    private static void drawGraphic(Context context, int[] sumDiff) {
        ArrayList<Double> yList = new ArrayList<Double>();
        ArrayList<String> xList = new ArrayList<String>();
        for (int i = 0; i < sumDiff.length; i++) {
            yList.add(Double.valueOf(sumDiff[i]));
            //xList.add(String.valueOf(hoppingAndCal(i)));
            xList.add(String.valueOf(mhopping_all_freq[i]));
        }
        LineGraphicView lgv = new LineGraphicView(context);
        lgv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        lgv.setData(yList, xList, true);
        lgv.setId(GRAPHIC_ID);
        mTL.addView(lgv);
    }

//    private static void sampleGraphic(Context context) {
//        yList = new ArrayList<Double>();
//        yList.add((double) 2.103);
//        yList.add(40.05);
//        yList.add(6.60);
//        yList.add(3.08);
//        yList.add(4.32);
//        yList.add(-22.0);
//        yList.add(5.0);
//
//        ArrayList<String> xRawDatas = new ArrayList<String>();
//        xRawDatas.add("05-19");
//        xRawDatas.add("05-20");
//        xRawDatas.add("05-21");
//        xRawDatas.add("05-22");
//        xRawDatas.add("05-23");
//        xRawDatas.add("05-24");
//        xRawDatas.add("05-25");
//        xRawDatas.add("05-26");
//
//        LineGraphicView lgv = new LineGraphicView(context);
//        lgv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
////        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
////        glp.columnSpec = GridLayout.spec(2);
////        lgv.setLayoutParams(glp);
//
//        lgv.setData(yList, xRawDatas, 70, 10, true);
//
//        mTL.addView(lgv);
//    }

    @Override
    public void onCreate(Bundle SavedBundle) {
        super.onCreate(SavedBundle);

        g_node_acc = new NodeDataSource(this);

        if (mICData.val_icid == 0) {
            Log.d(TAG, "Now is " + TAG);
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }
        setContentView(R.layout.act_testcsvview);
        loadView();

        mWorkThread = new HandlerThread("FunctionTwoWork");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());

        mContext = this;

        mText_freqNumber.setText(mTotalFreqN + "");
        mText_freq.setText("30k ~ 90k");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWorkThread.quit();
    }

    @Override
    public void onClick(View view) {
        if (view == mBtn_start) {
            mReady2Run = 1;
            freezeInput();
            mbasehopping = Integer.valueOf(mText_hopping_base.getText().toString());
            mlasthopping = Integer.valueOf(mText_hopping_last.getText().toString());
            mhoppingscale = Integer.valueOf(mText_hopping_scale.getText().toString());
            mTotalFreqN = (mbasehopping - mlasthopping) / mhoppingscale;
            mText_freqNumber.setText(Integer.toString(mTotalFreqN));
            mhopping_all_freq = new double[mTotalFreqN];
            Toast.makeText(HoppingCollectionChart.this, "Click volume up to start or down to reset.", Toast.LENGTH_LONG).show();
            return;
        }
        if (view == mBtn_back) {
            finish();
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //start process
            if (mReady2Run != 1) {
                Toast.makeText(HoppingCollectionChart.this, "Please press  start key first!", Toast.LENGTH_LONG).show();
                return true;
            }
            mReady2Run = 0;
            View graphic = mTL.findViewById(GRAPHIC_ID);
            if (graphic != null) {
                mTL.removeView(graphic);
            }

            mTotalFreqN = (mbasehopping - mlasthopping) / mhoppingscale;
            mText_freqNumber.setText(Integer.toString(mTotalFreqN));

            if (dialog_notouch != null) {
                dialog_notouch.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(HoppingCollectionChart.this);
            dialog_notouch = builder.create();
            dialog_notouch.setIcon(R.drawable.ic_launcher);

            dialog_notouch.setTitle("Alert Window");
            dialog_notouch.setMessage("Dont Touch Screen!!!");
            dialog_notouch.show();

            Message msg = Message.obtain();
            msg.what = MSG_STARTPROCESS;
            msg.arg1 = Integer.valueOf(mEdit_frames.getText().toString());
            mWorkHandler.sendMessage(msg);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            resumeInput();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void freezeInput() {
        mEdit_frames.setEnabled(false);
        mBtn_start.setEnabled(false);
        mText_hopping_base.setEnabled(false);
        mText_hopping_last.setEnabled(false);
        mText_hopping_scale.setEnabled(false);
    }

    private void resumeInput() {
        mEdit_frames.setEnabled(true);
        mBtn_start.setEnabled(true);
        mText_hopping_base.setEnabled(true);
        mText_hopping_last.setEnabled(true);
        mText_hopping_scale.setEnabled(true);
    }

    private static double hoppingAndCal(int i) {
        //temp
        double result = 0.0;
        try {
            g_node_acc.simpleWriteCMD(mICData.transform);
            Thread.sleep(100);

            Log.d("HXTPICFLOW", "sense off");
            //other setting
            g_node_acc.simpleWriteCMD(mICData.senseOff_CMD);
            Thread.sleep(100);

            //write node for hopping
            Log.d("HXTPICFLOW", "Enter hopping cmd");
            g_node_acc.simpleWriteCMD(mICData.hopping_reg_cmd);
            Thread.sleep(100);
            String[] command_hopping = main_hopping_func(g_node_acc.simpleReadCMD(mICData.hopping_reg_cmd),
                    mICData.hopping_reg_write, i);
            Thread.sleep(100);
            g_node_acc.simpleWriteCMD(command_hopping);
            Thread.sleep(100);

            Log.d("HXTPICFLOW", "reject idle mode");
            g_node_acc.simpleWriteCMD(mICData.idle_mode_CMD);
            Thread.sleep(100);

            Log.d("HXTPICFLOW", "disable flash reload");
            g_node_acc.simpleWriteCMD(mICData.disable_flash_reload_CMD);
            Thread.sleep(100);


            //decrease cc value
            Log.d("HXTPICFLOW", "cut off CC");
            Log.d("HXTPICFLOW", "C6 <=0x14");
            g_node_acc.simpleWriteCMD(mICData.iir_config_c4_cmd);
            Thread.sleep(100);
            String[] commandC4 = overrideThirdBytes(g_node_acc.simpleReadCMD(mICData.iir_config_c4_cmd),
                    mICData.iir_config_c4_write);
            g_node_acc.simpleWriteCMD(commandC4);
            Thread.sleep(100);
            Log.d("HXTPICFLOW", "CA <=0x14");
            g_node_acc.simpleWriteCMD(mICData.iir_config_c8_cmd);
            Thread.sleep(100);
            String[] commandC8 = overrideThirdBytes(g_node_acc.simpleReadCMD(mICData.iir_config_c8_cmd),
                    mICData.irr_config_c8_write);
            Thread.sleep(100);
            g_node_acc.simpleWriteCMD(commandC8);
            Thread.sleep(100);

            Log.d("HXTPICFLOW", "Sense on");
            g_node_acc.simpleWriteCMD(mICData.senseOn_CMD);
            Thread.sleep(100);

            //read node for hoppping
            Log.d("HXTPICFLOW", "getting hopping freq");
            Log.d("HXTPICFLOW", "First CLK Str");
            g_node_acc.simpleWriteCMD(mICData.rx_freq_clk_CMD);
            Thread.sleep(100);
            String first = g_node_acc.simpleReadCMD(mICData.rx_freq_clk_CMD);
            Thread.sleep(100);

            Log.d("HXTPICFLOW", "Second CLK Str");
            g_node_acc.simpleWriteCMD(mICData.rx_freq_clk2_CMD);
            Thread.sleep(100);
            String second = g_node_acc.simpleReadCMD(mICData.rx_freq_clk2_CMD);
            Thread.sleep(100);

            long firstCLK = parsingRegisterValue(first);
            Log.d("HXTPICFLOW", "First CLK Long" + Long.toString(firstCLK));
            long secondCLK = parsingRegisterValue(second);
            Log.d("HXTPICFLOW", "Second CLK Long" + Long.toString(secondCLK));
            double rx_frq = (OSC_HZ / firstCLK) / secondCLK;
            result = rx_frq;
            Log.d("HXTPICFLOW", "rx_frq = " + Double.toString(rx_frq));


        } catch (Exception e) {
            Log.e("HXTP", e.toString());
        }
        return result;
        //write read register.
    }

    private static void startProcess(int compareFrame) {
        if (mCSV == null) {
            mCSV = new CSVAccess();
        }

        int[] absMax = new int[mTotalFreqN];
        g_node_acc.simpleWriteCMD(mICData.dis_int_cmd);
        for (int i = 0; i < mTotalFreqN; i++) {
            //hopping freq
            //mhopping_all_freq[i] = hoppingAndCal(base_freq_val-i*5)/1000.0;
            mhopping_all_freq[i] = hoppingAndCal(mbasehopping - i * mhoppingscale) / 1000.0;
            double hopping = mhopping_all_freq[i];
            StringBuilder bb = new StringBuilder();
            ArrayList<int[][]> compareData = new ArrayList<int[][]>();

            g_node_acc.simpleWriteCMD(mICData.IIR_CMD);

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("HXTP", e.toString());
            }

            for (int j = 0; j < compareFrame; j++) {
                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_REAL_TIME;
                msg.arg1 = j;
                msg.obj = hopping;
                mHandler.sendMessage(msg);
                int[][] s = catchFrames((i == 0 && j == 0), hopping, j);
                compareData.add(s);
            }
            /* RETURN NORMAL status*/
            g_node_acc.simpleWriteCMD(mICData.back2normal_CMD);
            g_node_acc.simpleWriteCMD(mICData.hopping_CMD[2]);

            //resume other settings
            g_node_acc.simpleWriteCMD(mICData.senseOff_CMD);
            g_node_acc.simpleWriteCMD(mICData.enable_flash_reload_CMD);
            g_node_acc.simpleWriteCMD(mICData.senseOn_CMD);


            int rowMax = compareData.get(0).length;
            int colMax = compareData.get(0)[0].length;

            int[][] max = new int[rowMax][colMax];
            int[][] min = new int[rowMax][colMax];
            int[][] diff = new int[rowMax][colMax];

            //find max min diff
            for (int col = 0; col < colMax; col++) {
                for (int row = 0; row < rowMax; row++) {
                    int max_current = -10000;
                    int min_current = 10000;
                    for (int num = 0; num < compareData.size(); num++) {
                        int[][] now = compareData.get(num);
                        if (max_current < now[row][col]) {
                            max_current = now[row][col];
                        }
                        if (min_current > now[row][col]) {
                            min_current = now[row][col];
                        }

                    }
                    max[row][col] = max_current;
                    min[row][col] = min_current;
                    diff[row][col] = max_current - min_current;
                }
            }

            mCSV.appedMaxMinDIff(hopping, max, min, diff);
            for (int dI = 0; dI < max.length; dI++) {
                for (int dJ = 0; dJ < max[0].length; dJ++) {
                    if (Math.abs(max[dI][dJ]) > Math.abs(absMax[i])) {
                        absMax[i] = max[dI][dJ];
                    }
                    if (Math.abs(min[dI][dJ]) > Math.abs(absMax[i])) {
                        absMax[i] = min[dI][dJ];
                    }
                    if (absMax[i] > mMAXEdge)
                        mMAXEdge = absMax[i];
                }
            }

//            StringBuilder sb = new StringBuilder();
//            for(int x=0;x<32;x++) {
//                for(int xx=0;xx<18;xx++) {
//                    sb.append("  ");
//                    sb.append(max[x][xx] + "/" + min[x][xx] + "/" + diff[x][xx]);
//                }
//                sb.append("\n");
//            }
//            Log.d("TEST", sb.toString());

        }
        g_node_acc.simpleWriteCMD(mICData.en_int_cmd);
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FINISH;
        msg.obj = absMax;
        mHandler.sendMessage(msg);
        g_node_acc.simpleWriteCMD(mICData.back2normal_CMD);
    }

    private static long parsingRegisterValue(String data) {
        if ("".equals(data)) {
            return 1;
        }

        String[] temp = data.split("\n");
        String[] byteArray = temp[1].split("\\s+");
        Log.d("HXTP", "hopping cmd all =" + temp[1]);
        int[] bytes = new int[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = Integer.parseInt(byteArray[i].substring(2, 4), 16);
        }
        Log.d("HXTP", "hopping cmd [0]=" + bytes[0] + "[1]=" + bytes[1] + "[2]=" + bytes[2] + "[3]=" + bytes[3]);
        long result;
        result = (long) (bytes[3] << (24)) + (long) (bytes[2] << (16))
                + (long) (bytes[1] << 8) + (long) (bytes[0]);
        return (result <= 0) ? 1 : result;
    }

    private static String[] main_hopping_func(String data, String typeString, int hopping_val) {
        if ("".equals(data)) {
            return null;
        }
        String[] temp = data.split("\n");
        String[] byteArray = temp[1].split("\\s+");
        Log.d("HXTP", "cut off cc str[0]=" + byteArray[0] + "[1]=" + byteArray[1] + "[2]=" + byteArray[2] + "[3]=" + byteArray[3]);
        StringBuilder sb = new StringBuilder();
        sb.append("x");
        for (int i = 3; i >= 0; i--) {
            String[] x = byteArray[i].split("x");
            if (hopping_val > 256) {
                if (i == 0) {
                    sb.append(String.format("%02x", (hopping_val % 256)));
                } else if (i == 1) {
                    //sb.append(String.format("%02x",Integer.toHexString(hopping_val%256)));
                    sb.append(String.format("%02x", hopping_val / 256));
                } else {
                    sb.append(x[1]);
                }
            } else {
                if (i == 0) {
                    sb.append(String.format("%02x", hopping_val));
                } else {
                    sb.append(x[1]);
                }
            }

        }
        Log.d("HXTP", " ready to wrte =" + sb.toString());
        sb.append("\n");
        String[] out = new String[]{mICData.reg_path, typeString + ":" + sb.toString()};
        return out;
    }

    private static String[] overrideThirdBytes(String data, String typeString) {
        if ("".equals(data)) {
            return null;
        }
        String[] temp = data.split("\n");
        String[] byteArray = temp[1].split("\\s+");
        Log.d("HXTP", "cut off cc str[0]=" + byteArray[0] + "[1]=" + byteArray[1] + "[2]=" + byteArray[2] + "[3]=" + byteArray[3]);
        StringBuilder sb = new StringBuilder();
        sb.append("x");
        for (int i = 3; i >= 0; i--) {
            String[] x = byteArray[i].split("x");
            if (i == 1) {
                sb.append("14");
            } else {
                sb.append(x[1]);
            }

        }
        Log.d("HXTP", " ready to wrte =" + sb.toString());
        sb.append("\n");
        String[] out = new String[]{mICData.reg_path, typeString + ":" + sb.toString()};
        return out;
    }

    private static int[][] catchFrames(boolean overrideFile, double hopping, int frame) {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String rawdata = g_node_acc.simpleReadCMD(mICData.IIR_CMD);

        return mCSV.parseValueNappendFile(mContext, hopping, frame, rawdata, overrideFile, true);
    }
}
