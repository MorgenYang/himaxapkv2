package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.himaxtouch.DataMonitor.DataMonitorConfig;
import com.ln.himaxtouch.R;

/**
 * Created by 903622 on 2018/4/16.
 */

public class RawdataLayout extends RelativeLayout {

    private TextView[][] mTVs;
    private int mRow;
    private int mCol;
    private boolean isLocated;
    private Context mContext;
    private boolean isTransform = false;
    private int mScreenWidth;
    private int mScreenHeight;
    private static final int[][] COLOR_LEVEL = {
            {219, 238, 243}, {0, 128, 192},
            {255, 239, 156}, {255, 113, 40},
            {255, 239, 156}, {99, 190, 123}
    };

    public RawdataLayout(Context context, int row, int col, int layout_width, int layout_length, boolean isShowAreaInfo) {
        super(context);
        mContext = context;
        mScreenWidth = layout_width;
        mScreenHeight = layout_length;

        if ((layout_width > layout_length && col > row) || (layout_length > layout_width && row > col)) {
            mRow = col;
            mCol = row;
            isTransform = true;
        } else {
            mRow = row;
            mCol = col;
        }

        if (isShowAreaInfo) {
            mCol += 2;
        }

        mTVs = new TextView[mRow][mCol];

        isLocated = false;
        this.setBackgroundColor(Color.parseColor("#11000000"));
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(rlp);
    }

    private void setTVPosition() {
        float width_step = (float) mScreenWidth / (float) (mRow);
        float length_step = (float) mScreenHeight / (float) (mCol);

        for (int i = 0; i < mTVs.length; i++) {
            for (int j = 0; j < mTVs[0].length; j++) {
                //calculate position
                float x_position = mScreenWidth - (i * width_step) - (width_step);
                float y_position = mScreenHeight - (j * length_step) - (length_step);
                mTVs[i][j] = new TextView(mContext);
                mTVs[i][j].setTranslationX(x_position);
                mTVs[i][j].setTranslationY(y_position);
                mTVs[i][j].setWidth((int) (width_step + 0.5f));
                mTVs[i][j].setHeight((int) (length_step + 0.5f));
                mTVs[i][j].setTextColor(Color.parseColor("#444444"));
                mTVs[i][j].setTextSize(7);
                mTVs[i][j].setPadding(0, 0, 0, 0);
                mTVs[i][j].setGravity(Gravity.CENTER);
                mTVs[i][j].setBackground(getResources().getDrawable(R.drawable.stroke_rectangle));
                this.addView(mTVs[i][j]);
            }
        }
    }

    public void updateText(int[][] data, int colorType, int colorMaxValue, int textSize, boolean isShowBp, int[][] info, boolean isShowAreaInfo) {

        if (mTVs.length == 0) {
            return;
        }

        if (!isLocated) {
            setTVPosition();
            isLocated = true;
        }

        boolean isNeedChangeSize = false;
        if (mTVs[0][0].getTextSize() != textSize) {
            isNeedChangeSize = true;
        }
        int numColor = 15;
        int color_interval = colorMaxValue / numColor;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                int tmp_i = i;
                int tmp_j = j;
                if (isTransform) {
                    tmp_i = j;
                    tmp_j = i;
                }
                String s = (isShowBp) ? data[i][j] + "%" : data[i][j] + "";
                mTVs[tmp_i][tmp_j].setText(s);
                if (isNeedChangeSize) {
                    mTVs[tmp_i][tmp_j].setTextSize(textSize);
                }
                labelValueColorLevel(mTVs[tmp_i][tmp_j], data[i][j], color_interval, colorType, numColor, isShowBp);
            }
        }

        if (isShowAreaInfo) {
            for (int i = 0; i < info.length; i++) {
                for (int j = 0; j < info[0].length; j++) {
                    int tmp_i = (mTVs.length - 1) - j;
                    int tmp_j = (mTVs[0].length - 1) - i;
                    mTVs[tmp_i][tmp_j].setText(info[i][j] + "");
                    if (info[i][j] != 0) {
                        mTVs[tmp_i][tmp_j].setTextColor(Color.BLACK);
                        mTVs[tmp_i][tmp_j].setBackgroundColor(Color.parseColor("#00A000"));
                    } else {
                        mTVs[tmp_i][tmp_j].setTextColor(Color.GRAY);
                        mTVs[tmp_i][tmp_j].setBackgroundColor(Color.parseColor("#DDDDDD"));
                    }
                }
            }
        }
    }

    private void labelValueColorLevel(TextView tv, int value, int color_interval, int colorType, int numColorLevel, boolean isShowBP) {

        int option = colorType;

        if (color_interval == 0) {
            tv.setBackgroundColor(Color.rgb(COLOR_LEVEL[2 * option + 1][0], COLOR_LEVEL[2 * option + 1][1], COLOR_LEVEL[2 * option + 1][2]));
        } else {
            int index = value / color_interval;
            if (index >= numColorLevel) {
                index = numColorLevel;
            }
            if (isShowBP) {
                if (index >= (numColorLevel) * 6 / 10) {
                    index = numColorLevel;
                }
            }
            if (index < 0) {
                index = 0;
            }
            int red = COLOR_LEVEL[2 * option][0] - ((COLOR_LEVEL[2 * option][0] - COLOR_LEVEL[2 * option + 1][0]) * index / numColorLevel);
            int green = COLOR_LEVEL[2 * option][1] - ((COLOR_LEVEL[2 * option][1] - COLOR_LEVEL[2 * option + 1][1]) * index / numColorLevel);
            int blue = COLOR_LEVEL[2 * option][2] - ((COLOR_LEVEL[2 * option][2] - COLOR_LEVEL[2 * option + 1][2]) * index / numColorLevel);
            tv.setBackgroundColor(Color.rgb(red, green, blue));
        }
    }

    public void resizeText(float size) {
        for (int i = 0; i < mTVs.length; i++) {
            for (int j = 0; j < mTVs[0].length; j++) {
                mTVs[i][j].setTextSize(size);
            }
        }
    }

}
