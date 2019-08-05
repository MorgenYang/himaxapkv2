package com.ln.himaxtouch.ObjectiveTest;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ln.himaxtouch.RawdataRecord.CSVAccess;

/**
 * Created by 903622 on 2018/3/29.
 */

public class SnrModel implements IObjectiveTestModel {
    public final static int STATE_COLLECTING_UNTOUCHED_NOISE = 0;
    public final static int STATE_COLLECTED_UNTOUCHED_NOISE = 1;
    public final static int STATE_COLLECTING_TOUCHED_NOISE = 2;
    public final static int STATE_COLLECTED_TOUCHED_NOISE = 3;
    public final static int STATE_COLLECTED_FINISH = 4;

    public final static int RAW_BASE_FRAME = 20;
    public int mSignalType;
    public int mNoiseType;
    public int mNoiseThreshold;
    public int mRawBaseThreshold;
    public int mSignalTimeout;
    public int mCalculateFrames;
    public int mIgnoreFrames;
    public int mCollectingState;
    public int mCollectedNoiseFrames;
    public int mCollectedSignalFrames;
    public int mCollectedRawDataFrames;
    public int[][] mRawBaseData;
    public int mRowNum;
    public int mColNum;
    public float mTouchedX = 0;
    public float mTouchedY = 0;
    public double mResult_Signal = 130;
    public double mResult_Noise = 140;
    public double mResult_SNR = 150;
    public double mResult_SNR_MAX = 0;
    public int mMaxSignalForBP = 0;
    public long mMaxDCForBP = 1;
    public TextView mTitle;
    public TextView mMsg;
    public ProgressBar mPB_BaseRaw;
    public ProgressBar mPB_Item1;
    public ProgressBar mPB_Item1_skip;
    public ProgressBar mPB_Item2;
    public ProgressBar mPB_Item2_skip;
    public PopupWindow mPopup;

    public CSVAccess mCSV;

    public SnrModel() {
        mSignalType = 1;
        mNoiseType = 1;
        mNoiseThreshold = 150;
        mRawBaseThreshold = 10000;
        mSignalTimeout = 120;
        mCalculateFrames = 20;
        mIgnoreFrames = 20;
        mCollectingState = STATE_COLLECTING_UNTOUCHED_NOISE;
        mCollectedNoiseFrames = 0;
        mCollectedSignalFrames = 0;
        mCollectedRawDataFrames = 0;
        mRowNum = 0;
        mColNum = 0;

    }

    public void bindView(TextView title, TextView msg,
                         ProgressBar pb_baseraw, ProgressBar pb_item1, ProgressBar pb_item1_skip, ProgressBar pb_item2, ProgressBar pb_item2_skip, PopupWindow popup) {
        mTitle = title;
        mMsg = msg;
        mPB_BaseRaw = pb_baseraw;
        mPB_Item1 = pb_item1;
        mPB_Item1_skip = pb_item1_skip;
        mPB_Item2 = pb_item2;
        mPB_Item2_skip = pb_item2_skip;
        mPopup = popup;
    }

    @Override
    public String getRecordStringToSaveCsv() {
        return null;
    }

    @Override
    public void unbindViews() {
        mTitle = null;
        mMsg = null;
        mPB_Item1 = null;
        mPB_Item2 = null;
        mPopup = null;
    }

    @Override
    public void clearTestData() {
        mCollectingState = STATE_COLLECTING_UNTOUCHED_NOISE;
        mCollectedNoiseFrames = 0;
        mCollectedSignalFrames = 0;
        mCollectedRawDataFrames = 0;
        mTouchedX = 0;
        mTouchedY = 0;
        mRawBaseData = null;
        mRowNum = 0;
        mColNum = 0;
        mMaxDCForBP = 1;
        mMaxSignalForBP = 0;
    }


}
