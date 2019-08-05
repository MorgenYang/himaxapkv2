package com.ln.himaxtouch.DataMonitor;

import android.app.Activity;
import android.content.Context;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.HimaxRadioButton.PresetRadioGroup;
import com.ln.himaxtouch.ObjectiveTest.ObjectiveTestController;
import com.ln.himaxtouch.R;

import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONIOTR_SENSING_ON_OFF;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONITOR_RELOAD_SETTINGS;
import static com.ln.himaxtouch.HimaxApplication.MSG_START_OSR_CC_PROCESS;
import static com.ln.himaxtouch.HimaxApplication.MSG_SWITCH_OSC_CC;
import static com.ln.himaxtouch.HimaxApplication.mObjectiveTestController;

/**
 * Created by 903622 on 2018/6/7.
 */

public class DataMonitorActivity extends Activity {

    private HimaxApplication mApplication;
    private ObjectiveTestController mController;
    private Context mContext;
    private BaseLayout mMainLayout;
    private RelativeLayout mSettingsLayout;
    private PresetRadioGroup mRawDataGroup;
    private PresetRadioGroup mTransformGroup;
    private PresetRadioGroup mDataKeepGroup;
    private PresetRadioGroup mColorOptionGroup;
    private SeekBar mColorValueBar;
    private EditText mColorValueEdit;
    private SeekBar mFontValueBar;
    private EditText mFontValueEdit;
    private PresetRadioGroup mBackgroundGroup;
    private Switch mRecordSwitch;
    private Switch mBlackSwitch;
    private Switch mDragSwitch;
    private CheckBox mCheckBP;
    private TextView mTextMaxDC;
    private Switch mAreaInfoSwitch;
    private Switch mOSRCCSwtich;


    private int mKeyboardHeight = -10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_monitor);

        if (mApplication == null) {
            mApplication = (HimaxApplication) getApplication();
        }
        if (mController == null) {
            mController = mApplication.getObjectiveTestController();
        }

        mContext = this;

        mMainLayout = (BaseLayout) findViewById(R.id.main_layout);
        mSettingsLayout = (RelativeLayout) findViewById(R.id.settings_layout);
        mRawDataGroup = (PresetRadioGroup) findViewById(R.id.rawdata_radio);
        mTransformGroup = (PresetRadioGroup) findViewById(R.id.transform_radio);
        mDataKeepGroup = (PresetRadioGroup) findViewById(R.id.keep_radio);
        mColorOptionGroup = (PresetRadioGroup) findViewById(R.id.color_radio);
        mColorValueBar = (SeekBar) findViewById(R.id.bar_colormax);
        mColorValueEdit = (EditText) findViewById(R.id.et_colormax);
        mFontValueBar = (SeekBar) findViewById(R.id.bar_font_size);
        mFontValueEdit = (EditText) findViewById(R.id.et_font_size);
        mBackgroundGroup = (PresetRadioGroup) findViewById(R.id.background_radio);
        mRecordSwitch = (Switch) findViewById(R.id.record_switch);
        mBlackSwitch = findViewById(R.id.black_switch);
        mDragSwitch = (Switch) findViewById(R.id.drag_switch);
        mTextMaxDC = (TextView) findViewById(R.id.max_dc);
        mCheckBP = (CheckBox) findViewById(R.id.check_bp);
        mAreaInfoSwitch = (Switch) findViewById(R.id.areainfo_switch);
        mOSRCCSwtich = (Switch) findViewById(R.id.osr_cc_switch);

        mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect measureRect = new Rect(); //you should cache this, onGlobalLayout can get called often
                mMainLayout.getWindowVisibleDisplayFrame(measureRect);
                // measureRect.bottom is the position above soft keypad
                int keypadHeight = mMainLayout.getRootView().getHeight() - measureRect.bottom;

                if(mKeyboardHeight == -10000) {
                    mKeyboardHeight = keypadHeight;
                }

                if(keypadHeight > mKeyboardHeight) {
                    mColorValueEdit.requestFocus();
                } else {
                    mColorValueEdit.clearFocus();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSettingsLayout.getLayoutParams();
            params.setMargins(0, 500, 0, 0);
            mSettingsLayout.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSettingsLayout.getLayoutParams();
            params.setMargins(0, 600, 0, 0);
            mSettingsLayout.setLayoutParams(params);
        }

        mController.bindDataMonitorLayout(mMainLayout, mContext);
        mController.mDataMonitorModel.mRawDataGroup = mRawDataGroup;
        mController.mDataMonitorModel.mTransformGroup = mTransformGroup;
        mController.mDataMonitorModel.mDataKeepGroup = mDataKeepGroup;
        mController.mDataMonitorModel.mColorOptionGroup = mColorOptionGroup;
        mController.mDataMonitorModel.mColorValueBar = mColorValueBar;
        mController.mDataMonitorModel.mColorValueEdit = mColorValueEdit;
        mController.mDataMonitorModel.mFontValueBar = mFontValueBar;
        mController.mDataMonitorModel.mFontValueEdit = mFontValueEdit;
        mController.mDataMonitorModel.mBackgroundGroup = mBackgroundGroup;
        mController.mDataMonitorModel.mRecordSwitch = mRecordSwitch;
        mController.mDataMonitorModel.mBlackSwitch =  mBlackSwitch;
        mController.mDataMonitorModel.mDragSwitch = mDragSwitch;
        mController.mDataMonitorModel.mCheckBP = mCheckBP;
        mController.mDataMonitorModel.mTextMaxDC = mTextMaxDC;
        mController.mDataMonitorModel.mAreaInfoSwitch = mAreaInfoSwitch;
        mController.mDataMonitorModel.mOSRCCSwtich = mOSRCCSwtich;

        Message msg = Message.obtain();
        msg.arg1 = 0;
        msg.what = MSG_DATA_MONITOR_RELOAD_SETTINGS;
        mApplication.mWorkerHandler.sendMessage(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.mDataMonitorModel.unbindViews();
        mController.mDataMonitorModel.clearTestData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if(mSettingsLayout.getVisibility() == View.INVISIBLE) {
                TransitionManager.beginDelayedTransition(mSettingsLayout);
                mSettingsLayout.setVisibility(View.VISIBLE);
            } else {
                TransitionManager.beginDelayedTransition(mSettingsLayout);
                mSettingsLayout.setVisibility(View.INVISIBLE);
            }
            return true;
        }

        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mObjectiveTestController.mDataMonitorModel.mDiagOption = 0;
            mObjectiveTestController.mDataMonitorModel.mOSR_CC = true;
            mObjectiveTestController.mDataMonitorModel.mOSRCCSwtich.setChecked(true);
            mObjectiveTestController.mDataMonitorModel.isNeedReDrawBackgound = true;
            mObjectiveTestController.mDataMonitorModel.reloadRadios();
            mApplication.mWorkerHandler.sendEmptyMessage(MSG_DATA_MONIOTR_SENSING_ON_OFF);
            Message msg = Message.obtain();
            msg.arg1 = 1;
            msg.what = MSG_DATA_MONITOR_RELOAD_SETTINGS;
            mApplication.mWorkerHandler.sendMessage(msg);
            mApplication.mMainHandler.removeMessages(MSG_START_OSR_CC_PROCESS);
            mApplication.mMainHandler.sendEmptyMessage(MSG_START_OSR_CC_PROCESS);
//            mApplication..removeMessages(MSG_START_OSR_CC_PROCESS);
//            mApplication.mWorkerHandler.sendEmptyMessage(MSG_SWITCH_OSC_CC);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
