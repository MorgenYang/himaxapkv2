package com.ln.himaxtouch.DataMonitor;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.NodeDataSource;
import com.ln.himaxtouch.ObjectiveTest.ObjectiveTestController;
import com.ln.himaxtouch.RawdataRecord.CSVAccess;

import static com.ln.himaxtouch.HimaxApplication.MSG_CANCEL_RECORDING_NOTIFICATION;
import static com.ln.himaxtouch.HimaxApplication.MSG_UPDATE_NOTIFICATION_PROGRESS;
import static com.ln.himaxtouch.HimaxApplication.mMainHandler;

/**
 * Created by 903622 on 2018/6/14.
 */

public class RecordRawDataService extends Service {
    private static final String TAG="[HXTP]RecordRawServ";

    private final Messenger mMesseneger = new Messenger(new MessengerHandler());

   // private HimaxApplication mApplication;
    private static ObjectiveTestController mController;

    public final static int MSG_RECORD_START = 0;
    public final static int MSG_RECORD_END = 1;
    private static int mDiagValue;
    private static int mDiagArrValue;
    private static int mDataKeepOption;
    private static int mRecordingSecs;
    private static String mWritePath;

    public static HandlerThread mThread = new HandlerThread("backgorund_record");
    public static NodeDataSource mDataSource;
    public static CSVAccess mCSV;
    public static Handler mBackgroundHandler;
    private static  boolean isKeepCatch = false;
    public static int[][] mFrame;
    public static int[][] mPreviousFrame;

    private static HimaxApplication mApplication;

    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //get Channels
            int[] channels = new int[2];
            mDataSource.getRawDataRowAndColumn(channels, true);
            int row = channels[0];
            int col = channels[1];

            boolean isFirstTime = true;
            final int DROP_FRAME_NUM = 20;
            int drop = 0; /* didn't drop any frames... original value is DROP_FRAME_NUM*/
            boolean diagResult;

            long beginmSecs = System.currentTimeMillis();

            while(isKeepCatch) {
                //catch data and save file.
                int[][] frame = new int[row][col];
                StringBuilder raw = new StringBuilder();
                diagResult = mDataSource.readSpecificDiag(mDiagValue, frame, isFirstTime, raw, mDiagArrValue, true, null);

                if(drop==0) {

                    if (isFirstTime) {
                        isFirstTime = false;
                        mFrame = new int[row][col];
                        mPreviousFrame = new int[row][col];
                        if(mDataKeepOption != DataMonitorConfig.DATA_KEEP_DIFF_MAX) {
                            copyDataFromAtoB(frame, mFrame);
                        }
                        copyDataFromAtoB(frame, mPreviousFrame);
                    } else {
                        for (int i = 0; i < frame.length; i++) {
                            for (int j = 0; j < frame[0].length; j++) {
                                if(mFrame != null) {
                                    switch (mDataKeepOption) {
                                        case DataMonitorConfig.DATA_KEEP_NORMAL: {
                                            mFrame[i][j] = frame[i][j];
                                        } break;
                                        case DataMonitorConfig.DATA_KEEP_MAX: {
                                            if (frame[i][j] > mFrame[i][j]) {
                                                mFrame[i][j] = frame[i][j];
                                            }
                                        } break;
                                        case DataMonitorConfig.DATA_KEEP_MIN: {
                                            if (frame[i][j] < mFrame[i][j]) {
                                                mFrame[i][j] = frame[i][j];
                                            }
                                        } break;
                                        case DataMonitorConfig.DATA_KEEP_DIFF: {
                                            int diff = frame[i][j] - mPreviousFrame[i][j];
                                            mFrame[i][j] = Math.abs(diff);
                                            mPreviousFrame[i][j] = frame[i][j];
                                        } break;
                                        case DataMonitorConfig.DATA_KEEP_DIFF_MAX: {
                                            int diff = Math.abs(frame[i][j] - mPreviousFrame[i][j]);
                                            if (diff > mFrame[i][j]) {
                                                mFrame[i][j] = diff;
                                            }
                                            mPreviousFrame[i][j] = frame[i][j];
                                        } break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    if(diagResult) {
                        StringBuilder insertString = new StringBuilder();
                        transferDataToString(insertString, mFrame);
                        mCSV.append_line(mWritePath, insertString.toString());
                    } else {
                        mCSV.append_line(mWritePath, "<FrameStart.>\n");
                        mCSV.append_line(mWritePath, raw.toString());
                        mCSV.append_line(mWritePath, "<FrameEnd.>\n");
                    }
                } else {
                    drop--;
                }

                long endmSecs = System.currentTimeMillis();

                if(mRecordingSecs != 0 && (endmSecs-beginmSecs) > (mRecordingSecs*1000)) {
                    isKeepCatch = false;
                    diagResult = mDataSource.readSpecificDiag(0, frame, true, raw, true);
                    mDataSource.sensingOffAndOn();
                    mApplication.mMainHandler.sendEmptyMessage(MSG_CANCEL_RECORDING_NOTIFICATION);
                }
                if(mRecordingSecs != 0) {
                    int percentage = (int) ((endmSecs-beginmSecs)/(mRecordingSecs*10));
                    if(percentage % 10 == 1) {
                        Log.d("NIM190722", "if MSG_UPDATE_NOTIFICATION_PROGRESS:update");
                        Message m = Message.obtain();
                        m.what = MSG_UPDATE_NOTIFICATION_PROGRESS;
                        m.arg1 = percentage;
                        mMainHandler.sendMessage(m);
                    }
                }

             }
        }
    };


    private static void transferDataToString(StringBuilder sb, int[][] frame) {
        sb.append("Parsed Data\n");
        sb.append("Start\n");
        for(int i=0; i<frame.length; i++) {
            for(int j=0; j<frame[0].length; j++) {
                sb.append(frame[i][j]);
                if(j < frame[0].length-1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        sb.append("End\n");
        sb.append("\n");
    }

    private static void copyDataFromAtoB(int[][] a, int[][] b) {
        for(int i=0; i<a.length; i++) {
            for(int j=0; j<a[0].length; j++) {
                b[i][j] = a[i][j];
            }
        }
    }

    public static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECORD_START: {
                    Bundle b = msg.getData();
                    int[] params = b.getIntArray("record");
                    mWritePath = b.getString("path");
                    Log.d(TAG, "path:"+mWritePath);
                    mDiagValue = params[0];
                    mDiagArrValue = params[1];
                    mDataKeepOption = params[2];
                    mRecordingSecs = params[3];
                    isKeepCatch = true;
                    mBackgroundHandler.post(mRunnable);
                } break;
                case MSG_RECORD_END: {
                    isKeepCatch = false;
                    mController.mDataMonitorModel.mBackgroundRecordInfo = null;
                } break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThread.start();
        mBackgroundHandler = new Handler(mThread.getLooper());
        mDataSource = new NodeDataSource(this);
        mCSV = new CSVAccess();
        if (mApplication == null) {
            mApplication = (HimaxApplication) getApplication();
        }
        if (mController == null) {
            mController = mApplication.getObjectiveTestController();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThread.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMesseneger.getBinder();
    }
}
