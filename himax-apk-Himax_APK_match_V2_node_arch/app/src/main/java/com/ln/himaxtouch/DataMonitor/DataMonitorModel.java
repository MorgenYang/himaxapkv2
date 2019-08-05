package com.ln.himaxtouch.DataMonitor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ln.himaxtouch.CustomView.RawdataLayout;
import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.HimaxRadioButton.PresetRadioGroup;
import com.ln.himaxtouch.HimaxRadioButton.PresetValueButton;
import com.ln.himaxtouch.ObjectiveTest.IObjectiveTestModel;
import com.ln.himaxtouch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ln.himaxtouch.himax_config.mHXPath;


/**
 * Created by 903622 on 2018/6/7.
 */

public class DataMonitorModel implements IObjectiveTestModel {
    private static final String TAG = "[HXTP]DataMonitorModel";

    public BaseLayout mLayout;
    public RawdataLayout mRawDataLayout;

    public PresetRadioGroup mRawDataGroup;
    public ArrayList<PresetValueButton> mRawDataRadios = new ArrayList<PresetValueButton>();
    public PresetRadioGroup mTransformGroup;
    public ArrayList<PresetValueButton> mTransformRadios = new ArrayList<PresetValueButton>();
    public PresetRadioGroup mDataKeepGroup;
    public ArrayList<PresetValueButton> mDataKeepRadios = new ArrayList<PresetValueButton>();
    public PresetRadioGroup mColorOptionGroup;
    public ArrayList<PresetValueButton> mColorOptionRadios = new ArrayList<PresetValueButton>();
    public SeekBar mColorValueBar;
    public EditText mColorValueEdit;
    public SeekBar mFontValueBar;
    public EditText mFontValueEdit;
    public PresetRadioGroup mBackgroundGroup;
    public ArrayList<PresetValueButton> mBackgroundRadios = new ArrayList<PresetValueButton>();
    public Switch mRecordSwitch;
    public Switch mBlackSwitch;
    public Switch mDragSwitch;
    public CheckBox mCheckBP;
    public TextView mTextMaxDC;
    public Switch mAreaInfoSwitch;
    public Switch mOSRCCSwtich;

    public int[][] mFrame;
    public int[][] mPreviousFrame;
    public int[][] mAreaInfo;

    public String mRawDataString;
    public int mRow;
    public int mCol;
    public int mBackgroundViewId = 0;
    public boolean isStopped = true;
    public Context mActivityContext;

    //Setting Page Options From SharedPreference
    public List<String> mDiagOptionsKey;
    public List<String> mDiagOptionsValue;
    public List<String> mTransformOptionsKey;
    public List<String> mTransformOptionsValue;

    //Display options
    public int mColorType = DataMonitorConfig.COLOR_TYPE_ONE;

    //Data options
    public int mDiagOption = 0;
    public boolean isNeedReEcho = false;
    public int mTransformOption = 0;
    public int mDataKeepOption = DataMonitorConfig.DATA_KEEP_NORMAL;
    public int mColorDataMax = 1000;
    public int mColorBarMax = 10000;
    public int mFontSize = 6;
    public int mBackgroundOption = DataMonitorConfig.SCREEN_MODE_PARSED_DATA;
    public boolean isNeedReDrawBackgound = true;
    //Record options
    public boolean mRecordSwitchValue = false;
    public ArrayList<String> mFoundLogs = new ArrayList<String>();
    public String mCSVLogText;
    public String mPathToWrite;

    public String mBackgroundRecordInfo = null;

    public long mMaxDCValueForBP = 1;
    public boolean mOSR_CC = false;

    public boolean isShowInfo = false;

    public Notification.Builder mNotification;
    public final static int NOTIFY_ID = 55;
    public NotificationManager mNotificationManager;

    public final static String ACTION_RECORD_START = "com.ln.himaxtouch.DataMonitor.RecordService.Start";
    public final static String ACTION_RECORD_FINISH = "com.ln.himaxtouch.DataMonitor.RecordService.Finish";

    public DataMonitorModel(BaseLayout layout, Context context) {
        mLayout = layout;
        mActivityContext = context;

        mDiagOptionsKey = new ArrayList<String>();
        mDiagOptionsValue = new ArrayList<String>();
        mTransformOptionsKey = new ArrayList<String>();
        mTransformOptionsValue = new ArrayList<String>();
    }

