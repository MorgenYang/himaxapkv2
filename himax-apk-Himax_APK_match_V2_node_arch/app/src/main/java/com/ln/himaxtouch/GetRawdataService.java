package com.ln.himaxtouch;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;

import static com.ln.himaxtouch.HimaxApplication.mICData;

/**
 * Created by 902995 on 2018/4/19.
 */

public class GetRawdataService extends Service {

    private static final String TAG = "[HXTP]GetRawdataService";

    private Handler mMainWork;
    private NodeDataSource mNodeOp;
    private himax_config mHXConfig;
    private String mFileStr;

    private Runnable mWorkDescription = new Runnable() {
        @Override
        public void run() {
            String result = null;
            String cmd[] = {mNodeOp.setup_diag};
//            mtestwork();
//            Log.d("HXTP","onStartCommand Now mtestint="+Integer.toString(mtestint));

            result = mNodeOp.simpleReadCMD(cmd);
            mHXConfig.mStoreData(result, mFileStr);
            Log.d(TAG, "GetRawdataService" + result);
            Intent intent = new Intent();
            intent.putExtra("hx_rawdata", result);
            intent.setAction("himax.broadcast.rawdata");
            sendBroadcast(intent);
            mMainWork.postDelayed(this, 100);
        }
    };

    public int mtestint = 0;

    public void mtestwork() {
        mtestint++;
    }

    public int mtgetestwork() {
        return mtestint;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "in onCreate");
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss.ssss");
        String date = sDateFormat.format(new java.util.Date());
        mFileStr = "service" + date;
        mMainWork = new Handler();
        mNodeOp = new NodeDataSource(this);
        mHXConfig = new himax_config(this);
        if (mICData.val_icid == 0) {
            Log.d(TAG, "Now is " + TAG);
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }

        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "in onStartCommand");
        mMainWork.post(mWorkDescription);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "in onDestroy");
        mMainWork.removeCallbacks(mWorkDescription);
        super.onDestroy();
    }


}
