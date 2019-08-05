package com.ln.himaxtouch.ObjectiveTest;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.R;

import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_BOARD_PROTECTION;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_FIVE;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_FOUR;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_ONE;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_SIX;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_THREE;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_CUSTOMER_TWO;
import static com.ln.himaxtouch.CustomView.ObjectivePatternView.PATTERN_EXTEND_BOARD;
import static com.ln.himaxtouch.HimaxApplication.MSG_COLLECT_TOUCHED_NOISE;
import static com.ln.himaxtouch.HimaxApplication.MSG_COLLECT_UNTOUCHED_NOISE;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_CSV_EXTEND_BOARD;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_GHOST_POINT_RECORD;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_JITTER_TEST;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_MAX_POINT_COUNT_RECORD;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_PALM_TEST;
import static com.ln.himaxtouch.HimaxApplication.MSG_SAVE_CSV_BOARD_PROTECTION;
import static com.ln.himaxtouch.HimaxApplication.MSG_SNR_FINISH;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_SNR_PROGRESSBAR;
import static com.ln.himaxtouch.HimaxApplication.mContext;
import static com.ln.himaxtouch.HimaxApplication.mNodeAcc;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_FINISH;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_TOUCHED_NOISE;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTED_UNTOUCHED_NOISE;
import static com.ln.himaxtouch.ObjectiveTest.SnrModel.STATE_COLLECTING_TOUCHED_NOISE;

/**
 * Created by 903622 on 2018/3/29.
 */

public class ObjectTestOngoingActivity extends Activity {

    private final static boolean DEBUG = true;
    private final static String TAG = "[HXTP]ObjectTestOngoing";

    private HimaxApplication mApplication;
    private ObjectiveTestController mController;
    private PopupWindow mSNRPopup;
    private PopupWindow mTestResultPopup;
    private RelativeLayout mMainLayout;
    private int mToDoItem;
    private int mToDoItemChild;
    private IObjectiveTestModel mTestModel;
    private boolean isPressSaved = false;

    static {
        System.loadLibrary("HimaxAPK");
    }

    //Native function
    public native String writeCfg(String[] stringArray);

    public native String readCfg(String[] stringArray);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_objectivetest_ongoing);
        if (mApplication == null) {
            mApplication = (HimaxApplication) getApplication();
        }
        if (mController == null) {
            mController = mApplication.getObjectiveTestController();
        }

        mToDoItem = getIntent().getIntExtra("ToDoItem", 0);
        mToDoItemChild = getIntent().getIntExtra("ToDoItemChild", 0);

        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Button b = (Button) findViewById(R.id.start_collection);
        b.post(new Runnable() {
            @Override
            public void run() {
                switch (mToDoItem) {
                    case ObjectiveListId.OBJECTIVE_TEST_SNR: {
                        createPopupForCollectionNoise();
                        mApplication.mWorkerHandler.sendEmptyMessage(MSG_COLLECT_UNTOUCHED_NOISE);
                        mTestModel = mController.mSnrModel;
                        break;
                    }
                    case ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK: {
                        if (mToDoItemChild == 0) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_ONE, 2, false);
                            break;
                        }
                        if (mToDoItemChild == 1) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_TWO, 2, false);
                            break;
                        }
                        if (mToDoItemChild == 2) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_THREE, 1, false);
                            break;
                        }
                        if (mToDoItemChild == 3) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_FOUR, 1, true);
                            break;
                        }
                        if (mToDoItemChild == 4) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_FIVE, 1, false);
                            break;
                        }
                        if (mToDoItemChild == 5) {
                            mController.createPatternView(mMainLayout, mContext, PATTERN_CUSTOMER_SIX, 1, false);
                            break;
                        }
                        break;
                    }
                    case ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST:
                        mController.createJitterLayout(mMainLayout, mContext);
                        mTestModel = mController.mJitterModel;
                        break;
                    case ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD:
                        mController.createMaxPointCountView(mMainLayout, mContext);
                        mTestModel = mController.mMaxPointCountModel;
                        break;
                    case ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD:
                        mController.createGhostRecordView(mMainLayout, mContext);
                        mTestModel = mController.mGhostRecordModel;
                        break;
                    case ObjectiveListId.OBJECTIVE_TEST_PALM_TEST:
                        mController.createPalmTestView(mMainLayout, mContext);
                        mTestModel = mController.mPalmModel;
                        break;
                    case ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION: {
                        mController.createBoardLayout(mMainLayout, mContext, "board_protection", PATTERN_BOARD_PROTECTION);
                        mTestModel = mController.mBoardModel;
                        break;
                    }
                    case ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD: {
                        mController.createBoardLayout(mMainLayout, mContext, "extend_board", PATTERN_EXTEND_BOARD);
                        mTestModel = mController.mBoardModel;
                    }
                    default:
                        break;

                }

            }
        });
    }

    private void createPopupForCollectionNoise() {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_snr_collection, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_popup_title);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        ProgressBar base_raw = view.findViewById(R.id.pb_baseraw);
        ProgressBar pb_item1 = (ProgressBar) view.findViewById(R.id.pb_item1);
        ProgressBar pb_item1_skip = (ProgressBar) view.findViewById(R.id.pb_item1_skip);
        ProgressBar pb_item2 = (ProgressBar) view.findViewById(R.id.pb_item2);
        ProgressBar pb_item2_skip = (ProgressBar) view.findViewById(R.id.pb_item2_skip);
