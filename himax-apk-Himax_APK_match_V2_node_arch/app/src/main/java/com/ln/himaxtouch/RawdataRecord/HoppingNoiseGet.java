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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.ICInfo.ICData;
import com.ln.himaxtouch.NodeDataSource;
import com.ln.himaxtouch.R;

import java.text.DecimalFormat;
import java.util.Calendar;

import static com.ln.himaxtouch.HimaxApplication.mICData;

/**
 * Created by Einim on 2017/10/25.
 */

public class HoppingNoiseGet extends Activity implements View.OnClickListener
{
    String total_freq[] = {"f1","f2"};

    static EditText exe_time;
    static EditText delta_val;

    static TextView ela_time;
    static TextView current_frame;
    static TextView current_freq;
    static TextView current_freq_seed;
    static TextView failframe_num;
    static TextView fail_rate;
    static TextView file_path;
    static TextView total_freq_show;
    static TextView status;

    static TextView info;
    static TextView info2;

    static Button start_btn;
    Button back_btn;

    static int g_flag = 0;
    int g_flag2 = 0;
    int g_fail_times = 0;
    int g_run_count = 50;
    String g_max_str;
    String g_min_str;

    int g_delta_val = 0;



    static NodeDataSource g_node_acc;
    static private ICData gICData;
    RawdataProcess g_data_proc = new RawdataProcess();
    RWFile g_w2sd = new RWFile();
    static AlertDialog dialog_notouch;
    static CSVAccess mCSV;

