package com.ln.himaxtouch;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TotalSelfTestActivity extends Activity implements View.OnClickListener {
    public String proc_dir_node;
    public String proc_diag_node;

    SharedPreferences sh_settings;
    public Button touch_monitor_CLOSE_btn;
    public EditText IIRTestCondition;
    public EditText IIRSum;
    public EditText IIRSelfTestResult;
    public EditText FrameTimeEdit;

    static {
        System.loadLibrary("HimaxAPK");
    }
    //

    //Native function
    public native String writeCfg(String[] stringArray);

    public native String readCfg(String[] stringArray);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwupgrade);

        touch_monitor_CLOSE_btn = (Button) findViewById(R.id.fw_up_browse_btn);
        touch_monitor_CLOSE_btn.setOnClickListener(this);

        IIRTestCondition = (EditText) findViewById(R.id.fw_up_path_editText);
        IIRSum = (EditText) findViewById(R.id.EditText01);
        IIRSelfTestResult = (EditText) findViewById(R.id.EditText02);
        FrameTimeEdit = (EditText) findViewById(R.id.EditText03);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fwupgrade, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int selfTestCondition = Integer.valueOf(IIRTestCondition.getText().toString());
        int SumOfIIR = 0;
        int MaxSumOfIIR = 0;
        int RXNumber = 0;
        int TXNumber = 0;
        int FrameTime = Integer.valueOf(FrameTimeEdit.getText().toString());

        int temp_int = 0;
        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_diag_node = sh_settings.getString("SETUP_DIAG_NODE", "diag");


        String temp_str = "";
        String[] strings = {proc_diag_node, "2\n"};
        String[] out_strings = {proc_diag_node};
        String line = null;
        String[] split_line = null;
        String[] split_line1 = null;
        String[] split_sub = null;


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        writeCfg(strings);


        for (int timer = 0; timer < FrameTime; timer++) {
            SumOfIIR = 0;
            line = readCfg(out_strings);

            split_line = line.split("\n");
            split_line1 = split_line[0].split("  ");

            RXNumber = Integer.valueOf(split_line1[1].trim().substring(0, split_line1[1].trim().length() - 1));
            TXNumber = Integer.valueOf(split_line1[2].trim());

            for (int i = 0; i < TXNumber; i++) {
                for (int j = 0; j < RXNumber; j++) {
                    temp_str = split_line[i + 2].substring(j * 4, j * 4 + 4);
                    temp_int = Integer.valueOf(temp_str.trim());
                    if (temp_int == 0)
                        SumOfIIR += 1;
                    else
                        SumOfIIR += temp_int;
                }
            }
            if (SumOfIIR > MaxSumOfIIR) {
                MaxSumOfIIR = SumOfIIR;
            }
        }

        IIRSum.setText(Integer.toString(MaxSumOfIIR));

        if (MaxSumOfIIR > selfTestCondition) {
            IIRSelfTestResult.setText("FAIL");
        } else {
            IIRSelfTestResult.setText("PASS");
        }
    }

}