//        pb_item1_skip.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
//        pb_item1_skip.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        mSNRPopup = new PopupWindow(this);
        mSNRPopup.setContentView(view);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_objectivetest_ongoing, null);
        updatePopupWindow(rootView, mSNRPopup);
        mController.mSnrModel.bindView(tv_title, tv_msg, base_raw, pb_item1, pb_item1_skip, pb_item2, pb_item2_skip, mSNRPopup);
    }

    private void updatePopupWindow(View rootView, PopupWindow popup) {
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setWindowLayoutMode(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popup.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        popup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                    case MotionEvent.ACTION_BUTTON_PRESS:
                    case MotionEvent.ACTION_SCROLL:
                        if (mController.mSnrModel.mCollectingState == STATE_COLLECTED_UNTOUCHED_NOISE) {
                            mController.mSnrModel.mTouchedX = motionEvent.getX();
                            mController.mSnrModel.mTouchedY = motionEvent.getY();
                            mController.mSnrModel.mCollectingState = STATE_COLLECTING_TOUCHED_NOISE;
                            mApplication.mWorkerHandler.sendEmptyMessage(MSG_COLLECT_TOUCHED_NOISE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        if (mController.mSnrModel.mCollectingState == STATE_COLLECTED_TOUCHED_NOISE) {
                            Log.d("HXTPNIM0711", "Status done");
//                            mController.mSnrModel.mCollectingState = STATE_COLLECTED_FINISH;
//                            mApplication.mWorkerHandler.sendEmptyMessage(MSG_SNR_FINISH);
                        }
                        break;
                }
                return true;
            }
        });
        popup.update();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTestModel != null) {
            mController.onPauseAtObjectTestOnGoingActivity(mTestModel);
        }

        if (mSNRPopup != null) {
            mSNRPopup.dismiss();
            mSNRPopup = null;
        }
        if (mTestResultPopup != null) {
            mTestResultPopup.dismiss();
            mTestResultPopup = null;
        }
    }

    @Override
    public void onBackPressed() {
//        if (mController.isAllowBack()) {
        super.onBackPressed();
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPressSaved) {
            switch (mToDoItem) {
                case ObjectiveListId.OBJECTIVE_TEST_SNR: {
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK: {
                    mController.recordEvent(event);
                    if (mToDoItemChild == 0) {
                        break;
                    }
                    if (mToDoItemChild == 1) {

                        break;
                    }
                    if (mToDoItemChild == 2) {

                        break;
                    }
                    if (mToDoItemChild == 3) {

                        break;
                    }
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST: {
                    float[] p = new float[2];
                    p[0] = event.getX();
                    p[1] = event.getY();
                    mController.startJitterTest(p);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD: {
                    int point_count = event.getPointerCount();
                    mController.startMaxPointCount(point_count);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD: {
                    float[] p = new float[2];
                    p[0] = event.getX();
                    p[1] = event.getY();
                    mController.startGhostPointRecord(p);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_PALM_TEST: {
                    float[] p = new float[2];
                    p[0] = event.getX();
                    p[1] = event.getY();
                    mController.startPalmTest(p);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION: {
                    float[] p = new float[2];
                    p[0] = event.getX();
                    p[1] = event.getY();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    mController.startBoardProtectionTest(p, size.x, size.y);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD: {
                    float[] p = new float[2];
                    p[0] = event.getX();
                    p[1] = event.getY();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    mController.startExtendBoardTest(p, size.x, size.y);
                }
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && !isPressSaved) {
            isPressSaved = true;
            switch (mToDoItem) {
                case ObjectiveListId.OBJECTIVE_TEST_SNR: {

                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK: {
                    if (mToDoItemChild == 0) {

                        break;
                    }
                    if (mToDoItemChild == 1) {

                        break;
                    }
                    if (mToDoItemChild == 2) {

                        break;
                    }
                    if (mToDoItemChild == 3) {

                        break;
                    }
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST: {
                    mController.mJitterModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mJitterModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_JITTER_TEST;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD: {
                    mController.mMaxPointCountModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mMaxPointCountModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_MAX_POINT_COUNT_RECORD;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD: {
                    mController.mGhostRecordModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mGhostRecordModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_GHOST_POINT_RECORD;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_PALM_TEST: {
                    mController.mPalmModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mPalmModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_PALM_TEST;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION: {
                    mController.mBoardModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mBoardModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_CSV_BOARD_PROTECTION;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                case ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD: {
                    mController.mBoardModel.mProgressView.mMessage = getResources().getString(R.string.objective_csv_saving_msg);
                    mController.mBoardModel.mProgressView.invalidate();
                    Message msg = Message.obtain();
                    msg.what = MSG_SAVE_CSV_EXTEND_BOARD;
                    mApplication.mWorkerHandler.sendMessage(msg);
                    break;
                }
                default:
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
