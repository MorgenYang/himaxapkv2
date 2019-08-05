package com.ln.himaxtouch.ObjectiveTest;

import android.view.View;
import android.widget.RelativeLayout;

import com.ln.himaxtouch.CustomView.ObjectivePatternView;

/**
 * Created by 903622 on 2018/4/19.
 */

public class PatternModel implements IObjectiveTestModel{

    public ObjectivePatternView.Cell[][] mPatternCells;
    private static final int RESOLUTION_DEFAULT = 20;
    public int mResolution = RESOLUTION_DEFAULT;
    public RelativeLayout mLayout;
    public int mTrackViewId;

    public int mLineWidth = 3;
    public int mVerticalLineNum = 3;
    public int mHorizontalLineNum = 3;
    public int mLineSpace = 5;

    public PatternModel() {

    }

    public void createFitScaleCells(int layout_width, int layout_length) {
        int max_resolution = (layout_length > layout_width) ? layout_width : layout_length;
        mResolution -= (layout_length > layout_width) ? (layout_width%mResolution) : (layout_length%mResolution);
        if (mResolution > max_resolution) {
            mResolution = max_resolution;
        }

        int row_num = (layout_width / mResolution);
        int col_num = (layout_length / mResolution) + 1;

        mPatternCells = new ObjectivePatternView.Cell[row_num][col_num];

        for (int i = 0; i < row_num; i++) {
            for (int j = 0; j < col_num; j++) {
                int cellX = i * mResolution;
                int cellY = j * mResolution;
                ObjectivePatternView.Cell c = new ObjectivePatternView.Cell(cellX, cellY, mResolution);
                mPatternCells[i][j] = c;
            }
        }
    }

    @Override
    public String getRecordStringToSaveCsv() {
        return null;
    }

    @Override
    public void unbindViews() {
        if(mLayout != null) {
            View v = (View) mLayout.findViewById(mTrackViewId);
            if(v != null) {
                mLayout.removeView(v);
            }
            mLayout = null;
        }
    }

    @Override
    public void clearTestData() {
        mPatternCells = null;
    }
}
