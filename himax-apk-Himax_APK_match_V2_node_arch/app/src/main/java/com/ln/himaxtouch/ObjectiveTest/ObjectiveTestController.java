package com.ln.himaxtouch.ObjectiveTest;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.CustomView.MonitorGridView;
import com.ln.himaxtouch.CustomView.ObjectivePatternView;
import com.ln.himaxtouch.CustomView.PatternTrackResultView;
import com.ln.himaxtouch.CustomView.RawdataLayout;
import com.ln.himaxtouch.CustomView.SNRTestResultView;
import com.ln.himaxtouch.CustomView.PointRecordResultView;
import com.ln.himaxtouch.DataMonitor.BaseLayout;
import com.ln.himaxtouch.DataMonitor.DataMonitorConfig;
import com.ln.himaxtouch.DataMonitor.DataMonitorModel;
import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.HimaxRadioButton.PresetRadioGroup;
import com.ln.himaxtouch.HimaxRadioButton.PresetValueButton;
import com.ln.himaxtouch.NodeDataSource;
import com.ln.himaxtouch.PSensorTestModel;
import com.ln.himaxtouch.R;
import com.ln.himaxtouch.TouchTest.CsvEditor;
import com.ln.himaxtouch.himax_config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_SIX;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_GHOST_POINT_RECORD;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_JITTER;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_MAX_POINT_COUNT;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_PALM_TEST;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_FINISH_RECORD_RAW_DATA;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONIOTR_SENSING_ON_OFF;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONITOR_FIND_CSV_LOG;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONITOR_RELOAD_SETTINGS;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONITOR_SET_TRANSFORM;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_MONITOR_START;
import static com.ln.himaxtouch.HimaxApplication.MSG_DATA_OPEN_CSV_LOG;
import static com.ln.himaxtouch.HimaxApplication.MSG_SHOW_FOUND_MONITOR_LOGS_ALERT;
import static com.ln.himaxtouch.HimaxApplication.MSG_SNR_FINISH;
import static com.ln.himaxtouch.HimaxApplication.MSG_START_OSR_CC_PROCESS;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_DATA_MONITOR_SETTINGS_PAGE;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_DATA_MONITOR_UI;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_FILE_SAVED_RESULT;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_MONITOR_CSV_LOG_CONTENT;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_PSENSOR_DETAIL_INFO;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_SNR_PROGRESSBAR;
import static com.ln.himaxtouch.HimaxApplication.mICData;
import static com.ln.himaxtouch.HimaxApplication.mMainHandler;
import static com.ln.himaxtouch.HimaxApplication.mNodeAcc;
import static com.ln.himaxtouch.HimaxApplication.mObjectiveTestController;
import static com.ln.himaxtouch.HimaxApplication.mWorkerHandler;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_PALM_TEST;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.RAW_BASE_FRAME;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_FINISH;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_TOUCHED_NOISE;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_UNTOUCHED_NOISE;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTING_TOUCHED_NOISE;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTING_UNTOUCHED_NOISE;

/**
 * Created by 903622 on 2018/3/27.
 */

public class ObjectiveTestController {

    private static final String TAG_this = "ObjTestController";
    private static final String TAG = "[HXTP]" + TAG_this;
    private static final boolean DEBUG = false;


    private BaseExpandableListAdapter mExpandableListAdapter;
    private PopupWindow mSetingPopupWindow;

    private static Class thisclass;

    public Vibrator mVibrator;
    public SnrModel mSnrModel;
    public JitterModel mJitterModel;
    public BoardModel mBoardModel;
    public MaxPointCountModel mMaxPointCountModel;
    public GhostRecordModel mGhostRecordModel;
    public PalmModel mPalmModel;
    public PatternModel mPatternModel;
    public DataMonitorModel mDataMonitorModel;

    public static String gSaveLog;


    public PSensorTestModel mPSensorTestModel;

    public AlertDialog mDialog;

    public ObjectiveTestController() {
        mSnrModel = new SnrModel();
        gSaveLog = new String();
        thisclass = ObjectiveTestController.this.getClass();
    }

    public void onCreateMainListView(ExpandableListView view, BaseExpandableListAdapter adapter,
                                     ExpandableListView.OnGroupClickListener groupClickListener, ExpandableListView.OnChildClickListener childClickListener) {

        mExpandableListAdapter = adapter;
        view.setAdapter(adapter);
        view.setOnGroupClickListener(groupClickListener);
        view.setOnChildClickListener(childClickListener);

    }

    public void onItemClick(Context context, int groupId, int childId) {
        if (groupId <= OBJECTIVE_TEST_EXTEND_BORARD) {
            if (childId == -1) {
                Intent intent = new Intent();
                intent.putExtra("ToDoItem", groupId);
                intent.setClass(context, ObjectTestOngoingActivity.class);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("ToDoItem", groupId);
                intent.putExtra("ToDoItemChild", childId);
                intent.setClass(context, ObjectTestOngoingActivity.class);
                context.startActivity(intent);
            }

        }
    }

    public void openParameterSettingView(Context context, int groupId, int childId) {
        switch (groupId) {
            case ObjectiveListId.OBJECTIVE_TEST_SNR: {
                setupPopupSNR(context);
            }
            break;
            case ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK:
                setupPopupPattern(context, childId);
                break;
            case OBJECTIVE_TEST_JITTER_TEST:
                setupPopupJitter(context);
                break;
            case OBJECTIVE_TEST_MAX_POINT_RECORD:
                break;
            case OBJECTIVE_TEST_GHOST_POINT_RECORD:
                break;
            case OBJECTIVE_TEST_PALM_TEST:
                break;
            case OBJECTIVE_TEST_BORAD_PROTECTION: {
                setupPopupBoarder(context);
                break;
            }
            case OBJECTIVE_TEST_EXTEND_BORARD: {
                setupPopupBoarder(context);
                break;
            }
            default:
                break;

        }
    }