    //BEGIN:Steve_Ke
    private static long mStartTime;
    private static long mProcessTotalFrame;
    private static double[] mSeedFreq = new double[2];
    static TableLayout mTableTwo;
    private static Context mContext;
    private static final int OSC_HZ = 48*1000*1000;
    private static final int STATE_PROCESSING = 0;
    private static final int STATE_FINISH = 1;
    private static final int STATE_SUB_FINISH = 2;
    private static final int MSG_UPDATE_VIEW = 0;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_VIEW: {
                    int[] parameter = (int[]) msg.obj; // 0:num 1:frqSeed 2:STATE 3:max 4:min 5:failNum 6:elapseTime
                    int now_frame = parameter[0];
                    int current_seed = parameter[1];
                    int state = parameter[2];
                    int subFailNum = parameter[5];
                    int elapseTime = parameter[6];
                    int min = elapseTime/60;
                    int sec = elapseTime%60;
                    String minStr = (min >= 10) ? (""+min) : ("0"+min);
                    String secStr = (sec >= 10) ? (""+sec) : ("0"+sec);;
                    ela_time.setText(minStr + ":" + secStr);
                    if (state == STATE_FINISH) {
                        if(dialog_notouch.isShowing())
                            dialog_notouch.dismiss();

                        file_path.setText(mCSV.mFileName);
                        status.setText("FINISH");
                        StringBuilder totalFreq = new StringBuilder();
                        for(int i=0;i<mSeedFreq.length;i++) {
                            double f = mSeedFreq[i] / (double) 1000;
                            totalFreq.append(f+", ");
                        }
                        total_freq_show.setText(totalFreq.toString());

                        double failRate = (Double.valueOf(failframe_num.getText().toString()) / mProcessTotalFrame) * (double)  100;
                        DecimalFormat df = new DecimalFormat("#.00");
                        fail_rate.setText(df.format(failRate) + "%");

                        resumeView();
                        break;
                    }
                    current_frame.setText(Integer.toString(now_frame));
                    current_freq_seed.setText(""+(current_seed+1));
                    double currentF = mSeedFreq[current_seed] / (double) 1000;
                    current_freq.setText(currentF+" kHZ");
                    if (state == STATE_SUB_FINISH) {
                        int failNum = Integer.valueOf(failframe_num.getText().toString());
                        failNum = failNum + subFailNum;
                        failframe_num.setText(String.valueOf(failNum));
                        String result = (subFailNum>0)?"FAIL":"PASS";
                        double cf = mSeedFreq[current_seed] / (double) 1000;
                        String[] tableData = {String.valueOf(current_seed+1), String.valueOf(cf),
                                String.valueOf(parameter[3]), String.valueOf(parameter[4]), result, (subFailNum + "/" + now_frame)};
                        TableRow tableRow = new TableRow(mContext);
                        for(int c=0; c<tableData.length; c++)
                        {
                            TextView tv=new TextView(mContext);
                            tv.setText(tableData[c]+"  ");
                            tableRow.addView(tv);
                        }
                        mTableTwo.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        mProcessTotalFrame += Long.valueOf(current_frame.getText().toString());
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    private static void forbidView() {
        exe_time.setEnabled(false);
        delta_val.setEnabled(false);
        start_btn.setEnabled(false);
    }
    private static void resumeView() {
        exe_time.setEnabled(true);
        delta_val.setEnabled(true);
        start_btn.setEnabled(true);
    }

    private static final int MSG_PARSING_XML = 0;
    private static final int MSG_SET_TEST_FRQ = 1;
    private static final int MSG_START_CATCH_FRAMES = 2;
    private HandlerThread mWorkThread;
    private static WorkHandler mWorkHandler;
    private static class WorkHandler extends Handler {
        public WorkHandler(Looper l) {
            super(l);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PARSING_XML:
                    break;
                case MSG_SET_TEST_FRQ:
                    break;
                case MSG_START_CATCH_FRAMES:
                    mProcessTotalFrame = 0;
                    for (int i=0; i<mSeedFreq.length; i++) {
                        try {
                            //Transform matrix
                            g_node_acc.simpleWriteCMD(gICData.transform);
                            Thread.sleep(100);

                            mStartTime = Calendar.getInstance().getTimeInMillis();

                            Thread.sleep(1000);

                            //write node for hopping
                            Log.d("HXTPICFLOW","Enter hopping cmd");
                            g_node_acc.simpleWriteCMD(gICData.hopping_CMD[i]);
                            Thread.sleep(100);

                            Log.d("HXTPICFLOW","sense off");
                            //other setting
                            g_node_acc.simpleWriteCMD(gICData.senseOff_CMD);
                            Thread.sleep(100);

                            Log.d("HXTPICFLOW","reject idle mode");
                            g_node_acc.simpleWriteCMD(gICData.idle_mode_CMD);
                            Thread.sleep(100);

                            Log.d("HXTPICFLOW","disable flash reload");
                            g_node_acc.simpleWriteCMD(gICData.disable_flash_reload_CMD);
                            Thread.sleep(100);


                            //decrease cc value
                            Log.d("HXTPICFLOW","cut off CC");
                            Log.d("HXTPICFLOW","C6 <=0x14");
                            g_node_acc.simpleWriteCMD(gICData.iir_config_c4_cmd);
                            Thread.sleep(100);
                            String[] commandC4 = overrideThirdBytes(g_node_acc.simpleReadCMD(gICData.iir_config_c4_cmd),
                                    gICData.iir_config_c4_write);
                            g_node_acc.simpleWriteCMD(commandC4);
                            Thread.sleep(100);
                            Log.d("HXTPICFLOW","CA <=0x14");
                            g_node_acc.simpleWriteCMD(gICData.iir_config_c8_cmd);
                            Thread.sleep(100);
                            String[] commandC8 = overrideThirdBytes(g_node_acc.simpleReadCMD(gICData.iir_config_c8_cmd),
                                    gICData.irr_config_c8_write);
                            Thread.sleep(100);
                            g_node_acc.simpleWriteCMD(commandC8);
                            Thread.sleep(100);


                            // Log.d("HXTPICFLOW","Sense on");

                            //read node for hoppping
                            Log.d("HXTPICFLOW","getting hopping freq");
                            Log.d("HXTPICFLOW","First CLK Str");
                            g_node_acc.simpleWriteCMD(gICData.rx_freq_clk_CMD);
                            Thread.sleep(100);
                            String first = g_node_acc.simpleReadCMD(gICData.rx_freq_clk_CMD);
                            Thread.sleep(100);

                            Log.d("HXTPICFLOW","Second CLK Str");
                            g_node_acc.simpleWriteCMD(gICData.rx_freq_clk2_CMD);
                            Thread.sleep(100);
                            String second =g_node_acc.simpleReadCMD(gICData.rx_freq_clk2_CMD);
                            Thread.sleep(100);

                            long firstCLK = parsingRegisterValue(first);
                            Log.d("HXTPICFLOW","First CLK Long"+Long.toString(firstCLK));
                            long secondCLK = parsingRegisterValue(second);
                            Log.d("HXTPICFLOW","Second CLK Long"+Long.toString(secondCLK));
                            double rx_frq = (OSC_HZ/firstCLK)/secondCLK;
                            mSeedFreq[i] = rx_frq;

                            Log.d("HXTPICFLOW","Sense on");

                            g_node_acc.simpleWriteCMD(gICData.senseOn_CMD);
                            Thread.sleep(100);
                            catchFrames(msg.arg1, i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //reset hopping
                            g_node_acc.simpleWriteCMD(gICData.back2normal_CMD);
                            g_node_acc.simpleWriteCMD(gICData.hopping_CMD[2]);

                            //resume other settings
                            g_node_acc.simpleWriteCMD(gICData.senseOff_CMD);
                            g_node_acc.simpleWriteCMD(gICData.enable_flash_reload_CMD);
                            g_node_acc.simpleWriteCMD(gICData.senseOn_CMD);
                        }
                    }
                    Message send = Message.obtain();
                    int[] parameter = {0, 0, STATE_FINISH, 0, 0, 0,
                            Integer.valueOf(exe_time.getText().toString())}; // 0:num 1:frqSeed 2:STATE 3:max 4:min 5:failNum
                    send.obj = parameter;
                    send.what = MSG_UPDATE_VIEW;
                    mHandler.sendMessage(send);
                    break;
                default:
                    break;
            }
        }
    }

    private static long parsingRegisterValue(String data) {
        if ("".equals(data)) {
            return 1;
        }

        String[] temp = data.split("\n");
        String[] byteArray = temp[1].split("\\s+");
        Log.d("HXTP","hopping cmd all ="+temp[1]);
        int[] bytes = new int[4];
        for (int i=0; i<4; i++) {
            bytes[i] = Integer.parseInt(byteArray[i].substring(2, 4),16);
        }
        Log.d("HXTP","hopping cmd [0]="+bytes[0]+"[1]="+bytes[1]+"[2]="+bytes[2]+"[3]="+bytes[3]);
        long result;
        result = (long)(bytes[3] << (24)) + (long)(bytes[2] << (16))
        + (long)(bytes[1] << 8)+ (long)(bytes[0]);
        return (result<=0)?1:result;
    }

    private static String[] overrideThirdBytes(String data, String typeString) {
        if ("".equals(data)) {
            return null;
        }
        String[] temp = data.split("\n");
        String[] byteArray = temp[1].split("\\s+");
        Log.d("HXTP","cut off cc str[0]="+byteArray[0]+"[1]="+byteArray[1]+"[2]="+byteArray[2]+"[3]="+byteArray[3]);
        StringBuilder sb = new StringBuilder();
        sb.append("x");
        for (int i=3; i>=0; i--) {
            String[] x = byteArray[i].split("x");
            if (i==1) {
                sb.append("14");
            } else {
                sb.append(x[1]);
            }

        }
        Log.d("HXTP"," ready to wrte ="+sb.toString());
        sb.append("\n");
        String[] out = new String[] {gICData.reg_path, typeString + ":" + sb.toString()};
        return out;
    }

    private static void catchFrames(int execSeconds, int frqSeed) {
        mCSV.resetValue();
        int max=-10000;
        int min=10000;
        int failFrame = 0;
        int num=0;
        long startTime; //= Calendar.getInstance().getTimeInMillis();
        long currentTime;// = startTime;
        g_node_acc.simpleWriteCMD(gICData.IIR_CMD);

        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if(frqSeed == 0) {
            mCSV.newAnotherFileName(mContext);
        }

        startTime = Calendar.getInstance().getTimeInMillis();
        currentTime = startTime;

        while ((currentTime-startTime) <= (execSeconds*1000)) {
            try {
                Thread.sleep(100);
                String rawdata = g_node_acc.simpleReadCMD(gICData.IIR_CMD);

                mCSV.appendRawData1(mSeedFreq[frqSeed], num, rawdata,
                        Integer.valueOf(delta_val.getText().toString()), true);
                if (max<mCSV.mMax) {
                    max = mCSV.mMax;
                }
                if (min>mCSV.mMin) {
                    min = mCSV.mMin;
                }
                if (mCSV.mFail) {
                    failFrame++;
                }

                num++;
                currentTime = Calendar.getInstance().getTimeInMillis();

                int elapasedTime = (int) (currentTime - startTime)/1000;

                Message msg = Message.obtain();
                int[] parameter = new int[7]; // 0:num 1:frqSeed 2:STATE 3:max 4:min 5:failNum 6:elapsedTime
                parameter[0] = num;
                parameter[1] = frqSeed;
                parameter[2] = ((currentTime-startTime) >= (execSeconds*1000)) ? STATE_SUB_FINISH : STATE_PROCESSING;
                parameter[3] = max;
                parameter[4] = min;
                parameter[5] = failFrame;
                parameter[6] = elapasedTime;
                msg.obj = parameter;
                msg.what = MSG_UPDATE_VIEW;
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                Log.d("Error", e.fillInStackTrace().toString());
                e.fillInStackTrace();
            }
        }

    }

    private String[] mTableTitle =
            {"Seed#", "Freq", "Max", "Min", "Result", "Failed/Total Frame"};
    private void fillTableTitle() {
        mTableTwo.removeAllViews();
        TableRow tableRow=new TableRow(this);
        for(int c=0; c<mTableTitle.length; c++)
        {
            TextView tv=new TextView(this);
            tv.setText(mTableTitle[c]+"  ");
            tableRow.addView(tv);
        }
        mTableTwo.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        failframe_num.setText("0");
    }
    //END:Steve_Ke

    @Override
    public void onCreate(Bundle SavedBundle)
    {
        super.onCreate(SavedBundle);
        g_node_acc = new NodeDataSource(this);

        if(mICData.val_icid == 0) {
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
            
        }
        gICData = mICData;
        
        
        setContentView(R.layout.act_noiseget);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        exe_time = (EditText)findViewById(R.id.exe_time);
        delta_val = (EditText)findViewById(R.id.delta_val);

        ela_time = (TextView)findViewById(R.id.ela_time);
        current_frame = (TextView)findViewById(R.id.current_frame);
        current_freq = (TextView)findViewById(R.id.current_freq);
        current_freq_seed = (TextView)findViewById(R.id.current_freq_seed);
        failframe_num = (TextView)findViewById(R.id.failframe_num);
        fail_rate = (TextView)findViewById(R.id.fail_rate);
        file_path = (TextView)findViewById(R.id.file_path);
        total_freq_show = (TextView)findViewById(R.id.total_freq_show);
        status = (TextView)findViewById(R.id.status);

//        info = (TextView)findViewById(R.id.info);
//        info2 = (TextView)findViewById(R.id.info2);

        start_btn = (Button)findViewById(R.id.start_btn);
        back_btn = (Button)findViewById(R.id.back_btn);
        start_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);

        //BEGIN:Steve_Ke
        mContext = this;
        mCSV = new CSVAccess();
        mWorkThread = new HandlerThread("WorkThread");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
        mTableTwo = (TableLayout)findViewById(R.id.t2);
        fillTableTitle();
        //BEGIN:Steve_Ke
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWorkThread.quit();
    }

    @Override
    public void onClick(View v) {
        if(v == start_btn)
        {
            if(exe_time.getText().toString()== null || exe_time.getText().toString().isEmpty()) {
//                                Toast.makeText(NoiseGet.this, "execute time is null!", Toast.LENGTH_LONG).show();
            }
            else if(delta_val.getText().toString()== null || delta_val.getText().toString().isEmpty()) {
                Toast.makeText(HoppingNoiseGet.this, "Delta field is null!", Toast.LENGTH_LONG).show();
            }
            else {

                forbidView();
                g_delta_val = Integer.parseInt(delta_val.getText().toString());

                Toast.makeText(HoppingNoiseGet.this, "Click volume up to start or down to reset.", Toast.LENGTH_LONG).show();
            }
        }
        else if(v == back_btn)
        {
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            if(g_delta_val == 0)
            {
                Toast.makeText(HoppingNoiseGet.this, "delta value fail", Toast.LENGTH_LONG).show();
                return true;
            }

//            Toast.makeText(NoiseGet.this, "TOPPPPPPPP", Toast.LENGTH_LONG).show();
            if (dialog_notouch != null) {
                dialog_notouch.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(HoppingNoiseGet.this);
            dialog_notouch = builder.create();
            dialog_notouch.setTitle("Alert Window");
            dialog_notouch.setMessage("Dont Touch Screen!!!");
            dialog_notouch.show();

            fillTableTitle();
            Message msg = Message.obtain();
            msg.what = MSG_START_CATCH_FRAMES;
            msg.arg1 = Integer.valueOf(exe_time.getText().toString()); //how long(sec)
            mWorkHandler.sendMessage(msg);


            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            if(g_delta_val == 0)
            {
                Toast.makeText(HoppingNoiseGet.this, "delta value fail", Toast.LENGTH_LONG).show();
                return true;
            }

//            Toast.makeText(NoiseGet.this, "TOPPPPPPPP", Toast.LENGTH_LONG).show();
            if (dialog_notouch != null) {
                dialog_notouch.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialog_notouch = builder.create();
            dialog_notouch.setTitle("Alert Window");
            dialog_notouch.setMessage("Dont Touch Screen!!!");
            dialog_notouch.setIcon(R.drawable.ic_launcher);
            dialog_notouch.show();

            fillTableTitle();
            Message msg = Message.obtain();
            msg.what = MSG_START_CATCH_FRAMES;
            msg.arg1 = Integer.valueOf(exe_time.getText().toString()); //how long(sec)
            mWorkHandler.sendMessage(msg);

            return true;
            /*if (dialog_notouch != null) {
                dialog_notouch.dismiss();
            }
            resumeView();
            return true;*/
        }
        return true;
    }

}
