package com.ln.himaxtouch.ICInfo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ln.himaxtouch.NodeDataSource;
import com.ln.himaxtouch.R;

import static com.ln.himaxtouch.HimaxApplication.mICData;

public class TestPrintRaw extends Activity {
    private static final String TAG = "[HXTP]TestPrintRaw";

    ICData gICData;

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.act_testprintraw);
        TextView tv = findViewById(R.id.test_rawdata_view);


        if(mICData.val_icid == 0) {
            Log.d(TAG, "Now is "+ TAG);
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }
        gICData = mICData;
        SharedPreferences sh_settings = this.getSharedPreferences("HIAPK", 0);
//        gConfig.diag_path = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/") + sh_settings.getString("SETUP_DIAG_NODE", "diag");
//        gConfig.reg_path = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/") + sh_settings.getString("SETUP_REGISTER_NODE", "register");
//        gConfig.reInitPara();
        Log.d("TestPrintRaw","now path = " + gICData.diag_path);
        NodeDataSource node_acc = new NodeDataSource(this);
        String diag2[] = {gICData.diag_path, "2\n"};
        String diag0[] = {gICData.diag_path, "0\n"};
        node_acc.writeCfgByJava(diag2);
        try {
            Thread.sleep(1000);
        } catch (Exception e){

        }
        String tmp  = node_acc.readCfgByJava(diag2);
        if(tmp.isEmpty()) {
            Log.d("TestPrintRaw", "Null");
        } else {
            Log.d("TestPrintRaw", "aaaa:"+tmp);
        }
        tv.setText(tmp);
        node_acc.writeCfgByJava(diag0);

    }
}
