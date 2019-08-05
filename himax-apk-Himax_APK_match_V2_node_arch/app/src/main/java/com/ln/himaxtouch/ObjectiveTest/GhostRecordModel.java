package com.ln.himaxtouch.ObjectiveTest;

import android.view.View;
import android.widget.RelativeLayout;

import com.ln.himaxtouch.CustomView.PointRecordResultView;

import java.util.ArrayList;

/**
 * Created by 903622 on 2018/4/16.
 */

public class GhostRecordModel implements IObjectiveTestModel {

    public RelativeLayout mLayout;
    public int mBackgroundViewId = 0;
    public ArrayList<float[]> mRecordPoints;
    public PointRecordResultView mProgressView;
    public long mStartTime = 0;
    public long mEndTime = 0;
    public String mPathToSave;

    public GhostRecordModel() {
        mRecordPoints = new ArrayList<float[]>();
    }

    @Override
    public String getRecordStringToSaveCsv() {
        return null;
    }

    @Override
    public void unbindViews() {
        if(mLayout != null) {
            View background = mLayout.findViewById(mBackgroundViewId);
            if(background != null) {
                mLayout.removeView(background);
            }
        }
        mLayout = null;
        mProgressView = null;
    }

    @Override
    public void clearTestData() {
        mRecordPoints.clear();
        mStartTime = 0;
        mEndTime = 0;
        mPathToSave = "";
    }
}
