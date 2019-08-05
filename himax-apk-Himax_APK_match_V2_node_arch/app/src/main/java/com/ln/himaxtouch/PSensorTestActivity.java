package com.ln.himaxtouch;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.himaxtouch.ObjectiveTest.ObjectiveTestController;

import static com.ln.himaxtouch.HimaxApplication.MSG_START_P_SENSOR_TEST;

/**
 * Created by 903622 on 2018/4/25.
 */

public class PSensorTestActivity extends Activity {

    private final static boolean DEBUG = true;
    private final static String TAG = "PSensorTestActivity";

    private HimaxApplication mApplication;
    private ObjectiveTestController mController;
    private TextView mTvStatus;
    private TextView mTvBrightness;
    private TextView mTvBlockNum;
    private EditText mEdVibrator;
    private EditText mEdSensingThreshold;
    private EditText mEdUpperSensingThreshold;
    private EditText mEdProxmiityDebug;
    private EditText mEdProximityValue;
    private EditText mEdProximityUpdate;
    private Button mResetButton;
    private RelativeLayout mLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_psensor_test);

        mApplication = (HimaxApplication) getApplicationContext();
        mController = mApplication.getObjectiveTestController();

        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvBrightness = (TextView) findViewById(R.id.tv_brightness);
        mLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mEdVibrator = (EditText) findViewById(R.id.ed_vibrator);
        mTvBlockNum = (TextView) findViewById(R.id.tv_block_num);
        mEdSensingThreshold = (EditText) findViewById(R.id.ed_sensing_threshold);
        mEdUpperSensingThreshold = (EditText) findViewById(R.id.ed_sensing_threshold_upper);
        mEdProxmiityDebug = (EditText) findViewById(R.id.ed_sensing_proximity_debug);
        mEdProximityValue = (EditText) findViewById(R.id.ed_sensing_proximity_value);
        mEdProximityUpdate = (EditText) findViewById(R.id.ed_sensing_proximity_bl_update);
        mResetButton = (Button) findViewById(R.id.bt_reset);
        mController.mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResetButton.setEnabled(false);
                mController.mPSensorTestModel.isTerminated = true;
                mApplication.mWorkerHandler.sendEmptyMessageDelayed(MSG_START_P_SENSOR_TEST, 5000);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mController.bindPSensorView(mTvStatus, mTvBrightness, mLayout, mEdVibrator, mTvBlockNum, mEdSensingThreshold,
                mEdUpperSensingThreshold, mEdProxmiityDebug, mEdProximityValue, mEdProximityUpdate, mResetButton);
        mApplication.mWorkerHandler.sendEmptyMessage(MSG_START_P_SENSOR_TEST);
        mController.mPSensorTestModel.isTerminated = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mController.mPSensorTestModel.unbindViews();
        mController.mPSensorTestModel.clearTestData();
    }
}
