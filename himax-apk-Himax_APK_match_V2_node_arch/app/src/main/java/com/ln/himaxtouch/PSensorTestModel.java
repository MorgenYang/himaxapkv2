package com.ln.himaxtouch;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.himaxtouch.ObjectiveTest.IObjectiveTestModel;

/**
 * Created by 903622 on 2018/4/25.
 */

public class PSensorTestModel implements IObjectiveTestModel{

    public boolean isTerminated = true;
    public TextView mTV_Status;
    public TextView mTV_Brightness;
    public TextView mTV_BlockNum;
    public EditText mED_Vibrator;
    public EditText mED_SensingThreshold;
    public EditText mED_SensingUpperThreshold;
    public EditText mEdProxmiityDebug;
    public EditText mEdProximityValue;
    public EditText mEdProximityUpdate;
    public Button mResetButton;
    public RelativeLayout mLayout;
    public int mMAX_SENSING_VALUE = 5000;
    public int mMIN_SENSING_VALUE = 2000;
    public int mBrightness = 0;
    public int mHysteresis = 5;
    public long mSensingValue = 0;
    public int mThrsholdVib = 80;
    public String mAddress_proximity_debug = "10007ff0";
    public String mAddress_proximity_value = "10007fec";
    public String mAddress_proximity_update = "10007fe8";
    public String mBlockNum;

    public PSensorTestModel() {
    }

    public void setBrightnessInPercentage(long sensingValue) {
        mSensingValue = sensingValue;
        if(sensingValue >= mMAX_SENSING_VALUE) {
            mBrightness = 0;
            return;
        }
        if(sensingValue <= mMIN_SENSING_VALUE) {
            mBrightness = 100;
            return;
        }

        double temp = (double) (sensingValue-mMIN_SENSING_VALUE) /  (double) (mMAX_SENSING_VALUE-mMIN_SENSING_VALUE);
        int brightness_temp = 100-(int)(temp*100);

        if(Math.abs(mBrightness-brightness_temp) > mHysteresis) {
            mBrightness = brightness_temp;
        }

    }

    @Override
    public String getRecordStringToSaveCsv() {
        return null;
    }

    @Override
    public void unbindViews() {
        mTV_Brightness = null;
        mTV_Status = null;
        mTV_BlockNum = null;
        mED_Vibrator = null;
        mED_SensingThreshold = null;
        mED_SensingUpperThreshold = null;
        mLayout = null;
    }

    @Override
    public void clearTestData() {
        mBrightness = 0;
        isTerminated = true;
    }
}