    private void setupPopupSNR(Context context) {
        if (mSnrModel == null) {
            mSnrModel = new SnrModel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.popup_snr_settings, null);
        final EditText et_raw_base_threshold = (EditText) view.findViewById(R.id.et_raw_base_threshold);
        final RadioGroup group_signal_type = (RadioGroup) view.findViewById(R.id.signal_data_type);
        final RadioGroup group_noise_type = (RadioGroup) view.findViewById(R.id.signal_noise_type);
        final EditText et_noise_threshold = (EditText) view.findViewById(R.id.et_noise_threshold);
        final EditText et_signal_timeout = (EditText) view.findViewById(R.id.et_signal_timeout);
        final EditText et_calculate_frames = (EditText) view.findViewById(R.id.et_calculate_frames);
        final EditText et_ignore_framees = (EditText) view.findViewById(R.id.et_ignore_frames);
        Button btn_save = (Button) view.findViewById(R.id.btn_save);
        Button btn_exit = (Button) view.findViewById(R.id.btn_exit);

        ((RadioButton) group_signal_type.getChildAt(mSnrModel.mSignalType)).setChecked(true);
        ((RadioButton) group_noise_type.getChildAt(mSnrModel.mNoiseType)).setChecked(true);
        et_raw_base_threshold.setText(mSnrModel.mRawBaseThreshold + "");
        et_noise_threshold.setText(mSnrModel.mNoiseThreshold + "");
        et_signal_timeout.setText(mSnrModel.mSignalTimeout + "");
        et_calculate_frames.setText(mSnrModel.mCalculateFrames + "");
        et_ignore_framees.setText(mSnrModel.mIgnoreFrames + "");

        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnrModel.mRawBaseThreshold = Integer.valueOf(et_raw_base_threshold.getText().toString());
                mSnrModel.mSignalType = group_signal_type.indexOfChild(group_signal_type.findViewById(group_signal_type.getCheckedRadioButtonId()));
                mSnrModel.mNoiseType = group_noise_type.indexOfChild(group_noise_type.findViewById(group_noise_type.getCheckedRadioButtonId()));
                mSnrModel.mNoiseThreshold = Integer.valueOf(et_noise_threshold.getText().toString());
                mSnrModel.mSignalTimeout = Integer.valueOf(et_signal_timeout.getText().toString());
                mSnrModel.mCalculateFrames = Integer.valueOf(et_calculate_frames.getText().toString());
                mSnrModel.mIgnoreFrames = Integer.valueOf(et_ignore_framees.getText().toString());
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        btn_exit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        mSetingPopupWindow = new PopupWindow(context);
        mSetingPopupWindow.setContentView(view);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_objectivetest, null);
        updatePopupWindow(rootView, mSetingPopupWindow);
    }

    private void setupPopupBoarder(Context context) {
        if (mBoardModel == null) {
            mBoardModel = new BoardModel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.popup_board_settings, null);
        final EditText et_space = (EditText) view.findViewById(R.id.et_space);
        Button btn_save = (Button) view.findViewById(R.id.btn_save);
        Button btn_exit = (Button) view.findViewById(R.id.btn_exit);

        et_space.setText(mBoardModel.mSpaceToBoarder + "");

        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoardModel.mSpaceToBoarder = Float.valueOf(et_space.getText().toString());
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        btn_exit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        mSetingPopupWindow = new PopupWindow(context);
        mSetingPopupWindow.setContentView(view);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_objectivetest, null);
        updatePopupWindow(rootView, mSetingPopupWindow);
    }

    private void setupPopupJitter(Context context) {
        if (mJitterModel == null) {
            mJitterModel = new JitterModel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.popup_jitter_settings, null);
        final EditText et_tolerance = (EditText) view.findViewById(R.id.et_tolerance);
        Button btn_save = (Button) view.findViewById(R.id.btn_save);
        Button btn_exit = (Button) view.findViewById(R.id.btn_exit);

        et_tolerance.setText(mJitterModel.mTolerance + "");

        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJitterModel.mTolerance = Float.valueOf(et_tolerance.getText().toString());
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        btn_exit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        mSetingPopupWindow = new PopupWindow(context);
        mSetingPopupWindow.setContentView(view);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_objectivetest, null);
        updatePopupWindow(rootView, mSetingPopupWindow);
    }

    private void setupPopupPattern(Context context, int type) {
        if (mPatternModel == null) {
            mPatternModel = new PatternModel();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.popup_pattern_settings, null);
        final EditText et_cell_resolution = (EditText) view.findViewById(R.id.et_cell_resolution);
        final EditText et_line_width = (EditText) view.findViewById(R.id.et_line_width);
        final EditText et_vertical_line_num = (EditText) view.findViewById(R.id.et_vertical_line_num);
        final EditText et_horizontal_line_num = (EditText) view.findViewById(R.id.et_horizontal_line_num);
        final EditText et_line_space = (EditText) view.findViewById(R.id.et_line_space);

        if (type != 5) {
            et_line_width.setVisibility(View.GONE);
            et_vertical_line_num.setVisibility(View.GONE);
            et_horizontal_line_num.setVisibility(View.GONE);
            et_line_space.setVisibility(View.GONE);
        }

        Button btn_save = (Button) view.findViewById(R.id.btn_save);
        Button btn_exit = (Button) view.findViewById(R.id.btn_exit);

        et_cell_resolution.setText(mPatternModel.mResolution + "");
        et_line_width.setText(mPatternModel.mLineWidth + "");
        et_vertical_line_num.setText(mPatternModel.mVerticalLineNum + "");
        et_horizontal_line_num.setText(mPatternModel.mHorizontalLineNum + "");
        et_line_space.setText(mPatternModel.mLineSpace + "");

        btn_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPatternModel.mResolution = Integer.valueOf(et_cell_resolution.getText().toString());
                mPatternModel.mLineWidth = Integer.valueOf(et_line_width.getText().toString());
                mPatternModel.mVerticalLineNum = Integer.valueOf(et_vertical_line_num.getText().toString());
                mPatternModel.mHorizontalLineNum = Integer.valueOf(et_horizontal_line_num.getText().toString());
                mPatternModel.mLineSpace = Integer.valueOf(et_line_space.getText().toString());
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        btn_exit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetingPopupWindow.dismiss();
                mSetingPopupWindow = null;
            }
        });
        mSetingPopupWindow = new PopupWindow(context);
        mSetingPopupWindow.setContentView(view);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_objectivetest, null);
        updatePopupWindow(rootView, mSetingPopupWindow);
    }

    private void updatePopupWindow(View rootView, PopupWindow popup) {
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setWindowLayoutMode(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popup.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        popup.setFocusable(true);
        popup.update();
    }

    public void collectUnTouchedNoise(HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource) {
        if (mSnrModel != null) {
            mSnrModel.clearTestData();
        }
        if (mICData.val_icid == 0) {
            Log.d(TAG, "Now is collectUnTouchedNoise");
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }

        //get Max DC value;
        try {
            mSnrModel.mMaxDCForBP = dataSource.readMaxDC(mICData.dc_max);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        //step1. catch frame to calculate raw data base.
        int[] channels = new int[2];
        dataSource.getRawDataRowAndColumn(channels, false);
        mSnrModel.mRowNum = channels[0];
        mSnrModel.mColNum = channels[1];

        //step2. catch frame to calculate noise
        mSnrModel.mResult_Noise = catchNoiseForSNR(dataSource, channels[0], channels[1], mainHandler);

        //step3. switch to event stack from sram
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        boolean isSwitched = false;
//        while (!isSwitched) {
//            int[] dummy = new int[2];
//            dataSource.getRawDataRowAndColumn(dummy, false);
//            isSwitched = dataSource.sensingOffAndOn();
//        }

        mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
    }

    public void collectSNRFinish(HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource) {
        if (mSnrModel.mRawBaseData == null || mSnrModel.mColNum == 0 || mSnrModel.mRowNum == 0) {
            //should dismiss and restart process.
            return;
        }

        mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
    }

    public void collectTouchedNoise(HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource) {

        if (mSnrModel.mRawBaseData == null || mSnrModel.mColNum == 0 || mSnrModel.mRowNum == 0) {
            //should dismiss and restart process.
            return;
        }

        mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);

        //step1. catch frame to calculate signal
        mSnrModel.mResult_Signal = catchSignalForSNR(dataSource, mSnrModel.mRowNum, mSnrModel.mColNum,
                mSnrModel.mRawBaseData, mainHandler);

        //step2. calculateSNR

        mSnrModel.mResult_SNR = calculateSNR(mSnrModel.mResult_Noise, mSnrModel.mResult_Signal);
        Log.d(TAG, String.format("Now is mResult_Noise=%f, mResult_Signal=%f, mResult_SNR=%f", mSnrModel.mResult_Noise, mSnrModel.mResult_Signal, mSnrModel.mResult_SNR));

        if (mSnrModel.mResult_SNR_MAX < mSnrModel.mResult_SNR) {
            mSnrModel.mResult_SNR_MAX = mSnrModel.mResult_SNR;
        }

        //step3. switch to event stack from sram
//        190712 marked by Nim
//        boolean isSwitched = false;
//        while (!isSwitched) {
//            int[] dummy = new int[2];
//            dataSource.getRawDataRowAndColumn(dummy, false);
//            isSwitched = dataSource.sensingOffAndOn();
//        }

        mSnrModel.mCollectedSignalFrames = (mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames);
        mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
    }

    public void updateSNRProgressBar() {
        if (mSnrModel.mPB_Item1 == null
                || mSnrModel.mPB_Item1_skip == null
                || mSnrModel.mPB_Item2 == null
                || mSnrModel.mPB_Item2_skip == null) {
            return;
        }

        if (mSnrModel.mCollectedRawDataFrames < RAW_BASE_FRAME) {
            mSnrModel.mTitle.setText("Base Rawdata");
            mSnrModel.mMsg.setText("Getting Base Rawdata");
            int progress = mSnrModel.mCollectedRawDataFrames * 100 / RAW_BASE_FRAME;
            mSnrModel.mPB_BaseRaw.setProgress((progress < 0 ? 0 : progress));
            return;
        } else {
            mSnrModel.mPB_BaseRaw.setProgress(100);
        }

        if (mSnrModel.mCollectingState == STATE_COLLECTING_UNTOUCHED_NOISE) {
            mSnrModel.mTitle.setText(R.string.snr_untouched);
//            int progress1 = (mSnrModel.mCollectedRawDataFrames + mSnrModel.mCollectedNoiseFrames) * 100 / (mSnrModel.mCalculateFrames + RAW_BASE_FRAME);
//            int progress1_skip = (mSnrModel.mCollectedRawDataFrames + mSnrModel.mCollectedNoiseFrames) * 100 / (mSnrModel.mCalculateFrames + RAW_BASE_FRAME);
            int progress1 = (mSnrModel.mCollectedNoiseFrames - mSnrModel.mIgnoreFrames) * 100 / (mSnrModel.mCalculateFrames);
            int progress1_skip = mSnrModel.mCollectedNoiseFrames * 100 / (mSnrModel.mIgnoreFrames);
            mSnrModel.mPB_Item1.setProgress((progress1 < 0 ? 0 : progress1));
            mSnrModel.mPB_Item1_skip.setProgress((progress1_skip < 0 ? 0 : progress1_skip));
            if (mSnrModel.mCollectedNoiseFrames > mSnrModel.mIgnoreFrames && (mSnrModel.mCollectedNoiseFrames - mSnrModel.mIgnoreFrames < mSnrModel.mCalculateFrames)) {
                mSnrModel.mMsg.setText(R.string.snr_collecting_noise);
                mSnrModel.mMsg.setTextColor(Color.GRAY);
            } else if (mSnrModel.mCollectedNoiseFrames <= mSnrModel.mIgnoreFrames) {
                mSnrModel.mMsg.setTextColor(Color.WHITE);
                mSnrModel.mMsg.setText("Collecting Noise rawdata and skipt it");
            } else if (mSnrModel.mCollectedNoiseFrames >= (mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames)) {
                mSnrModel.mCollectingState = STATE_COLLECTED_UNTOUCHED_NOISE;
                mSnrModel.mMsg.setTextColor(Color.RED);
                mSnrModel.mMsg.setText("Now please put your finger on Panel");
            } else {
                mSnrModel.mMsg.setText(R.string.snr_collecting_noise);
            }
            return;
        }
        if (mSnrModel.mCollectingState == STATE_COLLECTING_TOUCHED_NOISE) {
            mSnrModel.mTitle.setText(R.string.snr_touched);
            int progress2 = (mSnrModel.mCollectedSignalFrames - mSnrModel.mIgnoreFrames) * 100 / (mSnrModel.mCalculateFrames);
            int progress2_skip = mSnrModel.mCollectedSignalFrames * 100 / (mSnrModel.mIgnoreFrames);
            mSnrModel.mPB_Item2.setProgress(progress2 < 0 ? 0 : progress2);
            mSnrModel.mPB_Item2_skip.setProgress(progress2_skip < 0 ? 0 : progress2_skip);
            if (mSnrModel.mCollectedSignalFrames > mSnrModel.mIgnoreFrames && (mSnrModel.mCollectedSignalFrames - mSnrModel.mIgnoreFrames < mSnrModel.mCalculateFrames)) {
                mSnrModel.mMsg.setTextColor(Color.GRAY);
                mSnrModel.mMsg.setText(R.string.snr_please_touch);

            } else if (mSnrModel.mCollectedSignalFrames <= mSnrModel.mIgnoreFrames) {
                mSnrModel.mMsg.setTextColor(Color.WHITE);
                mSnrModel.mMsg.setText("Collecting Signal rawdata and skipt it");
            } else if (mSnrModel.mCollectedSignalFrames >= (mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames)) {
                mSnrModel.mCollectingState = STATE_COLLECTED_TOUCHED_NOISE;
                mSnrModel.mMsg.setTextColor(Color.GREEN);
                mSnrModel.mMsg.setText("Collected OK, Please let finger leave panel!"); //R.string.snr_finish_calculation
//                RelativeLayout r_layout = (RelativeLayout) mSnrModel.mPopup.getContentView().findViewById(R.id.body);
//                r_layout.removeAllViews();
//                View circle = new SNRTestResultView(mSnrModel.mPopup.getContentView().getContext(), mSnrModel);
//                r_layout.addView(circle);
                mSnrModel.mCollectingState = STATE_COLLECTED_FINISH;
                mWorkerHandler.sendEmptyMessage(MSG_SNR_FINISH);
            } else {
                mSnrModel.mMsg.setText(R.string.snr_collecting_signal);
            }
            return;
        }
        if (mSnrModel.mCollectingState == STATE_COLLECTED_FINISH) {
            RelativeLayout r_layout = (RelativeLayout) mSnrModel.mPopup.getContentView().findViewById(R.id.body);
            r_layout.removeAllViews();
            View circle = new SNRTestResultView(mSnrModel.mPopup.getContentView().getContext(), mSnrModel);
            r_layout.addView(circle);
            return;
        }
    }

    public boolean isAllowBack() {
        if (mSnrModel.mCollectingState == STATE_COLLECTING_UNTOUCHED_NOISE ||
                mSnrModel.mCollectingState == STATE_COLLECTING_TOUCHED_NOISE) {
            return false;
        } else {
            return true;
        }
    }

    public void catchBaseRawData(NodeDataSource dataSource, int row, int col, int[][] baseData,
                                 HimaxApplication.MainHandler mainHandler) {
        int[][] frame = new int[row][col];
        int[][] frame_all = new int[row][col];
        int count_frame = 0;

        boolean passDiffTest = false;
        if (mICData.val_icid == 0) {
            Log.d(TAG, "Now is catchBaseRawData");
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }


        for (int i = 0; i < RAW_BASE_FRAME + 1; i++) {

            dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, (i == 0), false);

            if (!passDiffTest) {
                /* confirm the data is correct, so check it is bigger than standard value or not*/
                while (!passDiffTest) {
                    passDiffTest = isEachDiffCorrect(frame, mSnrModel.mRawBaseThreshold);
                    if (!passDiffTest) {
                        dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, false, false);
                    }
                }

            } else {
                if (DEBUG) {
                    Log.d(TAG, logEntireFrame(frame, "SNR_CatchRawFrame" + i));
                }
                count_frame++;
                frame_all = addAllFrameforAVG(frame_all, frame, null, count_frame, (i == RAW_BASE_FRAME));
                Log.e("HXTP_NIM", String.format("The count frame number = %d", count_frame));
                mSnrModel.mCSV.appendRawData(2, mSnrModel.mCollectedRawDataFrames, frame);

                mSnrModel.mCollectedRawDataFrames += 1;
                mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
            }
        }
        addArrays(baseData, frame_all);
        //getAvgArrays(baseData, count_frame);
        mSnrModel.mCSV.appendRawData(3, mSnrModel.mCollectedRawDataFrames, baseData);
    }

    private void addArrays(int[][] sigma, int[][] frame) {
        for (int i = 0; i < sigma.length; i++) {
            for (int j = 0; j < sigma[0].length; j++) {
                sigma[i][j] += frame[i][j];
            }
        }
    }

    private void getAvgArrays(int[][] sigma, int frame_num) {
        for (int i = 0; i < sigma.length; i++) {
            for (int j = 0; j < sigma[0].length; j++) {
                int temp = sigma[i][j] / frame_num;
                sigma[i][j] = temp;
            }
        }
    }


    public double catchNoiseForSNR(NodeDataSource dataSource, int row, int col, HimaxApplication.MainHandler mainHandler) {

        double noiseValue = 0;
        int[][] sigma = new int[row][col];

        int[][] rawData = new int[row][col];
        int skip_num = 0;

        if (mICData.val_icid == 0) {
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }

        /* keep active*/
        Log.d("HXTP", "keep active");
        dataSource.simpleWriteCMD(mICData.tcon_dummy_for_idle_cmd);
        dataSource.readRegister(String.format("%08x", mICData.adr_tcon_dummy));

        /* reject hopping*/
        Log.d("HXTP", "reject hopping");
        dataSource.simpleWriteCMD(mICData.exe_force_stop_hopping);


        for (int i_raw = 0; i_raw < rawData.length; i_raw++) {
            for (int j_raw = 0; j_raw < rawData[0].length; j_raw++) {
                rawData[i_raw][j_raw] = 0;
            }
        }
        catchBaseRawData(dataSource, row, col, rawData, mainHandler);
        mSnrModel.mRawBaseData = rawData;
        Log.e(TAG, logEntireFrame(rawData, "SNR_RawFrame"));

        if (DEBUG) {
            Log.d(TAG, logEntireFrame(rawData, "SNR_RawFrame"));
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        //catch SNR noise
        switch (mSnrModel.mNoiseType) {
            case 0: {
                //Max
                for (int i = 0; i < mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames; i++) {
                    int[][] frame = new int[row][col];
                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, false, false);

                    if (DEBUG) {
                        Log.d(TAG, logEntireFrame(frame, "SNR_CatchNoiseframe" + i));
                    }

                    if (!isEachDiffCorrect(frame, mSnrModel.mRawBaseThreshold) && (skip_num < mSnrModel.mSignalTimeout)) {
                        /* confirm the data is correct, so check it is bigger than standard value or not*/
                        skip_num++;
                        i--;
                        Log.e(TAG, String.format("Noise_Max Now idx=%d, skip_num=%d", i, skip_num));
                    } else {
                        if (skip_num >= mSnrModel.mSignalTimeout) {
                            Log.d(TAG, String.format("Now skip_num=%d", skip_num));
                        }
                        if (i >= mSnrModel.mIgnoreFrames) {
                            subRawDataAndSquare(frame, rawData);
                            addArrays(sigma, frame);
                            //Log.e(TAG, logEntireFrame(sigma, "\t Noise Sigma:"));
                        }
                        mSnrModel.mCollectedNoiseFrames += 1;
                        if (i < mSnrModel.mIgnoreFrames) {
                            mSnrModel.mCSV.appendRawData(7, i, frame);

                        } else {
                            mSnrModel.mCSV.appendRawData(0, i, frame);
                            mSnrModel.mCSV.appendRawData(4, i, frame);
                        }

                        if (mSnrModel.mCollectedNoiseFrames != mSnrModel.mCalculateFrames)
                            mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                    }

                }

                double[][] result = new double[row][col];
                divideRawAmountAndSqrt(sigma, result);
                noiseValue = findMax(result);
                mSnrModel.mCSV.appendRawData(5, 0, "Max Noise Value:" + noiseValue + "", 10000, false);
                if (DEBUG) {
                    Log.d(TAG, "SNR_NoiseValue: " + noiseValue);
                }

                break;
            }
            case 1: {
                //Average
                for (int i = 0; i < mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames; i++) {
                    int[][] frame = new int[row][col];
                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, false, false);

                    if (!isEachDiffCorrect(frame, mSnrModel.mRawBaseThreshold)) {
                        /* confirm the data is correct, so check it is bigger than standard value or not*/
                        skip_num++;
                        i--;
                        Log.d(TAG, String.format("Noise_average Now idx=%d, skip_num=%d", i, skip_num));
                    } else {
                        if (i >= mSnrModel.mIgnoreFrames) {
                            subRawDataAndSquare(frame, rawData);
                            //sigma = frame;
                            addArrays(sigma, frame);
                        }
                        mSnrModel.mCollectedNoiseFrames += 1;
                        if (i < mSnrModel.mIgnoreFrames) {
                            mSnrModel.mCSV.appendRawData(7, i, frame);

                        } else {
                            mSnrModel.mCSV.appendRawData(0, i, frame);
                            mSnrModel.mCSV.appendRawData(4, i, frame);
                        }
                        if (mSnrModel.mCollectedNoiseFrames != mSnrModel.mCalculateFrames)
                            mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                    }

                }
                double[][] result = new double[row][col];
                divideRawAmountAndSqrt(sigma, result);
                noiseValue = getLinearAvg(result);
                mSnrModel.mCSV.appendRawData(5, 0, "Average Noise Value:" + noiseValue + "", 10000, false);
                break;
            }
            case 2: {
                //Max-Touch
                for (int i = 0; i < mSnrModel.mCalculateFrames; i++) {
                    int[][] frame = new int[row][col];
                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, false, false);

                    if (!isEachDiffCorrect(frame, mSnrModel.mRawBaseThreshold)) {
                        i--;
                    } else {
                        subRawDataAndSquare(frame, rawData);
                        addArrays(sigma, frame);
                        mSnrModel.mCollectedNoiseFrames += 1;
                        if (mSnrModel.mCollectedNoiseFrames != mSnrModel.mCalculateFrames)
                            mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                    }

                }

                double[][] result = new double[row][col];
                divideRawAmountAndSqrt(sigma, result);
                noiseValue = findMax(result);
                break;
            }
            case 3: {
                //Avg-Touch
                for (int i = 0; i < mSnrModel.mCalculateFrames; i++) {
                    int[][] frame = new int[row][col];
                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, false, false);

                    if (!isEachDiffCorrect(frame, mSnrModel.mRawBaseThreshold)) {
                        i--;
                    } else {
                        subRawDataAndSquare(frame, rawData);
                        addArrays(sigma, frame);
                        mSnrModel.mCollectedNoiseFrames += 1;
                        if (mSnrModel.mCollectedNoiseFrames != mSnrModel.mCalculateFrames)
                            mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                    }

                }
                double[][] result = new double[row][col];
                divideRawAmountAndSqrt(sigma, result);
                noiseValue = getLinearAvg(result);
                break;
            }
            default:
                break;
        }
        /* diag return back*/
        dataSource.simpleWriteCMD(mICData.back2normal_CMD);

        return noiseValue;
    }

    private void compensateSignal(int[][] input) {
        double v = 0.125;
        int center = input.length / 2;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                if (i < center) {
                    double result = (double) input[i][j] * ((v / center) * (i + 1) + 1);
                    input[i][j] = (int) result;
                } else {
                    double result = (double) input[i][j] * ((v / center) * (input.length - i + 1) + 1);
                    input[i][j] = (int) result;
                }
            }
        }
    }

    private double catchSignalForSNR(NodeDataSource dataSource, int row, int col, int[][] rawData, HimaxApplication.MainHandler mainHandler) {
        double signalValue = 0;
        int[][] tmp = new int[row][col];
        boolean isRerun = true;

        if (mICData.val_icid == 0) {
            Log.d(TAG, "Now is catchSignalForSNR");
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }

        /* keep active*/
        if (true)
            dataSource.simpleWriteCMD(mICData.tcon_dummy_for_idle_cmd);

        /* reject hopping*/
        dataSource.simpleWriteCMD(mICData.exe_force_stop_hopping);

        if (mICData.DIAG_DC_SRAM < 10) {
            isRerun = false;
            Log.d(TAG, "It is the event stack need to collecting data");
            dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, tmp, true, false);
            dataSource.msDelay(1000);
        }


        //catch SNR signal
        switch (mSnrModel.mSignalType) {
            case 0: {
                //block
                double maxSigma = 0.0;
                int[] record = new int[2];
                int[][] frame = new int[row][col];
                int[][] sum_frame = new int[row][col];
                for (int i_shift = 0; i_shift < (mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames); i_shift++) {


                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, ((i_shift <= 0) && isRerun), false);

                    int i = i_shift - mSnrModel.mIgnoreFrames;
                    int xxx = findMaxOverThreshold(frame, record, mSnrModel.mNoiseThreshold, rawData);

                    if (!isEachDiffCorrectWithOther(frame, rawData, mSnrModel.mNoiseThreshold)) {
                        /* confirm the data is correct, so check it is bigger than standard value or not*/
                        i_shift--;
                        Log.d(TAG, String.format("signal_block Now idx=%d", i_shift));
                    } else {

                        if (i_shift >= mSnrModel.mIgnoreFrames) {

                            sum_frame = addAllFrameforAVG(sum_frame, frame, rawData, (mSnrModel.mCollectedSignalFrames + 1), ((i_shift - mSnrModel.mIgnoreFrames + 1) == mSnrModel.mCalculateFrames));
                        }
                    }

                    mSnrModel.mCollectedSignalFrames += 1;
                    if (i_shift < mSnrModel.mIgnoreFrames) {
                        mSnrModel.mCSV.appendRawData(7, i_shift, frame);
                    } else {
                        mSnrModel.mCSV.appendRawData(1, i_shift, frame);
                        mSnrModel.mCSV.appendRawData(6, i_shift, sum_frame);
                    }

                    if (mSnrModel.mCollectedSignalFrames != (mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames))
                        mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                }

                maxSigma = findMax(sum_frame);
                Log.d("HXTP_NIM", String.format("mCalculateFrames=%d, mIgnoreFrames=%d, " +
                        "mCollectedSignalFrames=%d, maxSigma=%f, signalValue=%f", mSnrModel.mCalculateFrames, mSnrModel.mIgnoreFrames, mSnrModel.mCollectedSignalFrames, maxSigma, signalValue));
                signalValue = maxSigma; // Math.sqrt(( maxSigma) / ((double) mSnrModel.mCalculateFrames));
                mSnrModel.mCSV.appendString(String.format("Now Signal value in block is %.02f", signalValue));
                break;
            }
            case 1: {
                //frame
                int[][] frame = new int[row][col];
                double avgSigma = 0;
                for (int i = 0; i < mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames; i++) {
                    dataSource.readSpecificDiag(mICData.DIAG_DC_SRAM, frame, ((i <= 0) && isRerun), false);
                    //double temp = getAvgFromOvrThreshold(frame, rawData, mSnrModel.mNoiseThreshold);
                    if (!isEachDiffCorrectWithOther(frame, rawData, mSnrModel.mNoiseThreshold)) {
                        /* confirm the data is correct, so check it is bigger than standard value or not*/
                        i--;
                        Log.e(TAG, String.format("signal_frame Now idx=%d", i));
                    } else {
                        if (i >= mSnrModel.mIgnoreFrames) {
                            /* find the maximum in one frame event minus the base data*/
                            Log.e(TAG, "Now SNR_signal  idex=" + (i - mSnrModel.mIgnoreFrames));
                            avgSigma += findMax(frame, rawData);
                        }
                        mSnrModel.mCollectedSignalFrames += 1;

                        if (i < mSnrModel.mIgnoreFrames) {
                            mSnrModel.mCSV.appendRawData(7, i, frame);
                        } else {
                            mSnrModel.mCSV.appendRawData(1, i, frame);
                        }
                        if (mSnrModel.mCollectedSignalFrames <= mSnrModel.mCalculateFrames + mSnrModel.mIgnoreFrames)
                            mainHandler.sendEmptyMessage(MSG_UPDATE_SNR_PROGRESSBAR);
                    }
                }
                signalValue = avgSigma / mSnrModel.mCalculateFrames;
                Log.d("HXTP_NIM", String.format("mCalculateFrames=%d, mIgnoreFrames=%d, " +
                        "mCollectedSignalFrames=%d, avgSigma=%f, signalValue=%f", mSnrModel.mCalculateFrames, mSnrModel.mIgnoreFrames, mSnrModel.mCollectedSignalFrames, avgSigma, signalValue));
                mSnrModel.mCSV.appendString(String.format("Now Signal value in frame is %.02f", signalValue));
                break;
            }
            default:
                break;
        }

        /*Diag return back*/
        dataSource.simpleWriteCMD(mICData.back2normal_CMD);
        /* Allow to enter Idle mode*/
        dataSource.simpleWriteCMD(mICData.tcon_dummy_for_normal_cmd);
        /* Allow to hopping*/
        dataSource.simpleWriteCMD(mICData.exe_force_start_hopping);

        return signalValue;
    }

    private int findMaxOverThreshold(int[][] input, int[] position, int threshold, int[][] raw) {
        int max = 0;
        int maxSignalForBP = mSnrModel.mMaxSignalForBP;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {

                if (maxSignalForBP < input[i][j]) {
                    maxSignalForBP = input[i][j];
                }

                int temp = input[i][j] - raw[i][j];
                if (temp > threshold && temp > max) {
                    max = temp;
                    position[0] = i;
                    position[1] = j;
                }
            }
        }
        mSnrModel.mMaxSignalForBP = maxSignalForBP;
        return max;
    }


    private double findMax(int[][] input) {
        double max = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                if (input[i][j] > max) {
                    max = input[i][j];
                }
            }
        }
        return max;
    }

    private double findMax(int[][] input, int[][] base) {
        double max = 0;
        int temp = 0;
        int maxSignalForBP = mSnrModel.mMaxSignalForBP;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {

                if (maxSignalForBP < input[i][j]) {
                    maxSignalForBP = input[i][j];
                }
                if (mICData.isOncell)
                    temp = Math.abs(input[i][j] - base[i][j]);
                else
                    temp = input[i][j] - base[i][j];
                if (temp > max) {
                    max = temp;
                }
            }
        }
        /* debug hopping affect or not */
        if (false) { /* for hx83102d (SEC A10)*/
            Log.e(TAG, "\tSNR_signal Now max=" + max);
            if (max < 500) {
                Log.e(TAG, logEntireFrame(input, "\tSNR_signal raw="));
            }
            Log.e(TAG, "SNR_siganl hopping: " + mNodeAcc.readRegister("80020038", false));
            Log.e(TAG, "SNR_siganl hopping check: " + mNodeAcc.readRegister("10007088", false));
        }
        mSnrModel.mMaxSignalForBP = maxSignalForBP;
        return max;
    }

    private double findMax(double[][] input) {
        double max = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                if (input[i][j] > max) {
                    max = input[i][j];
                }
            }
        }
        return max;
    }

    private double getLinearAvg_org(double[][] input) {
        double sigma = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                sigma += input[i][j] * input[i][j];
            }
        }
        double temp = sigma / (double) (input.length * input[0].length);
        sigma = temp;
        return Math.sqrt(sigma);
    }

    private double getLinearAvg(double[][] input) {
        double sigma = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                sigma += input[i][j];
            }
        }
        double temp = sigma / (double) (input.length * input[0].length);
        sigma = temp;
        return sigma;
    }

    private double calculateSNR(double noise, double signal) {
        return 20 * Math.log10(signal / noise);
    }

    private void subRawDataAndSquare(int[][] input, int[][] rawData) {
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                if (mICData.isOncell)
                    input[i][j] = Math.abs(input[i][j] - rawData[i][j]);
                else
                    input[i][j] -= rawData[i][j];
                int temp = input[i][j] * input[i][j];
                input[i][j] += temp;
            }
        }
    }

    private void divideRawAmountAndSqrt(int[][] sigma, double[][] output) {
        for (int i = 0; i < sigma.length; i++) {
            for (int j = 0; j < sigma[i].length; j++) {
                double temp = ((double) sigma[i][j]) / ((double) mSnrModel.mCalculateFrames);
                output[i][j] = Math.sqrt(temp);
            }
        }
    }

    private double getAvgFromOvrThreshold(int[][] input, int[][] raw, int threshold) {
        int avg = 0;
        int num = 0;
        int maxSignalForBP = mSnrModel.mMaxSignalForBP;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {

                if (maxSignalForBP < input[i][j]) {
                    maxSignalForBP = input[i][j];
                }

                int temp = (input[i][j] - raw[i][j]);
                if (temp > threshold) {
                    avg += temp;
                    num++;
                }
            }
        }
        mSnrModel.mMaxSignalForBP = maxSignalForBP;
        return (num == 0) ? 0 : ((double) avg / (double) num);
    }

    private int[][] findMaxAllFrame(int[][] now_value, int[][] new_input) {
        for (int i = 0; i < now_value.length; i++) {
            for (int j = 0; j < now_value[i].length; j++) {
                if (now_value[i][j] < new_input[i][j])
                    now_value[i][j] = new_input[i][j];
            }
        }
        return now_value;
    }

    private int[][] findMaxDiffAllFrame(int[][] now_value, int[][] new_input, int[][] base) {
        int tmp = 0;
        for (int i = 0; i < now_value.length; i++) {
            for (int j = 0; j < now_value[i].length; j++) {
                tmp = new_input[i][j] - base[i][j];
                if (now_value[i][j] < tmp)
                    now_value[i][j] = tmp;
            }
        }
        return now_value;
    }

    private int[][] addAllFrameforAVG(int[][] now_value, int[][] new_input, int[][] base, int frame_num, boolean end2AvgFrame) {
        if (end2AvgFrame) {
            Log.d("HXTP_NIM", logEntireFrame(now_value, "Before cal avg"));
        }
        if (base != null) {
            Log.d("HXTP_NIM", "It will cut base");
        }
        for (int i = 0; i < now_value.length; i++) {
            for (int j = 0; j < now_value[i].length; j++) {
                if (base != null) {
                    if (mICData.isOncell)
                        now_value[i][j] += Math.abs(new_input[i][j] - base[i][j]);
                    else
                        now_value[i][j] += (new_input[i][j] - base[i][j]);
                } else {
                    now_value[i][j] += new_input[i][j];
                }
                if (end2AvgFrame) {
                    Log.d("HXTP_NIM", String.format("The end of frame number = %d", frame_num));
                    now_value[i][j] = now_value[i][j] / frame_num;
                }
            }
        }
        return now_value;
    }

    private double getSumFrom1Frame(int[][] input) {
        int sum = 0;
        int num = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                sum += (input[i][j]);

            }
        }
        return (double) sum;
    }

    private double getAvgFromAvgData(int[][] input, int[][] raw, int threshold) {
        int avg = 0;
        int num = 0;
        int maxSignalForBP = mSnrModel.mMaxSignalForBP;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {

                if (maxSignalForBP < input[i][j]) {
                    maxSignalForBP = input[i][j];
                }

                avg += (input[i][j] - raw[i][j]);

            }
        }
        return (double) avg;
    }

    private String logEntireFrame(int[][] input, String title) {
        StringBuilder log = new StringBuilder();
        log.append(title + "\n");
        for (int xx = 0; xx < input.length; xx++) {
            for (int xxx = 0; xxx < input[0].length; xxx++) {
                if (xxx == 0) {
                    log.append(input[xx][xxx]);
                } else {
                    log.append("," + input[xx][xxx]);
                }

            }
            log.append("\n");
        }
        return log.toString();
    }

    private boolean isEachDiffCorrect(int[][] input, int threshold) {


        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                //if(j+1 != input[0].length) {
                if (input[i][j] < threshold) {
                    //Log.d("HXTP0712", "small than threshold");
                    return false;
                }
                //}
            }
        }
        return true;
    }

    private boolean isEachDiffCorrectWithOther(int[][] input, int[][] other, int threshold) {

        int OverThresholdNum = 0;
        int value = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                if (j + 1 != input[0].length) {
                    if (mICData.isOncell)
                        value = Math.abs(input[i][j] - other[i][j]);
                    else
                        value = input[i][j] - other[i][j];
                    if (value > threshold) {
                        Log.e(TAG, String.format("Now threshold=%d, diff_value=%d", threshold, value));
                        OverThresholdNum++;
                    }
                }
            }
        }
        if (OverThresholdNum > 0)
            return true;
        else
            return false;
    }

    public void onPauseAtObjectTestOnGoingActivity(IObjectiveTestModel testModel) {
        testModel.unbindViews();
        testModel.clearTestData();

    }

    public void createJitterLayout(RelativeLayout layout, Context context) {
        if (mJitterModel == null) {
            mJitterModel = new JitterModel();
        }

        mJitterModel.mStartTime = Calendar.getInstance().getTimeInMillis();
        mJitterModel.mLayout = layout;
        mJitterModel.mPathToSave = getFilePath(context, "jitter_test",
                mJitterModel.mStartTime);

        View patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(),
                layout.getHeight(), PATTERN_JITTER);
        mJitterModel.mBackgroundViewId = patternView.generateViewId();
        patternView.setId(mJitterModel.mBackgroundViewId);
        mJitterModel.mLayout.addView(patternView);

        PointRecordResultView trackView = new PointRecordResultView(context, mJitterModel.mPathToSave,
                context.getResources().getString(R.string.objective_csv_msg_wait_touch),
                context.getResources().getString(R.string.objective_csv_unsaved_msg));
        mJitterModel.mProgressView = trackView;
        mJitterModel.mLayout.addView(trackView);
    }

    public void startJitterTest(float[] p) {
        //calculate deviation and write to excel
        if (!mJitterModel.mResult) {
            return;
        }

        if (mJitterModel.mRecordPoints.size() > 0) {
            float[] last = mJitterModel.mRecordPoints.get(mJitterModel.mRecordPoints.size() - 1);
            float w = last[0] - p[0];
            float l = last[1] - p[1];
            double d = Math.sqrt(w * w + l * l);
            if (d > mJitterModel.mTolerance) {
                mJitterModel.mResult = false;
                DecimalFormat df = new DecimalFormat("#.00");
                mJitterModel.mProgressView.mProgress = "NG  " + df.format(p[0]) + "," + df.format(p[1]);
                mJitterModel.mProgressView.isTestPassed = false;
                mJitterModel.mProgressView.invalidate();
            }
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            mJitterModel.mProgressView.mProgress = "detected point " + df.format(p[0]) + "," + df.format(p[1]);
            mJitterModel.mProgressView.invalidate();
        }

        mJitterModel.mRecordPoints.add(p);


    }

    public void saveJitterTestToCsv(CsvEditor csvEditor, String item, HimaxApplication.MainHandler mainHandler) {
        mJitterModel.mEndTime = Calendar.getInstance().getTimeInMillis();
        long duration = mJitterModel.mEndTime - mJitterModel.mStartTime;
        String header = csvEditor.createNewHeader(item,
                duration, mJitterModel.mStartTime, mJitterModel.mEndTime, mJitterModel.mResult);
        boolean result1 = csvEditor.appendStringToFile(header, mJitterModel.mPathToSave);
        String data = csvEditor.transferRecordToString(mJitterModel.mRecordPoints);
        boolean result2 = csvEditor.appendStringToFile(data, mJitterModel.mPathToSave);

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FILE_SAVED_RESULT;
        msg.arg1 = OBJECTIVE_TEST_JITTER_TEST;
        msg.obj = (result1 && result2);
        mainHandler.sendMessage(msg);
    }

    public void createBoardLayout(RelativeLayout layout, Context context, String item, int type) {
        if (mBoardModel == null) {
            mBoardModel = new BoardModel();
        }
        mBoardModel.mLayout = layout;

        mBoardModel.mStartTime = Calendar.getInstance().getTimeInMillis();
        mBoardModel.mPathToSave = getFilePath(context, item,
                mBoardModel.mStartTime);
        ObjectivePatternView patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(), layout.getHeight(), type);
        patternView.mBoardSpace = mBoardModel.mSpaceToBoarder;
        mBoardModel.mBackgroundViewId = patternView.generateViewId();
        patternView.setId(mBoardModel.mBackgroundViewId);
        mBoardModel.mLayout.addView(patternView);

        PointRecordResultView trackView = new PointRecordResultView(context, mBoardModel.mPathToSave,
                context.getResources().getString(R.string.objective_csv_msg_wait_touch),
                context.getResources().getString(R.string.objective_csv_unsaved_msg));
        mBoardModel.mProgressView = trackView;
        mBoardModel.mLayout.addView(trackView);

    }

    public void startBoardProtectionTest(float[] p, int width, int length) {
        //calculate deviation and write to excel
        if (!mBoardModel.mResult) {
            return;
        }
        DecimalFormat df = new DecimalFormat("#.00");

        mBoardModel.mProgressView.mProgress = df.format(p[0]) + "," + df.format(p[1]);
        if (p[0] > (width - mBoardModel.mSpaceToBoarder) || p[0] < mBoardModel.mSpaceToBoarder) {
            mBoardModel.mResult = false;
            mBoardModel.mRecordPoints.add(p);
            mBoardModel.mProgressView.mProgress = "NG  " + df.format(p[0]) + "," + df.format(p[1]);
            mBoardModel.mProgressView.isTestPassed = false;
        }
        if (p[1] > (length - mBoardModel.mSpaceToBoarder) || p[1] < mBoardModel.mSpaceToBoarder) {
            mBoardModel.mResult = false;
            mBoardModel.mRecordPoints.add(p);
            mBoardModel.mProgressView.mProgress = "NG  " + df.format(p[0]) + "," + df.format(p[1]);
            mBoardModel.mProgressView.isTestPassed = false;
        }

        mBoardModel.mProgressView.invalidate();
    }

    public void startExtendBoardTest(float[] p, int width, int length) {
        //calculate deviation and write to excel

        if (p[0] > (width - mBoardModel.mSpaceToBoarder) || p[0] < mBoardModel.mSpaceToBoarder) {
            mBoardModel.mRecordPoints.add(p);
            DecimalFormat df = new DecimalFormat("#.00");
            mBoardModel.mProgressView.mProgress = "last record:  " + df.format(p[0]) + "," + df.format(p[1]);
            mBoardModel.mProgressView.invalidate();
        }
        if (p[1] > (length - mBoardModel.mSpaceToBoarder) || p[1] < mBoardModel.mSpaceToBoarder) {
            mBoardModel.mRecordPoints.add(p);
            DecimalFormat df = new DecimalFormat("#.00");
            mBoardModel.mProgressView.mProgress = "last record:  " + df.format(p[0]) + "," + df.format(p[1]);
            mBoardModel.mProgressView.invalidate();
        }

    }

    public void saveBoardProtectionDataToCsv(CsvEditor csvEditor, String item, HimaxApplication.MainHandler mainHandler, int uiSource) {
        mBoardModel.mEndTime = Calendar.getInstance().getTimeInMillis();
        long duration = mBoardModel.mEndTime - mBoardModel.mStartTime;
        String header = csvEditor.createNewHeader(item,
                duration, mBoardModel.mStartTime, mBoardModel.mEndTime, mBoardModel.mResult);
        boolean result1 = csvEditor.appendStringToFile(header, mBoardModel.mPathToSave);
        String data = csvEditor.transferRecordToString(mBoardModel.mRecordPoints);
        boolean result2 = csvEditor.appendStringToFile(data, mBoardModel.mPathToSave);

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FILE_SAVED_RESULT;
        msg.arg1 = uiSource;
        msg.obj = (result1 && result2);
        mainHandler.sendMessage(msg);
    }


    public void createGhostRecordView(RelativeLayout layout, Context context) {
        if (mGhostRecordModel == null) {
            mGhostRecordModel = new GhostRecordModel();
        }
        mGhostRecordModel.mLayout = layout;

        mGhostRecordModel.mStartTime = Calendar.getInstance().getTimeInMillis();
        mGhostRecordModel.mPathToSave = getFilePath(context, "ghost_record",
                mGhostRecordModel.mStartTime);

        View patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(), layout.getHeight(),
                PATTERN_GHOST_POINT_RECORD);
        mGhostRecordModel.mBackgroundViewId = patternView.generateViewId();
        patternView.setId(mGhostRecordModel.mBackgroundViewId);
        mGhostRecordModel.mLayout.addView(patternView);

        PointRecordResultView trackView = new PointRecordResultView(context, mGhostRecordModel.mPathToSave,
                context.getResources().getString(R.string.objective_csv_msg_wait_touch),
                context.getResources().getString(R.string.objective_csv_unsaved_msg));
        mGhostRecordModel.mProgressView = trackView;
        mGhostRecordModel.mLayout.addView(trackView);
    }

    public void startGhostPointRecord(float[] p) {
        //write to excel
        mGhostRecordModel.mRecordPoints.add(p);
        DecimalFormat df = new DecimalFormat("#.00");
        mGhostRecordModel.mProgressView.mProgress = "NG  " + df.format(p[0]) + "," + df.format(p[1]);
        mGhostRecordModel.mProgressView.isTestPassed = false;
        mGhostRecordModel.mProgressView.invalidate();
    }

    public void saveGhostPointDataToCsv(CsvEditor csvEditor, String item, HimaxApplication.MainHandler mainHandler) {
        mGhostRecordModel.mEndTime = Calendar.getInstance().getTimeInMillis();
        long duration = mGhostRecordModel.mEndTime - mGhostRecordModel.mStartTime;
        String header = csvEditor.createNewHeader(item,
                duration, mGhostRecordModel.mStartTime, mGhostRecordModel.mEndTime, (mGhostRecordModel.mRecordPoints.size() > 0) ? false : true);
        boolean result1 = csvEditor.appendStringToFile(header, mGhostRecordModel.mPathToSave);
        String data = csvEditor.transferRecordToString(mGhostRecordModel.mRecordPoints);
        boolean result2 = csvEditor.appendStringToFile(data, mGhostRecordModel.mPathToSave);

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FILE_SAVED_RESULT;
        msg.arg1 = OBJECTIVE_TEST_GHOST_POINT_RECORD;
        msg.obj = (result1 && result2);
        mainHandler.sendMessage(msg);
    }

    public void createPalmTestView(RelativeLayout layout, Context context) {
        if (mPalmModel == null) {
            mPalmModel = new PalmModel();
        }
        mPalmModel.mLayout = layout;

        mPalmModel.mStartTime = Calendar.getInstance().getTimeInMillis();
        mPalmModel.mPathToSave = getFilePath(context, "palm_test",
                mPalmModel.mStartTime);

        View patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(),
                layout.getHeight(), PATTERN_PALM_TEST);
        mPalmModel.mBackgroundViewId = patternView.generateViewId();
        patternView.setId(mPalmModel.mBackgroundViewId);
        mPalmModel.mLayout.addView(patternView);

        PointRecordResultView trackView = new PointRecordResultView(context, mPalmModel.mPathToSave,
                context.getResources().getString(R.string.objective_csv_msg_wait_touch),
                context.getResources().getString(R.string.objective_csv_unsaved_msg));
        mPalmModel.mProgressView = trackView;
        mPalmModel.mLayout.addView(trackView);
    }

    public void startPalmTest(float[] p) {
        if (mPalmModel.mResult) {
            mPalmModel.mResult = false;
            mPalmModel.mRecordPoints.add(p);
            DecimalFormat df = new DecimalFormat("#.00");
            mPalmModel.mProgressView.mProgress = "NG  " + df.format(p[0]) + "," + df.format(p[1]);
            mPalmModel.mProgressView.isTestPassed = false;
            mPalmModel.mProgressView.invalidate();
        }
    }

    public void savePalmTestDataToCsv(CsvEditor csvEditor, String item, HimaxApplication.MainHandler mainHandler) {
        mPalmModel.mEndTime = Calendar.getInstance().getTimeInMillis();
        long duration = mPalmModel.mEndTime - mPalmModel.mStartTime;
        String header = csvEditor.createNewHeader(item,
                duration, mPalmModel.mStartTime, mPalmModel.mEndTime, mPalmModel.mResult);
        boolean result1 = csvEditor.appendStringToFile(header, mPalmModel.mPathToSave);
        String data = csvEditor.transferRecordToString(mPalmModel.mRecordPoints);
        boolean result2 = csvEditor.appendStringToFile(data, mPalmModel.mPathToSave);

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FILE_SAVED_RESULT;
        msg.arg1 = OBJECTIVE_TEST_PALM_TEST;
        msg.obj = (result1 && result2);
        mainHandler.sendMessage(msg);
    }

    public void createMaxPointCountView(RelativeLayout layout, Context context) {
        if (mMaxPointCountModel == null) {
            mMaxPointCountModel = new MaxPointCountModel();
        }
        mMaxPointCountModel.mLayout = layout;

        mMaxPointCountModel.mStartTime = Calendar.getInstance().getTimeInMillis();
        mMaxPointCountModel.mPathToSave = getFilePath(context, "max_point_count",
                mMaxPointCountModel.mStartTime);

        View patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(),
                layout.getHeight(), PATTERN_MAX_POINT_COUNT);
        mMaxPointCountModel.mBackgroundViewId = patternView.generateViewId();
        patternView.setId(mMaxPointCountModel.mBackgroundViewId);
        mMaxPointCountModel.mLayout.addView(patternView);

        PointRecordResultView trackView = new PointRecordResultView(context, mMaxPointCountModel.mPathToSave,
                context.getResources().getString(R.string.objective_csv_msg_wait_touch),
                context.getResources().getString(R.string.objective_csv_unsaved_msg));
        mMaxPointCountModel.mProgressView = trackView;
        mMaxPointCountModel.mLayout.addView(trackView);
    }

    public void startMaxPointCount(int point_count) {
        if (point_count > mMaxPointCountModel.mMaxRecord) {
            mMaxPointCountModel.mMaxRecord = point_count;
        }
        mMaxPointCountModel.mProgressView.mProgress = "Max count(" + mMaxPointCountModel.mMaxRecord + ")";
        mMaxPointCountModel.mProgressView.invalidate();
    }

    public void saveMaxPointDataToCsv(CsvEditor csvEditor, String item, HimaxApplication.MainHandler mainHandler) {
        mMaxPointCountModel.mEndTime = Calendar.getInstance().getTimeInMillis();
        long duration = mMaxPointCountModel.mEndTime - mMaxPointCountModel.mStartTime;
        String header = csvEditor.createNewHeader(item,
                duration, mMaxPointCountModel.mStartTime, mMaxPointCountModel.mEndTime, true);
        boolean result1 = csvEditor.appendStringToFile(header, mMaxPointCountModel.mPathToSave);
        boolean result2 = csvEditor.appendStringToFile("Max Count," + mMaxPointCountModel.mMaxRecord, mMaxPointCountModel.mPathToSave);

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_FILE_SAVED_RESULT;
        msg.arg1 = OBJECTIVE_TEST_MAX_POINT_RECORD;
        msg.obj = (result1 && result2);
        mainHandler.sendMessage(msg);
    }


    private String getFilePath(Context context, String item, long timestamp) {
        return himax_config.mHXPath + item + "_" + timestamp + ".csv";
    }

    public void updateFileSavedResult(boolean result, int type, Context context) {
        switch (type) {
            case ObjectiveListId.OBJECTIVE_TEST_SNR: {
                break;
            }
            case ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK: {
            }
            case OBJECTIVE_TEST_JITTER_TEST: {
                if (result) {
                    mJitterModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mJitterModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }

                mJitterModel.mProgressView.invalidate();
                break;
            }
            case OBJECTIVE_TEST_MAX_POINT_RECORD: {
                if (result) {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }

                mMaxPointCountModel.mProgressView.invalidate();
                break;
            }
            case OBJECTIVE_TEST_GHOST_POINT_RECORD: {
                if (result) {
                    mGhostRecordModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }
                mGhostRecordModel.mProgressView.invalidate();
                break;
            }
            case OBJECTIVE_TEST_PALM_TEST: {
                if (result) {
                    mPalmModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }
                mPalmModel.mProgressView.invalidate();
                break;
            }
            case OBJECTIVE_TEST_BORAD_PROTECTION: {
                if (result) {
                    mBoardModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }
                mBoardModel.mProgressView.invalidate();
                break;
            }
            case OBJECTIVE_TEST_EXTEND_BORARD: {
                if (result) {
                    mBoardModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_msg);
                } else {
                    mMaxPointCountModel.mProgressView.mMessage = context.getResources().getString(R.string.objective_csv_saved_fail);
                }
                mBoardModel.mProgressView.invalidate();
            }
        }
    }


    public void createPatternView(RelativeLayout layout, Context context, int pattern, int numOfLine, boolean isNeedDetectFastLine) {
        if (mPatternModel == null) {
            mPatternModel = new PatternModel();
        }

        if (mPatternModel.mHorizontalLineNum > 0 && mPatternModel.mVerticalLineNum > 0 && pattern == PATTERN_CUSTOMER_SIX) {
            numOfLine = 2;
        }

        mPatternModel.createFitScaleCells(layout.getWidth(), layout.getHeight());

        ObjectivePatternView patternView = new ObjectivePatternView(context, 0, 0, layout.getWidth(),
                layout.getHeight(), pattern);
        patternView.mModel = mPatternModel;
        PatternTrackResultView trackView = new PatternTrackResultView(context, layout.getWidth(), layout.getHeight(), numOfLine, isNeedDetectFastLine);
        trackView.mPatternCells = mPatternModel.mPatternCells;
        patternView.mPatternCells = mPatternModel.mPatternCells;
        patternView.labelCustomerPatternCells();
        mPatternModel.mTrackViewId = trackView.generateViewId();
        trackView.setId(mPatternModel.mTrackViewId);
        layout.addView(patternView);
        layout.addView(trackView);
        mPatternModel.mLayout = layout;
    }

    public void recordEvent(MotionEvent event) {
        PatternTrackResultView view = (PatternTrackResultView) mPatternModel.mLayout.findViewById(mPatternModel.mTrackViewId);
        if (view != null) {
            view.recordEvent(event);
        }

    }


    public void bindPSensorView(TextView status, TextView brightness, RelativeLayout layout, EditText vibrator, TextView blockNum,
                                EditText sensingThreshold, EditText sensingUpperThreshold, EditText proximityDebug, EditText proximityValue,
                                EditText proximityUpdate, Button resetButton) {
        if (mPSensorTestModel == null) {
            mPSensorTestModel = new PSensorTestModel();
        }
        mPSensorTestModel.mTV_Status = status;
        mPSensorTestModel.mTV_Brightness = brightness;
        mPSensorTestModel.mLayout = layout;
        mPSensorTestModel.mTV_BlockNum = blockNum;
        mPSensorTestModel.mED_Vibrator = vibrator;
        mPSensorTestModel.mED_SensingThreshold = sensingThreshold;
        mPSensorTestModel.mED_SensingUpperThreshold = sensingUpperThreshold;
        mPSensorTestModel.mED_Vibrator.setText(mPSensorTestModel.mThrsholdVib + "");
        mPSensorTestModel.mED_SensingThreshold.setText(mPSensorTestModel.mMIN_SENSING_VALUE + "");
        mPSensorTestModel.mED_SensingUpperThreshold.setText(mPSensorTestModel.mMAX_SENSING_VALUE + "");
        mPSensorTestModel.mEdProxmiityDebug = proximityDebug;
        mPSensorTestModel.mEdProximityValue = proximityValue;
        mPSensorTestModel.mEdProximityUpdate = proximityUpdate;
        mPSensorTestModel.mResetButton = resetButton;

        mPSensorTestModel.mEdProxmiityDebug.setText(mPSensorTestModel.mAddress_proximity_debug);
        mPSensorTestModel.mEdProximityValue.setText(mPSensorTestModel.mAddress_proximity_value);
        mPSensorTestModel.mEdProximityUpdate.setText(mPSensorTestModel.mAddress_proximity_update);


    }

    public void startPSensorTest(final HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource) {

        mPSensorTestModel.mResetButton.setEnabled(true);
        mPSensorTestModel.isTerminated = false;

        //write node to notify mobile that mobile entered in Call UI.
        try {
            dataSource.wrtieRegister(mPSensorTestModel.mEdProximityUpdate.getText().toString(), 0xA33AA33A);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        long value_ago = 0;
        while (!mPSensorTestModel.isTerminated) {
            try {
                Thread.sleep(50);
                //cat node to calculate how bright the node show.
                long value = dataSource.readRegister(mPSensorTestModel.mEdProximityValue.getText().toString());
                if (value == 0)
                    value = value_ago;
                value_ago = value;

                Thread.sleep(50);

                String block = dataSource.readRegister(mPSensorTestModel.mEdProxmiityDebug.getText().toString(), true);
                mPSensorTestModel.mBlockNum = block;

                //send mainHandler msg to update UI.
                mPSensorTestModel.setBrightnessInPercentage(value);
                mainHandler.sendEmptyMessage(MSG_UPDATE_PSENSOR_DETAIL_INFO);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            //resume fw p sensor function to 0
            dataSource.wrtieRegister(mPSensorTestModel.mEdProximityUpdate.getText().toString(), 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    public void updatePSensorInfo() {
        if (mPSensorTestModel.mTV_Status == null) {
            return;
        }

        mPSensorTestModel.mTV_Status.setText("SensingValue: " + mPSensorTestModel.mSensingValue);
        mPSensorTestModel.mTV_Brightness.setText("Brightness: " + mPSensorTestModel.mBrightness + "%");
        mPSensorTestModel.mTV_BlockNum.setText("Block Num: " + mPSensorTestModel.mBlockNum);

        int color = 0;
        if (mPSensorTestModel.mBrightness > 95) {
            color = 255;
        }
        if (mPSensorTestModel.mBrightness <= 95 && mPSensorTestModel.mBrightness > 50) {
            color = 100 * mPSensorTestModel.mBrightness / 100;
        }
        if (mPSensorTestModel.mBrightness < 50) {
            color = 50 * mPSensorTestModel.mBrightness / 100;
        }
        String hex = addZeroForNum(Integer.toHexString(color), 2);
        String hexString = "#" + hex + hex + hex;

        mPSensorTestModel.mLayout.setBackgroundColor(Color.parseColor(hexString));
        try {
            mPSensorTestModel.mThrsholdVib = Integer.parseInt(mPSensorTestModel.mED_Vibrator.getText().toString());
            mPSensorTestModel.mMIN_SENSING_VALUE = Integer.parseInt(mPSensorTestModel.mED_SensingThreshold.getText().toString());
            mPSensorTestModel.mMAX_SENSING_VALUE = Integer.parseInt(mPSensorTestModel.mED_SensingUpperThreshold.getText().toString());
        } catch (Exception e) {

        }

        if (mPSensorTestModel.mBrightness < mPSensorTestModel.mThrsholdVib) {
            mVibrator.vibrate(50);
        }

    }

    public void setupSettingsData(HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource, boolean isFirstInit) {

        //get Diag data
//        190719 mark
//        if(mICData.val_icid == 0) {
//            Log.d(TAG, "Now is setupSettingsData");
//            mICData.readICIDByNode();
//            mICData.matchICIDStr2Int();
//            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
//        }

        if (mDataMonitorModel.mDiagOptionsKey.size() <= 0) {
            mDataMonitorModel.mDiagOptionsKey.clear();
            boolean exist =
                    getMonitorAttrFromSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsKey, mDataMonitorModel.mDiagOptionsKey);
            if (!exist) {
                initSettingPageSharedPreference();
                setupSettingsData(mainHandler, dataSource, true);
                return;
            }
            getMonitorAttrFromSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsValue, mDataMonitorModel.mDiagOptionsValue);
        }

        //get Transform data
        if (mDataMonitorModel.mTransformOptionsKey.size() <= 0) {
            getMonitorAttrFromSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsKey, mDataMonitorModel.mTransformOptionsKey);
            getMonitorAttrFromSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsValue, mDataMonitorModel.mTransformOptionsValue);
        }

        //get Max DC value;
        try {
            mDataMonitorModel.mMaxDCValueForBP = dataSource.readMaxDC(mICData.dc_max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDataMonitorModel.mMaxDCValueForBP <= 0) {
            mDataMonitorModel.mMaxDCValueForBP = 1;
        }

        if (isFirstInit) {
            try {
                mDataMonitorModel.mOSR_CC = dataSource.isOSRCCOpend();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

        createAllMonitorRadioButtons();

        mainHandler.sendEmptyMessage(MSG_UPDATE_DATA_MONITOR_SETTINGS_PAGE);
    }

    private void initSettingPageSharedPreference() {
        String initDiagKey = "Close,IIR,DC,Baseline,IIR-sram,DC-sram,Add";
        String initDiagValue = "0,1,2,3,11,12,0";
        saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsKey, initDiagKey);
        saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsValue, initDiagValue);

        String initTransformKey = "Tran,Tran,Tran,Tran";
        String initTransformValue = "origin,vertical,horizontal,both";
        saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsKey, initTransformKey);
        saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsValue, initTransformValue);
    }

    private void createAllMonitorRadioButtons() {
        for (int i = 0; i < mDataMonitorModel.mDiagOptionsKey.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
            p.setId(i);
            p.setLayoutParams(params);
            p.setValue(mDataMonitorModel.mDiagOptionsKey.get(i));
            if (i == 0) {
                p.setUnit("");
                p.setChecked(true);
            } else if (i == (mDataMonitorModel.mDiagOptionsKey.size() - 1)) {
                p.setUnit("");
            } else {
                p.setUnit("diag" + mDataMonitorModel.mDiagOptionsValue.get(i));
            }
            mDataMonitorModel.mRawDataRadios.add(p);
        }

        for (int i = 0; i < mDataMonitorModel.mTransformOptionsValue.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
            p.setLayoutParams(params);
            p.setId(i);
            p.setValue(mDataMonitorModel.mTransformOptionsKey.get(i));
            p.setUnit(mDataMonitorModel.mTransformOptionsValue.get(i));
            mDataMonitorModel.mTransformRadios.add(p);
        }

        {
            String[] key = {"Normal", "Keep", "Keep", "Normal", "Keep"};
            String[] value = {"data", "max", "min", "diff", "diff max"};
            for (int i = 0; i < key.length; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
                p.setLayoutParams(params);
                p.setId(i);
                p.setValue(key[i]);
                p.setUnit(value[i]);
                if (i == 0) {
                    p.setChecked(true);
                }
                mDataMonitorModel.mDataKeepRadios.add(p);
            }
        }

        {
            String[] key = {"Grid", "Raw", "Read"};
            String[] value = {"data", "data", "log"};
            for (int i = 0; i < key.length; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
                p.setLayoutParams(params);
                p.setId(i);
                p.setValue(key[i]);
                p.setUnit(value[i]);
                if (i == 0) {
                    p.setChecked(true);
                }
                mDataMonitorModel.mBackgroundRadios.add(p);
            }
        }

        {
            String[] key = {"Type1", "Type2", "Type3"};
            String[] value = {"B->W", "R->Y", "G->Y"};
            for (int i = 0; i < key.length; i++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
                p.setLayoutParams(params);
                p.setId(i);
                p.setValue(key[i]);
                p.setUnit(value[i]);
                if (i == 0) {
                    p.setChecked(true);
                }
                mDataMonitorModel.mColorOptionRadios.add(p);
            }
        }
    }

    public void updateMonitorSettingPage(HimaxApplication.WorkerHandler mWorkerHandler) {
        setupDiagRadio(mWorkerHandler);
        setupTransformType(mWorkerHandler);
        setupDataKeep();
        setupColorOption();
        setupColorValueBar();
        setupFontValueBar();
        setupBackgroundRadio(mWorkerHandler);
        setupBlackSwitch();
        setupRecordSwitch();
        setupDragSwitch();
        setupAreaInfoSwitch();
        setupOSRCCSwitch();
        setupCheckBP();
        if (mDataMonitorModel.mRecordSwitchValue) {
            disableAllSettings();
        }
    }

    private void setupDiagRadio(final HimaxApplication.WorkerHandler mWorkerHandler) {
        for (PresetValueButton b : mDataMonitorModel.mRawDataRadios) {
            mDataMonitorModel.mRawDataGroup.addView(b);
        }
        mDataMonitorModel.mRawDataGroup.check(mDataMonitorModel.mDiagOption);
        mDataMonitorModel.mRawDataGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                int originOption = mDataMonitorModel.mDiagOption;

                if (checkedId == mDataMonitorModel.mDiagOptionsKey.size() - 1) {
                    mDataMonitorModel.mDiagOption = 0;
                    final ScrollView sl = createEidtItemDiagView(mDataMonitorModel.mActivityContext, mDataMonitorModel.mLayout.getHeight(),
                            mDataMonitorModel.mLayout.getWidth(), mDataMonitorModel.mDiagOptionsKey, mDataMonitorModel.mDiagOptionsValue);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mDataMonitorModel.mActivityContext);
                    dialog.setTitle("Edit driver node [diag] group.");
                    dialog.setIcon(R.drawable.ic_launcher);
                    dialog.setView(sl);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int num = (int) sl.getTag();
                            StringBuilder sb_key = new StringBuilder();
                            StringBuilder sb_value = new StringBuilder();
                            for (int i = 0; i < num; i++) {
                                EditText et_key = (EditText) sl.findViewById(1000 + i);
                                EditText et_value = (EditText) sl.findViewById(2000 + i);
                                if (!"".equals(et_key.getText().toString().trim()) && !"".equals(et_value.getText().toString().trim())) {
                                    sb_key.append(et_key.getText());
                                    sb_key.append(",");
                                    sb_value.append(et_value.getText());
                                    sb_value.append(",");
                                }
                            }
                            sb_key.append("Add");
                            sb_value.append("0");
                            saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsKey, sb_key.toString());
                            saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorDiagOptionsValue, sb_value.toString());
                            mDataMonitorModel.mDiagOption = 0;
                            mDataMonitorModel.reloadRadios();
                            Message msg = Message.obtain();
                            msg.arg1 = 1;
                            msg.what = MSG_DATA_MONITOR_RELOAD_SETTINGS;
                            mWorkerHandler.sendMessage(msg);
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDataMonitorModel.mRawDataGroup.check(0);
                        }
                    });
                    dialog.show();
                    return;
                }

                try {
                    mDataMonitorModel.mDiagOption = Integer.valueOf(mDataMonitorModel.mDiagOptionsValue.get(checkedId));
                    if (checkedId != 0) {
                        if (originOption == 0) {
                            // close option to any diag option.
                            mWorkerHandler.sendEmptyMessage(MSG_DATA_MONITOR_START);
                        } else {
                            // any non-close option to any non-close option.
                            mDataMonitorModel.isNeedReEcho = true;
                        }
                    } else {
                        mDataMonitorModel.isNeedReDrawBackgound = true;
                    }
                } catch (Exception e) {
                    mDataMonitorModel.mDiagOption = 0;
                }
            }
        });
    }

    private void setupTransformType(final HimaxApplication.WorkerHandler mWorkerHandler) {
        for (PresetValueButton b : mDataMonitorModel.mTransformRadios) {
            mDataMonitorModel.mTransformGroup.addView(b);
        }
        mDataMonitorModel.mDiagOption = 0;
        mDataMonitorModel.mTransformGroup.check(mDataMonitorModel.mTransformOption);
        mDataMonitorModel.mTransformGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
//                if(checkedId == mDataMonitorModel.mTransformOptionsKey.size()-1) {
//                    mDataMonitorModel.mDiagOption = 0;
//                    final ScrollView sl = createEidtItemDiagView(mDataMonitorModel.mActivityContext, mDataMonitorModel.mLayout.getHeight(),
//                            mDataMonitorModel.mLayout.getWidth(), mDataMonitorModel.mTransformOptionsKey, mDataMonitorModel.mTransformOptionsValue);
//                    final AlertDialog.Builder dialog = new AlertDialog.Builder(mDataMonitorModel.mActivityContext);
//                    dialog.setTitle("Edit driver node [diag_arr]");
//                    dialog.setIcon(R.drawable.ic_launcher);
//                    dialog.setView(sl);
//                    dialog.setCancelable(false);
//                    dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            int num = (int) sl.getTag();
//                            StringBuilder sb_key = new StringBuilder();
//                            StringBuilder sb_value = new StringBuilder();
//                            for(int i=0; i<num; i++) {
//                                EditText et_key = (EditText) sl.findViewById(1000+i);
//                                EditText et_value = (EditText) sl.findViewById(2000+i);
//                                if(!"".equals(et_key.getText().toString().trim()) && !"".equals(et_value.getText().toString().trim())) {
//                                    sb_key.append(et_key.getText());
//                                    sb_key.append(",");
//                                    sb_value.append(et_value.getText());
//                                    sb_value.append(",");
//                                }
//                            }
//                            sb_key.append("Add");
//                            sb_value.append("0");
//                            saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsKey, sb_key.toString());
//                            saveMonitorAttrToSP(mDataMonitorModel.mActivityContext, "HIMAX", himax_config.sMonitorTransformOptionsValue, sb_value.toString());
//                            mDataMonitorModel.mDiagOption = 0;
//                            mDataMonitorModel.reloadRadios();
//                            mWorkerHandler.sendEmptyMessage(MSG_DATA_MONITOR_RELOAD_SETTINGS);
//                        }
//                    });
//                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            mDataMonitorModel.mDiagOption = 0;
//                        }
//                    });
//                    dialog.show();
//                    return;
//                }

                try {
                    mDataMonitorModel.mTransformOption = checkedId;
                } catch (Exception e) {
                    mDataMonitorModel.mTransformOption = 0;
                }

                int originDiagOption = mDataMonitorModel.mDiagOption;
                mDataMonitorModel.mDiagOption = 0;
//                mDataMonitorModel.isNeedReEcho = true;

                Message msg = Message.obtain();
                msg.what = MSG_DATA_MONITOR_SET_TRANSFORM;
                msg.arg1 = originDiagOption;
                msg.arg2 = mDataMonitorModel.mBackgroundGroup.getCheckedRadioButtonId();
                mWorkerHandler.sendMessageDelayed(msg, 100);
            }
        });
    }

    private void setupDataKeep() {
        for (PresetValueButton b : mDataMonitorModel.mDataKeepRadios) {
            mDataMonitorModel.mDataKeepGroup.addView(b);
        }
        mDataMonitorModel.mDataKeepGroup.check(mDataMonitorModel.mDataKeepOption);
        mDataMonitorModel.mDataKeepGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                mDataMonitorModel.mDataKeepOption = checkedId;
                mDataMonitorModel.isNeedReEcho = true;
            }
        });
    }

    private void setupColorOption() {
        for (PresetValueButton b : mDataMonitorModel.mColorOptionRadios) {
            mDataMonitorModel.mColorOptionGroup.addView(b);
        }
        mDataMonitorModel.mColorOptionGroup.check(mDataMonitorModel.mColorType);
        mDataMonitorModel.mColorOptionGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                mDataMonitorModel.mColorType = checkedId;
            }
        });
    }

    private void setupColorValueBar() {
        mDataMonitorModel.mColorValueBar.setMax(mDataMonitorModel.mColorBarMax);
        mDataMonitorModel.mColorValueBar.setProgress(mDataMonitorModel.mColorDataMax);
        mDataMonitorModel.mColorValueBar.incrementProgressBy(1);
        mDataMonitorModel.mColorValueEdit.setText(mDataMonitorModel.mColorBarMax + "");
        mDataMonitorModel.mColorValueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int value = Integer.valueOf(editable.toString());
                    mDataMonitorModel.mColorBarMax = value;
                    mDataMonitorModel.mColorValueBar.setMax(value);
                } catch (Exception e) {

                }
            }
        });
        mDataMonitorModel.mColorValueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                mDataMonitorModel.mColorValueEdit.setText((seekBar.getProgress()+2)+"");
                mDataMonitorModel.mColorDataMax = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupFontValueBar() {
        mDataMonitorModel.mFontValueBar.setProgress(mDataMonitorModel.mFontSize - 3);
        mDataMonitorModel.mFontValueBar.incrementProgressBy(1);
        mDataMonitorModel.mFontValueEdit.setText((mDataMonitorModel.mFontSize) + "");
        mDataMonitorModel.mFontValueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDataMonitorModel.mFontValueEdit.setText((seekBar.getProgress() + 2) + "");
                mDataMonitorModel.mFontSize = seekBar.getProgress() + 2;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupBackgroundRadio(final HimaxApplication.WorkerHandler workerHandler) {
        for (PresetValueButton b : mDataMonitorModel.mBackgroundRadios) {
            mDataMonitorModel.mBackgroundGroup.addView(b);
        }
        mDataMonitorModel.mBackgroundGroup.check(mDataMonitorModel.mBackgroundOption);
        mDataMonitorModel.mBackgroundGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                mDataMonitorModel.mBackgroundOption = checkedId;
                mDataMonitorModel.isNeedReDrawBackgound = true;
                switch (checkedId) {
                    case DataMonitorConfig.SCREEN_MODE_RAW_STRING: {
                        mDataMonitorModel.mLayout.resumeScaleAndPosition();
                        if (!mDataMonitorModel.mRecordSwitchValue) {
                            enableAllSettings();
                        }
                        mDataMonitorModel.clearBackgroundView();
                    }
                    break;
                    case DataMonitorConfig.SCREEN_MODE_READ_LOG: {
                        disableAllSettings();
                        mDataMonitorModel.mDiagOption = 0;
                        mDataMonitorModel.clearBackgroundView();

                        mDataMonitorModel.mLayout.resumeScaleAndPosition();

                        WebView web = new WebView(mDataMonitorModel.mActivityContext);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        web.setLayoutParams(lp);
                        web.loadDataWithBaseURL("", "Loading data, please wait a minute.", "text/html", "UTF-8", "");
                        mDataMonitorModel.mLayout.setTag(web);
                        mDataMonitorModel.mLayout.addView(web);
                        workerHandler.sendEmptyMessage(MSG_DATA_MONITOR_FIND_CSV_LOG);
                    }
                    break;
                    default:
                        if (!mDataMonitorModel.mRecordSwitchValue) {
                            enableAllSettings();
                        }
                        mDataMonitorModel.clearBackgroundView();
                        break;
                }
            }
        });
    }

    private void setupBlackSwitch() {
        mDataMonitorModel.mLayout.isEnableBlack = mDataMonitorModel.mBlackSwitch.isChecked();
        mDataMonitorModel.mBlackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                mDataMonitorModel.mLayout.isEnableBlack = b;
                SharedPreferences.Editor sp = mDataMonitorModel.mActivityContext.getSharedPreferences("HIAPK", 0).edit();
                sp.putBoolean("Monitor_Black", mDataMonitorModel.mLayout.isEnableBlack);
                sp.commit();

                TextView tv = (TextView) mDataMonitorModel.mLayout.getTag();

                if (b) {
                    Log.d(TAG, "Enable Black");
                    if (tv != null)
                        tv.setTextColor(Color.WHITE);
                    mDataMonitorModel.mLayout.setBackgroundColor(Color.BLACK);
                } else {
                    Log.d(TAG, "disable Black");
                    if (tv != null)
                        tv.setTextColor(Color.BLACK);
                    mDataMonitorModel.mLayout.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    private void setupRecordSwitch() {
        mDataMonitorModel.mRecordSwitch.setChecked(mDataMonitorModel.mRecordSwitchValue);
        mDataMonitorModel.mRecordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    mDataMonitorModel.mDiagOption = 0;
                    mDataMonitorModel.mRawDataGroup.check(0);
                    createDataRecordSettings(b);
                    disableAllSettings();
                } else {
                    if (mDataMonitorModel.mRecordSwitchValue) {
                        mDataMonitorModel.mRecordSwitchValue = b;
                        mDataMonitorModel.mNotificationManager.cancel(mDataMonitorModel.NOTIFY_ID);
                        enableAllSettings();
                    }
                }
            }
        });
    }

    private void setupDragSwitch() {
        mDataMonitorModel.mLayout.isEnableDrag = mDataMonitorModel.mDragSwitch.isChecked();
        mDataMonitorModel.mDragSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                mDataMonitorModel.mLayout.isEnableDrag = b;
                if (!b) {
                    SharedPreferences.Editor sp = mDataMonitorModel.mActivityContext.getSharedPreferences("HIAPK", 0).edit();
                    sp.putFloat("Monitor_Scale", mDataMonitorModel.mLayout.mScale);
                    sp.putString("Monitor_Position", mDataMonitorModel.mLayout.mLeftP + "," + mDataMonitorModel.mLayout.mTopP +
                            "," + mDataMonitorModel.mLayout.mRightP + "," + mDataMonitorModel.mLayout.mBottomP);
                    sp.commit();
                }
            }
        });
    }

    private void setupAreaInfoSwitch() {
        mDataMonitorModel.mAreaInfoSwitch.setChecked(mDataMonitorModel.isShowInfo);
        mDataMonitorModel.mAreaInfoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                mDataMonitorModel.isShowInfo = b;
                mDataMonitorModel.isNeedReDrawBackgound = true;
            }
        });
    }

    private void setupOSRCCSwitch() {
        mDataMonitorModel.mOSRCCSwtich.setChecked(mDataMonitorModel.mOSR_CC);
        mDataMonitorModel.mOSRCCSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("Steve", "boolean : " + b);
                if (mDataMonitorModel.mOSR_CC != b) {
                    mDataMonitorModel.mDiagOption = 0;
                    mDataMonitorModel.mOSR_CC = b;
                    mDataMonitorModel.isNeedReDrawBackgound = true;
                    mDataMonitorModel.reloadRadios();
                    mWorkerHandler.sendEmptyMessage(MSG_DATA_MONIOTR_SENSING_ON_OFF);
                    Message msg = Message.obtain();
                    msg.arg1 = 1;
                    msg.what = MSG_DATA_MONITOR_RELOAD_SETTINGS;
                    mWorkerHandler.sendMessage(msg);
                    mMainHandler.removeMessages(MSG_START_OSR_CC_PROCESS);
                    mMainHandler.sendEmptyMessage(MSG_START_OSR_CC_PROCESS);
//                    mWorkerHandler.removeMessages(MSG_SWITCH_OSC_CC);
//                    mWorkerHandler.sendEmptyMessage(MSG_SWITCH_OSC_CC);
                }
            }
        });
    }

    public void switchOSRCC(NodeDataSource dataSource) {
        dataSource.setOSRCCSwitch(mObjectiveTestController.mDataMonitorModel.mOSR_CC);
    }

    private void setupCheckBP() {
        mDataMonitorModel.mTextMaxDC.setText("detected max dc: " + mDataMonitorModel.mMaxDCValueForBP);
        mDataMonitorModel.mCheckBP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mDataMonitorModel.mColorValueBar.setMax(100);
                    mDataMonitorModel.mColorValueBar.setProgress(100);
                    mDataMonitorModel.mColorValueEdit.setText("100");
                } else {
                    mDataMonitorModel.mColorValueBar.setMax(mDataMonitorModel.mColorBarMax);
                    mDataMonitorModel.mColorValueBar.setProgress(mDataMonitorModel.mColorDataMax);
                    mDataMonitorModel.mColorValueEdit.setText(mDataMonitorModel.mColorBarMax + "");
                }

            }
        });
    }

    private void createDataRecordSettings(final boolean switchRecord) {
        LayoutInflater inflater = LayoutInflater.from(mDataMonitorModel.mActivityContext);
        View alertLayout = inflater.inflate(R.layout.alert_record_settings, null);
        final PresetRadioGroup diagRadio = (PresetRadioGroup) alertLayout.findViewById(R.id.rawdata_radio);
        final NumberPicker minPicker = (NumberPicker) alertLayout.findViewById(R.id.minPicker);
        final NumberPicker secPicker = (NumberPicker) alertLayout.findViewById(R.id.secPicker);
        final CheckBox checkTimer = (CheckBox) alertLayout.findViewById(R.id.timer_check);
        checkTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    minPicker.setEnabled(true);
                    secPicker.setEnabled(true);
                } else {
                    minPicker.setEnabled(false);
                    secPicker.setEnabled(false);
                }
            }
        });
        minPicker.setMaxValue(60);
        minPicker.setMinValue(0);
        secPicker.setMaxValue(60);
        secPicker.setMinValue(1);
        minPicker.setEnabled(false);
        secPicker.setEnabled(false);
        for (int i = 1; i < mDataMonitorModel.mDiagOptionsKey.size() - 1; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            PresetValueButton p = new PresetValueButton(mDataMonitorModel.mActivityContext, null, R.style.PresetLayoutButton);
            p.setId(i);
            p.setLayoutParams(params);
            p.setValue(mDataMonitorModel.mDiagOptionsKey.get(i));
            p.setUnit("diag" + mDataMonitorModel.mDiagOptionsValue.get(i));
            diagRadio.addView(p);
            if (i == 1) {
                diagRadio.check(i);
            }
        }
        diagRadio.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                String value = mDataMonitorModel.mDiagOptionsValue.get(checkedId);
                int diag = 1;
                try {
                    diag = Integer.valueOf(value);
                } catch (Exception e) {
                }
                if (diag >= 10) {
                    checkTimer.setChecked(true);
                    checkTimer.setEnabled(false);
                } else {
                    checkTimer.setChecked(false);
                    checkTimer.setEnabled(true);
                }
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(mDataMonitorModel.mActivityContext);
        alert.setTitle("Record Settings");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enableAllSettings();
                mDataMonitorModel.mRecordSwitch.setChecked(false);
            }
        });
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int checkId = diagRadio.getCheckedRadioButtonId();
                boolean enableTimer = checkTimer.isChecked();
                int min = minPicker.getValue();
                int sec = secPicker.getValue();
                Log.d(TAG, String.format("min=%d, sec=%d, diag=%d", min, sec, Integer.valueOf(mDataMonitorModel.mDiagOptionsValue.get(checkId))));
                mDataMonitorModel.mRecordSwitchValue = switchRecord;
                if (!enableTimer) {
                    min = 0;
                    sec = 0;
                }
                mDataMonitorModel.setupNotifcation(mDataMonitorModel.mActivityContext,
                        Integer.valueOf(mDataMonitorModel.mDiagOptionsValue.get(checkId)), min, sec, true);
                Notification nf = mDataMonitorModel.mNotification.build();
                nf.flags |= Notification.FLAG_INSISTENT;
                nf.flags |= Notification.FLAG_NO_CLEAR;
                mDataMonitorModel.mNotificationManager.notify(mDataMonitorModel.NOTIFY_ID, nf);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


    public void saveMonitorAttrToSP(Context context, String path, String name, String values) {
        SharedPreferences sp = context.getSharedPreferences(path, Context.MODE_PRIVATE);
        sp.edit().putString(name, values).commit();
    }

    public boolean getMonitorAttrFromSP(Context context, String path, String name, List<String> values) {
        SharedPreferences sp = context.getSharedPreferences(path, Context.MODE_PRIVATE);
        String temp = sp.getString(name, null);
        if (temp != null) {
            String[] parse = temp.split(",");
            for (int i = 0; i < parse.length; i++) {
                values.add(parse[i]);
            }
        } else {
            return false;
        }
        return true;
    }


    public void bindDataMonitorLayout(BaseLayout dataLayout, Context context) {
        if (mDataMonitorModel == null) {
            mDataMonitorModel = new DataMonitorModel(dataLayout, context);
        } else {
            mDataMonitorModel.mLayout = dataLayout;
            mDataMonitorModel.mActivityContext = context;
        }
    }

    public void collectMonitorDataNonStop(HimaxApplication.MainHandler mainHandler, NodeDataSource dataSource) {
        if (mDataMonitorModel == null) {
            return;
        }

        //get Channels
        int[] channels = new int[2];
        dataSource.getRawDataRowAndColumn(channels, true);
        mDataMonitorModel.mRow = channels[0];
        mDataMonitorModel.mCol = channels[1];

        boolean isFirstTime = true;
        mDataMonitorModel.isStopped = false;
        final int DROP_FRAME_NUM = 10;
        int drop = DROP_FRAME_NUM;
        boolean diagResult;

        while (mDataMonitorModel.mDiagOption != 0) {

            //catch frame
            int[][] frame = new int[mDataMonitorModel.mRow][mDataMonitorModel.mCol];
            int ll = (mDataMonitorModel.mRow > mDataMonitorModel.mCol) ? mDataMonitorModel.mCol : mDataMonitorModel.mRow;
            if (mDataMonitorModel.mAreaInfo == null) {
                mDataMonitorModel.mAreaInfo = new int[2][ll];
            }

            StringBuilder raw = new StringBuilder();
            diagResult = dataSource.readSpecificDiag(mDataMonitorModel.mDiagOption, frame, isFirstTime, raw, mDataMonitorModel.mTransformOption, true, mDataMonitorModel.mAreaInfo);

            mDataMonitorModel.mRawDataString = raw.toString();
//            if(!diagResult && mDataMonitorModel.mBackgroundOption == DataMonitorConfig.SCREEN_MODE_PARSED_DATA) {
//                mDataMonitorModel.mDiagOption = 0;
//                return;
//            }

            if (mDataMonitorModel.isNeedReEcho) {
                mDataMonitorModel.isNeedReEcho = false;
                isFirstTime = true;
                drop = DROP_FRAME_NUM;
            }

            if (drop == 0) {
                if (isFirstTime) {
                    isFirstTime = false;
                    mDataMonitorModel.mFrame = new int[mDataMonitorModel.mRow][mDataMonitorModel.mCol];
                    mDataMonitorModel.mPreviousFrame = new int[mDataMonitorModel.mRow][mDataMonitorModel.mCol];
                    if (mDataMonitorModel.mDataKeepOption != DataMonitorConfig.DATA_KEEP_DIFF_MAX) {
                        copyDataFromAtoB(frame, mDataMonitorModel.mFrame, mDataMonitorModel.mCheckBP.isChecked(), mDataMonitorModel.mMaxDCValueForBP);
                    }
                    copyDataFromAtoB(frame, mDataMonitorModel.mPreviousFrame, mDataMonitorModel.mCheckBP.isChecked(), mDataMonitorModel.mMaxDCValueForBP);
                } else {
                    for (int i = 0; i < frame.length; i++) {
                        for (int j = 0; j < frame[0].length; j++) {
                            if (mDataMonitorModel.mFrame != null) {
                                if (mDataMonitorModel.mCheckBP.isChecked()) {
                                    frame[i][j] = (int) ((long) frame[i][j] * 100 / mDataMonitorModel.mMaxDCValueForBP);
                                }
                                switch (mDataMonitorModel.mDataKeepOption) {
                                    case DataMonitorConfig.DATA_KEEP_NORMAL: {
                                        mDataMonitorModel.mFrame[i][j] = frame[i][j];
                                    }
                                    break;
                                    case DataMonitorConfig.DATA_KEEP_MAX: {
                                        if (frame[i][j] > mDataMonitorModel.mFrame[i][j]) {
                                            mDataMonitorModel.mFrame[i][j] = frame[i][j];
                                        }
                                    }
                                    break;
                                    case DataMonitorConfig.DATA_KEEP_MIN: {
                                        if (frame[i][j] < mDataMonitorModel.mFrame[i][j]) {
                                            mDataMonitorModel.mFrame[i][j] = frame[i][j];
                                        }
                                    }
                                    break;
                                    case DataMonitorConfig.DATA_KEEP_DIFF: {
                                        int diff = frame[i][j] - mDataMonitorModel.mPreviousFrame[i][j];
                                        mDataMonitorModel.mFrame[i][j] = Math.abs(diff);
                                        mDataMonitorModel.mPreviousFrame[i][j] = frame[i][j];
                                    }
                                    break;
                                    case DataMonitorConfig.DATA_KEEP_DIFF_MAX: {
                                        int diff = Math.abs(frame[i][j] - mDataMonitorModel.mPreviousFrame[i][j]);
                                        if (diff > mDataMonitorModel.mFrame[i][j]) {
                                            mDataMonitorModel.mFrame[i][j] = diff;
                                        }
                                        mDataMonitorModel.mPreviousFrame[i][j] = frame[i][j];
                                    }
                                    break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }

                mainHandler.sendEmptyMessage(MSG_UPDATE_DATA_MONITOR_UI);

            } else {
                drop--;
            }
        }

        dataSource.readSpecificDiag(0, new int[mDataMonitorModel.mRow][mDataMonitorModel.mCol], true,
                new StringBuilder(), mDataMonitorModel.mTransformOption, true, null);
    }

    private void copyDataFromAtoB(int[][] source, int[][] target, boolean isBP, long max) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[0].length; j++) {
                if (isBP) {
                    source[i][j] = (int) ((long) source[i][j] * 100 / max);
                }
                target[i][j] = source[i][j];
            }
        }
    }

    public void updateDataMonitorUI() {
        if (mDataMonitorModel == null) {
            return;
        }
        if (mDataMonitorModel.mLayout == null) {
            return;
        }

        switch (mDataMonitorModel.mBackgroundOption) {
            case DataMonitorConfig.SCREEN_MODE_PARSED_DATA: {
                if (mDataMonitorModel.isNeedReDrawBackgound) {
                    mDataMonitorModel.isNeedReDrawBackgound = false;
                    mDataMonitorModel.clearBackgroundView();
                    Log.d("TT", "1");
                }

                if (mDataMonitorModel.mDiagOption != 0) {
                    if (mDataMonitorModel.mRawDataLayout == null) {

                        ViewGroup.LayoutParams l = mDataMonitorModel.mLayout.getLayoutParams();
                        l.width = (int) (mDataMonitorModel.mLayout.getWidth());
                        l.height = (int) (mDataMonitorModel.mLayout.getHeight());

                        View patternView = new MonitorGridView(mDataMonitorModel.mActivityContext, mDataMonitorModel.mRow,
                                mDataMonitorModel.mCol, l.width, l.height, mDataMonitorModel.mTransformOption, mDataMonitorModel.isShowInfo);
                        mDataMonitorModel.mBackgroundViewId = View.generateViewId();
                        patternView.setId(mDataMonitorModel.mBackgroundViewId);

                        RawdataLayout dataLayout = new RawdataLayout(mDataMonitorModel.mActivityContext, mDataMonitorModel.mRow, mDataMonitorModel.mCol,
                                l.width, l.height, mDataMonitorModel.isShowInfo);
                        mDataMonitorModel.mLayout.addView(dataLayout);
                        mDataMonitorModel.mLayout.addView(patternView);
                        mDataMonitorModel.mRawDataLayout = dataLayout;
                        Log.d("TT", "2");

                        SharedPreferences sp = mDataMonitorModel.mActivityContext.getSharedPreferences("HIAPK", 0);
                        String s = sp.getString("Monitor_Position", "");
                        if (s.length() > 0) {
                            String[] s_i = s.split(",");
                            try {
                                int ll = Integer.valueOf(s_i[0]);
                                int tt = Integer.valueOf(s_i[1]);
                                int rr = Integer.valueOf(s_i[2]);
                                int bb = Integer.valueOf(s_i[3]);
                                mDataMonitorModel.mLayout.setPreviousScaleAndPosition(sp.getFloat("Monitor_Scale", 1), ll, tt, rr, bb);
                            } catch (Exception e) {
                                e.fillInStackTrace();
                            }
                        }
                    }

                    if (!mDataMonitorModel.mLayout.isEnableDrag) {
                        mDataMonitorModel.mRawDataLayout.updateText(mDataMonitorModel.mFrame, mDataMonitorModel.mColorType,
                                mDataMonitorModel.mColorDataMax, mDataMonitorModel.mFontSize,
                                mDataMonitorModel.mCheckBP.isChecked(), mDataMonitorModel.mAreaInfo, mDataMonitorModel.isShowInfo);
                        mDataMonitorModel.mLayout.resumeOriginPosition();
                    }
                    if (mDataMonitorModel.mLayout.isEnableBlack) {
                        mDataMonitorModel.mLayout.setBackgroundColor(Color.BLACK);

                    } else {
                        mDataMonitorModel.mLayout.setBackgroundColor(Color.WHITE);
                    }
                }

            }
            break;
            case DataMonitorConfig.SCREEN_MODE_RAW_STRING: {
                if (mDataMonitorModel.isNeedReDrawBackgound) {
                    mDataMonitorModel.isNeedReDrawBackgound = false;
                    mDataMonitorModel.clearBackgroundView();
                    TextView tv = new TextView(mDataMonitorModel.mActivityContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0, 30, 0, 0);
                    tv.setLayoutParams(layoutParams);
                    tv.setTextSize(mDataMonitorModel.mFontSize);
                    tv.setHorizontallyScrolling(true);


                    tv.setText("Raw Data String");
                    tv.setTypeface(Typeface.MONOSPACE);
                    tv.setMovementMethod(new ScrollingMovementMethod());
                    mDataMonitorModel.mLayout.addView(tv);
                    mDataMonitorModel.mLayout.setTag(tv);
                    //mDataMonitorModel.mLayout.setHorizontalScrollBarEnabled(true);

                    SharedPreferences sp = mDataMonitorModel.mActivityContext.getSharedPreferences("HIAPK", 0);
                    String s = sp.getString("Monitor_Position", "");
                    if (s.length() > 0) {
                        String[] s_i = s.split(",");
                        try {
                            int ll = Integer.valueOf(s_i[0]);
                            int tt = Integer.valueOf(s_i[1]);
                            int rr = Integer.valueOf(s_i[2]);
                            int bb = Integer.valueOf(s_i[3]);
                            mDataMonitorModel.mLayout.setPreviousScaleAndPosition(sp.getFloat("Monitor_Scale", 1), ll, tt, rr, bb);
                        } catch (Exception e) {
                            e.fillInStackTrace();
                        }
                    }


                    if (mDataMonitorModel.mLayout.isEnableBlack) {
                        tv.setTextColor(Color.WHITE);
                        mDataMonitorModel.mLayout.setBackgroundColor(Color.BLACK);

                    } else {
                        tv.setTextColor(Color.BLACK);
                        mDataMonitorModel.mLayout.setBackgroundColor(Color.WHITE);
                    }
                }

                if (!mDataMonitorModel.mLayout.isEnableDrag) {
                    TextView tv = (TextView) mDataMonitorModel.mLayout.getTag();
                    if (tv != null && mDataMonitorModel.mRawDataString != null) {
                        tv.setText(mDataMonitorModel.mRawDataString);
                        if (tv.getTextSize() != mDataMonitorModel.mFontSize) {
                            tv.setTextSize(mDataMonitorModel.mFontSize);
                        }
                    }
                }


            }
            break;
            case DataMonitorConfig.SCREEN_MODE_TRUE_COLOR_BACKGROUND: {
            }
            break;
            case DataMonitorConfig.SCREEN_MODE_CUSTOMER_IMAGE_BACKGROUND: {
            }
            break;
        }
    }

    public void setTransformBySoftware(int diagOption) {
        mDataMonitorModel.mDiagOption = diagOption;
        mDataMonitorModel.isNeedReDrawBackgound = true;
        mDataMonitorModel.isNeedReEcho = true;
    }

    public void setTransformInDiagArr(NodeDataSource dataSoruce, int originDiagOption) {
        int diag_arr_cmd = 0;
        switch (mDataMonitorModel.mTransformOption) {
            case 0:
                diag_arr_cmd = 0;
                break;
            case 1:
                diag_arr_cmd = 4;
                break;
            case 2:
                diag_arr_cmd = 2;
                break;
            case 3:
                diag_arr_cmd = 7;
                break;
        }
        dataSoruce.setRawDataTransform(diag_arr_cmd);
        mDataMonitorModel.mDiagOption = originDiagOption;
        mDataMonitorModel.isNeedReDrawBackgound = true;
        mDataMonitorModel.isNeedReEcho = true;
    }

    private ScrollView createEidtItemDiagView(final Context context, int height, int width, List<String> itemKey, List<String> itemValue) {
        /* Main Layer,it will include all of layer*/
        final ScrollView sl = new ScrollView(context);
        /* Because Srcoll view just include only one linear layer
         * we should include all layer into one linear layer*/
        final LinearLayout ll = new LinearLayout(context);
        /*layer parameter*/
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ScrollView.LayoutParams slp = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        slp.height = height * 7 / 10;
        slp.width = width * 7 / 10;
        slp.gravity = Gravity.CENTER;
        llp.weight = 1;

        ll.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout.LayoutParams llp_per_layer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ll.setLayoutParams(llp);
        sl.setLayoutParams(slp);

        final ArrayList<LinearLayout> ll_per_layer = new ArrayList<LinearLayout>();
        final LinearLayout.LayoutParams per_field = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        per_field.width = width / 10 * 7 / 2;
        per_field.gravity = Gravity.CENTER;

        Button b = new Button(context);
        b.setText("add item");
        b.setBackgroundColor(Color.TRANSPARENT);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layer = new LinearLayout(context);

                layer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                layer.setGravity(Gravity.CENTER);
                layer.setLayoutParams(llp_per_layer);
                layer.setOrientation(LinearLayout.HORIZONTAL);
                EditText et_key = new EditText(context);
                EditText et_value = new EditText(context);
                et_key.setHint("<name>");
                et_value.setHint("<value>");
                et_key.setId(1000 + ll_per_layer.size());
                et_value.setId(2000 + ll_per_layer.size());
                layer.addView(et_key, per_field);
                layer.addView(et_value, per_field);

                ll.addView(layer);
                sl.requestLayout();
                ll_per_layer.add(layer);
                sl.setTag(ll_per_layer.size());
            }
        });
        ll.addView(b);
        /*for setting diag buttons*/
        for (int i = 0; i < itemKey.size() - 1; i++) {
            LinearLayout layer = new LinearLayout(context);
            ll_per_layer.add(layer);

            layer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layer.setGravity(Gravity.CENTER);
            layer.setLayoutParams(llp_per_layer);
            layer.setOrientation(LinearLayout.HORIZONTAL);

            EditText et_key = new EditText(context);
            EditText et_value = new EditText(context);
            et_key.setId(1000 + i);
            et_value.setId(2000 + i);
            if (i == 0) {
                et_key.setEnabled(false);
                et_value.setEnabled(false);
            }

            et_key.setText(itemKey.get(i));
            et_value.setText(itemValue.get(i));

            layer.addView(et_key, per_field);
            layer.addView(et_value, per_field);

            ll.addView(layer);
        }
        sl.addView(ll);
        sl.setTag(ll_per_layer.size());
        return sl;
    }

    private static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public void disableAllSettings() {
        if (mDataMonitorModel.mRawDataGroup == null) {
            return;
        }
        for (int i = 0; i < mDataMonitorModel.mRawDataGroup.getChildCount(); i++) {
            mDataMonitorModel.mRawDataGroup.getChildAt(i).setEnabled(false);
        }
        for (int i = 0; i < mDataMonitorModel.mTransformGroup.getChildCount(); i++) {
            mDataMonitorModel.mTransformGroup.getChildAt(i).setEnabled(false);
        }
        for (int i = 0; i < mDataMonitorModel.mDataKeepGroup.getChildCount(); i++) {
            mDataMonitorModel.mDataKeepGroup.getChildAt(i).setEnabled(false);
        }
        mDataMonitorModel.mColorValueBar.setEnabled(false);
        mDataMonitorModel.mFontValueBar.setEnabled(false);
    }

    public void resetAllMonitorSettingsValue() {
        mDataMonitorModel.mRecordSwitchValue = false;
    }

    public void enableAllSettings() {
        if (mDataMonitorModel.mRawDataGroup == null) {
            return;
        }
        for (int i = 0; i < mDataMonitorModel.mRawDataGroup.getChildCount(); i++) {
            mDataMonitorModel.mRawDataGroup.getChildAt(i).setEnabled(true);
        }
        for (int i = 0; i < mDataMonitorModel.mTransformGroup.getChildCount(); i++) {
            mDataMonitorModel.mTransformGroup.getChildAt(i).setEnabled(true);
        }
        for (int i = 0; i < mDataMonitorModel.mDataKeepGroup.getChildCount(); i++) {
            mDataMonitorModel.mDataKeepGroup.getChildAt(i).setEnabled(true);
        }
        if (mDataMonitorModel.mColorValueBar != null)
            mDataMonitorModel.mColorValueBar.setEnabled(true);
        if (mDataMonitorModel.mFontValueBar != null)
            mDataMonitorModel.mFontValueBar.setEnabled(true);
        mDataMonitorModel.mRecordSwitch.setChecked(mDataMonitorModel.mRecordSwitchValue);
    }

    public void sensingOnAndOff(NodeDataSource dataSource) {
        mDataMonitorModel.mDiagOption = 0;
        dataSource.readSpecificDiag(mDataMonitorModel.mDiagOption, null, true, new StringBuilder(), true);
        dataSource.sensingOffAndOn();
    }

    public void findAllCSVLog(HimaxApplication.MainHandler mainHandler) {
        String path = himax_config.mHXPath;
        File directory = new File(path);
        File[] files = directory.listFiles();
        mDataMonitorModel.mFoundLogs.clear();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            String[] type = name.split("\\.");
            if ("csv".equals(type[1])) {
                mDataMonitorModel.mFoundLogs.add(name);
            }
        }
        mainHandler.sendEmptyMessage(MSG_SHOW_FOUND_MONITOR_LOGS_ALERT);
    }

    public void showFoundLogsDialog(final HimaxApplication.WorkerHandler workerHandler) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mDataMonitorModel.mActivityContext);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Logs File");
        builderSingle.setCancelable(false);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mDataMonitorModel.mActivityContext, R.layout.himax_dialog_singlechoice);
        for (String item : mDataMonitorModel.mFoundLogs) {
            arrayAdapter.add(item);
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mDataMonitorModel.mBackgroundGroup.check(0);
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = Message.obtain();
                msg.what = MSG_DATA_OPEN_CSV_LOG;
                Bundle b = new Bundle();
                b.putString("file", mDataMonitorModel.mFoundLogs.get(which));
                msg.setData(b);
                mWorkerHandler.sendMessage(msg);
            }
        });
        builderSingle.show();
    }

    public String testFile;

    public void readCSVFile(HimaxApplication.MainHandler mainHandler, String fileName) {
        testFile = fileName;
        File file = new File(himax_config.mHXPath, fileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Toast.makeText(mDataMonitorModel.mActivityContext, "open file failed, caused by " + e, Toast.LENGTH_SHORT).show();
        }
        mDataMonitorModel.mCSVLogText = text.toString();
        mainHandler.sendEmptyMessage(MSG_UPDATE_MONITOR_CSV_LOG_CONTENT);
    }

    public void updateCSVLogUI() {
        WebView web = (WebView) mDataMonitorModel.mLayout.getTag();
        WebSettings webSettings = web.getSettings();
        web.loadDataWithBaseURL("", mDataMonitorModel.mCSVLogText.replaceAll("\\n", "<br/>"), "text/html", "UTF-8", "");
        webSettings.setTextZoom(1);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        mDataMonitorModel.mLayout.setTag(null);
        mDataMonitorModel.mCSVLogText = null;
    }

    public void cancelRecordNotification() {
        try {
            Log.d("NIM190722", "Run cancelRecordNotification");
            mDataMonitorModel.mRecordSwitchValue = false;
            mDataMonitorModel.mNotificationManager.cancel(mDataMonitorModel.NOTIFY_ID);
            mDataMonitorModel.mRecordSwitch.setChecked(false);
        } catch (NullPointerException e) {
            e.fillInStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    public void updateRecordNotificationProgress(int progress, int max) {
        try {
            StatusBarNotification[] nn = mDataMonitorModel.mNotificationManager.getActiveNotifications();
            if (nn.length <= 0) {
                return;
            }
            mDataMonitorModel.mNotification.setProgress(max, progress, false);
            mDataMonitorModel.mNotificationManager.notify(mDataMonitorModel.NOTIFY_ID, mDataMonitorModel.mNotification.build());
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }

    public void showProcessingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mDataMonitorModel.mActivityContext);
        mDialog = builder.setTitle("Processing ...")
                .setMessage("Please do not touch screen!")
                .setCancelable(false).create();
        mDialog.show();
    }

    public void dismissProcessingDialog() {
        mDialog.dismiss();
    }

    public static class OTCSaveLog {
        String save_log;

        public OTCSaveLog() {
            save_log = new String();
        }

        public void SetString(String in) {
            save_log = in;
        }

        public void UpdateString() {
            gSaveLog = save_log;
        }

        public void SaveLog(String in) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date builddate = new Date();

            String file_name = dateFormat.format(builddate) + "_SNRLog.csv";
            File path = new File("/sdcard/");
            try {
                File w_f = new File(path, file_name);
                BufferedWriter f_w = new BufferedWriter(new FileWriter(w_f));
                f_w.append(in);
                f_w.flush();
                f_w.close();
            } catch (Exception e) {
                Log.e("HXTP", e.toString());
            }

        }
    }
}