    public void setupNotifcation(Context context, int diag, int min, int sec, boolean isNeedStartPending) {
        mNotification = null;
        Intent intentStart = new Intent(context, HimaxApplication.RecordServiceReceiver.class);
        intentStart.setAction(ACTION_RECORD_START);
        int[] param1 = new int[4];
        param1[0] = diag;
        param1[1] = mTransformOption;
        param1[2] = mDataKeepOption;
        param1[3] = ((min * 60) + sec);
        intentStart.putExtra("record", param1);

        Intent intentStop = new Intent(context, HimaxApplication.RecordServiceReceiver.class);
        intentStop.setAction(ACTION_RECORD_FINISH);

        StringBuilder sb = new StringBuilder();

        if (mBackgroundRecordInfo == null) {
            sb.append("1.");
            sb.append(mDiagOptionsKey.get(mDiagOptionsValue.indexOf(diag + "")));
            sb.append("\n");
            sb.append("2.");
            sb.append(mTransformOptionsKey.get(mTransformOption) + " " + mTransformOptionsValue.get(mTransformOption));
            sb.append("\n");
            sb.append("3.");
            sb.append(((PresetValueButton) mDataKeepGroup.getChildAt(mDataKeepOption)).mValue);
            sb.append(" ");
            sb.append(((PresetValueButton) mDataKeepGroup.getChildAt(mDataKeepOption)).mUnit);
            sb.append("\n");
            sb.append("4.");
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            mPathToWrite = mHXPath + mDiagOptionsKey.get(mDiagOptionsValue.indexOf(diag + "")) + "_" + sdf.format(currentTime) + ".csv";
            sb.append(mPathToWrite);
            sb.append("\n");
            if (min == 0 && sec == 0) {
                sb.append("5.");
                sb.append("recording time: non-stop");
            } else {
                sb.append("5.");
                sb.append("recording time: ");
                sb.append(min);
                sb.append(" min ");
                sb.append(sec);
                sb.append(" sec");
            }
            mBackgroundRecordInfo = sb.toString();
            Log.d(TAG, "if " + mBackgroundRecordInfo);
        } else {
            Log.d(TAG, "else " + mBackgroundRecordInfo);
            sb.append(mBackgroundRecordInfo);
        }


        PendingIntent pendingStart = PendingIntent.getBroadcast(context, 0, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingStop = PendingIntent.getBroadcast(context, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);
        String id = "channel_1";
        String description = "143";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(id, description, importance);
        channel.enableLights(true);
        channel.enableVibration(true);
        mNotification = new Notification.Builder(mActivityContext, id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle("Record Raw Data Service")
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setStyle(new Notification.BigTextStyle().bigText(sb.toString()));
        if (isNeedStartPending) {
            mNotification.addAction(R.drawable.custom_btn_hint_blue, "start (delay 3 secs)", pendingStart);
        } else {
            mNotification.setProgress(100, 0, false);
        }
        mNotification.addAction(R.drawable.custom_btn_hint_blue, "finish", pendingStop);

        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        }


    }

    @Override
    public String getRecordStringToSaveCsv() {
        return null;
    }

    @Override
    public void unbindViews() {
        if (mLayout != null) {
            View background = mLayout.findViewById(mBackgroundViewId);
            if (background != null) {
                mLayout.removeView(background);
                mLayout.removeView(mRawDataLayout);
            }
        }
        mLayout = null;
        mRawDataLayout = null;


        mRawDataGroup.removeAllViews();
        mRawDataGroup.clearCheck();
        mRawDataGroup = null;
        mTransformGroup.removeAllViews();
        mTransformGroup.clearCheck();
        mTransformGroup = null;
        mDataKeepGroup.removeAllViews();
        mDataKeepGroup.clearCheck();
        mDataKeepGroup = null;
        mColorOptionGroup.removeAllViews();
        ;
        mColorOptionGroup.clearCheck();
        mColorOptionGroup = null;
        mColorValueBar = null;
        mColorValueEdit = null;
        mFontValueBar = null;
        mFontValueEdit = null;
        mBackgroundGroup.removeAllViews();
        mBackgroundGroup.clearCheck();
        mBackgroundGroup = null;
        mRecordSwitch = null;

        mRawDataRadios.clear();
        mTransformRadios.clear();
        mDataKeepRadios.clear();
        mBackgroundRadios.clear();
        mColorOptionRadios.clear();
    }

    @Override
    public void clearTestData() {
        mRow = 0;
        mCol = 0;
        mFrame = null;
        mPreviousFrame = null;
//        mActivityContext = null;
        isStopped = true;
        mBackgroundViewId = 0;
        mDiagOption = 0;
        mColorType = 0;
        isNeedReEcho = false;
        isNeedReDrawBackgound = true;
    }

    public void reloadRadios() {
        mRawDataGroup.removeAllViews();
        mTransformGroup.removeAllViews();
        mTransformGroup.clearCheck();
        mDataKeepGroup.removeAllViews();
        mColorOptionGroup.removeAllViews();
        mBackgroundGroup.removeAllViews();
        mRawDataRadios.clear();
        mColorOptionRadios.clear();
        mTransformRadios.clear();
        mDataKeepRadios.clear();
        mColorOptionRadios.clear();
        mBackgroundRadios.clear();
        mDiagOptionsKey.clear();
        mDiagOptionsValue.clear();
        mTransformOptionsKey.clear();
        mTransformOptionsValue.clear();
    }

    public void clearBackgroundView() {
        mLayout.removeAllViews();
        mRawDataLayout = null;
    }


}
