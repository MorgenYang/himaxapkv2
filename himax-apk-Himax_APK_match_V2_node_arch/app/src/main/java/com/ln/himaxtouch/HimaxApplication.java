package com.ln.himaxtouch;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ln.himaxtouch.DataMonitor.RecordRawDataService;
import com.ln.himaxtouch.ICInfo.ICData;
import com.ln.himaxtouch.ObjectiveTest.ObjectiveTestController;
import com.ln.himaxtouch.RawdataRecord.CSVAccess;
import com.ln.himaxtouch.TouchTest.CsvEditor;

import static com.ln.himaxtouch.DataMonitor.DataMonitorModel.ACTION_RECORD_START;
import static com.ln.himaxtouch.DataMonitor.DataMonitorModel.NOTIFY_ID;
import static com.ln.himaxtouch.DataMonitor.RecordRawDataService.MSG_RECORD_END;
import static com.ln.himaxtouch.DataMonitor.RecordRawDataService.MSG_RECORD_START;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD;

/**
 * Created by 902995 on 2018/2/1.
 */

public class HimaxApplication extends Application {

    private final static boolean DEBUG = true;
    private final static String TAG = "[HXTP]HimaxApplication";
    /**
     *MainThread_MSG_AREA
     */
    public final static int MSG_UPDATE_SNR_PROGRESSBAR = 0;
    public final static int MSG_UPDATE_FILE_SAVED_RESULT = 1;
    public final static int MSG_UPDATE_PSENSOR_DETAIL_INFO = 2;
    public final static int MSG_UPDATE_DATA_MONITOR_UI = 3;
    public final static int MSG_UPDATE_DATA_MONITOR_SETTINGS_PAGE = 4;
    public final static int MSG_SHOW_FOUND_MONITOR_LOGS_ALERT = 5;
    public final static int MSG_UPDATE_MONITOR_CSV_LOG_CONTENT = 6;
    public final static int MSG_CANCEL_RECORDING_NOTIFICATION = 7;
    public final static int MSG_UPDATE_NOTIFICATION_PROGRESS = 8;
    public final static int MSG_START_OSR_CC_PROCESS = 9;
    public final static int MSG_END_OSR_CC_PROCESS = 10;

    /**
     *WorkThread_MSG_AREA
     */
    public final static int MSG_COLLECT_UNTOUCHED_NOISE = 0;
    public final static int MSG_COLLECT_TOUCHED_NOISE = 1;
    public final static int MSG_SNR_FINISH = 2;
    public final static int MSG_SAVE_JITTER_TEST = 3;
    public final static int MSG_SAVE_MAX_POINT_COUNT_RECORD = 4;
    public final static int MSG_SAVE_GHOST_POINT_RECORD = 5;
    public final static int MSG_SAVE_PALM_TEST = 6;
    public final static int MSG_SAVE_CSV_BOARD_PROTECTION = 7;
    public final static int MSG_SAVE_CSV_EXTEND_BOARD = 8;
    public final static int MSG_START_P_SENSOR_TEST = 9;
    public final static int MSG_DATA_MONITOR_START = 10;
    public final static int MSG_DATA_MONITOR_RELOAD_SETTINGS = 11;
    public final static int MSG_DATA_MONITOR_SET_TRANSFORM = 12;
    public final static int MSG_DATA_START_RECORD_RAW_DATA = 13;
    public final static int MSG_DATA_FINISH_RECORD_RAW_DATA = 14;
    public final static int MSG_DATA_MONIOTR_SENSING_ON_OFF = 15;
    public final static int MSG_DATA_MONITOR_FIND_CSV_LOG = 16;
    public final static int MSG_DATA_OPEN_CSV_LOG = 17;
    public final static int MSG_SWITCH_OSC_CC = 18;

    private HandlerThread mWorkerThread;
    public static WorkerHandler mWorkerHandler;
    public static MainHandler mMainHandler = new MainHandler();
    public static ObjectiveTestController mObjectiveTestController;
    public static Context mContext;
    public static Messenger mRecordServiceMessenger;
    public static NodeDataSource mNodeAcc;
    public static ICData mICData;

    public static class WorkerHandler extends Handler {
        WorkerHandler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case MSG_COLLECT_UNTOUCHED_NOISE: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.mSnrModel.mCSV = new CSVAccess();
                    mObjectiveTestController.mSnrModel.mCSV.newAnotherFileName(mContext);
                    mObjectiveTestController.collectUnTouchedNoise(mMainHandler, mNodeAcc);
                }
                    break;
                case MSG_COLLECT_TOUCHED_NOISE: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.collectTouchedNoise(mMainHandler, mNodeAcc);
                }
                    break;
                case MSG_SNR_FINISH: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.collectSNRFinish(mMainHandler, mNodeAcc);
                }
                break;
                case MSG_SAVE_JITTER_TEST: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.saveJitterTestToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_jitter_test), mMainHandler);
                    break;
                }
                case MSG_SAVE_MAX_POINT_COUNT_RECORD: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.saveMaxPointDataToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_record_max_point_count), mMainHandler);
                    break;
                }
                case MSG_SAVE_GHOST_POINT_RECORD: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.saveGhostPointDataToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_ghost_point_record), mMainHandler);
                    break;
                }
                case MSG_SAVE_PALM_TEST: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.savePalmTestDataToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_palm_test), mMainHandler);
                    break;
                }
                case MSG_SAVE_CSV_BOARD_PROTECTION: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.saveBoardProtectionDataToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_board_protection), mMainHandler, OBJECTIVE_TEST_BORAD_PROTECTION);
                    break;
                }
                case MSG_SAVE_CSV_EXTEND_BOARD: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.saveBoardProtectionDataToCsv(new CsvEditor(mContext),
                            mContext.getResources().getString(R.string.objective_extend_board), mMainHandler, OBJECTIVE_TEST_EXTEND_BORARD);
                    break;
                }
                case MSG_START_P_SENSOR_TEST: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.startPSensorTest(mMainHandler, mNodeAcc);
                } break;
                case MSG_DATA_MONITOR_START: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.collectMonitorDataNonStop(mMainHandler, mNodeAcc);
                } break;
                case MSG_DATA_MONITOR_RELOAD_SETTINGS: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.setupSettingsData(mMainHandler, mNodeAcc,(msg.arg1==0));
                } break;
                case MSG_DATA_MONITOR_SET_TRANSFORM: {
                    checkObjTestCtrExisted();
                    Log.e(TAG, String.format("Now tans:%d background value %d", msg.arg1, msg.arg2));
//                    mObjectiveTestController.setTransformInDiagArr(new NodeDataSource(mContext), msg.arg1);
                    if( msg.arg2 == 1) {
                        /* diag arr*/

                        mObjectiveTestController.setTransformInDiagArr(mNodeAcc, msg.arg1);
                    } else {
                        mObjectiveTestController.setTransformBySoftware(msg.arg1);
                    }
                    mWorkerHandler.sendEmptyMessage(MSG_DATA_MONITOR_START);
                } break;
                case MSG_DATA_START_RECORD_RAW_DATA: {
                    checkObjTestCtrExisted();
                    if(mRecordServiceMessenger != null) {
                        Message m = Message.obtain();
                        Bundle b = new Bundle(msg.getData());
                        m.setData(b);
                        m.what = MSG_RECORD_START;
                        try {
                            mRecordServiceMessenger.send(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mObjectiveTestController.disableAllSettings();
                } break;
                case MSG_DATA_FINISH_RECORD_RAW_DATA: {
                    checkObjTestCtrExisted();
                    if(mRecordServiceMessenger != null) {
                        Message m = Message.obtain();
                        Bundle b = new Bundle(msg.getData());
                        m.setData(b);
                        m.what = MSG_RECORD_END;
                        try {
                            mRecordServiceMessenger.send(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mObjectiveTestController.resetAllMonitorSettingsValue();
                    mObjectiveTestController.enableAllSettings();
                } break;
                case MSG_DATA_MONIOTR_SENSING_ON_OFF: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.sensingOnAndOff(mNodeAcc);
                } break;
                case MSG_DATA_MONITOR_FIND_CSV_LOG: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.findAllCSVLog(mMainHandler);
                } break;
                case MSG_DATA_OPEN_CSV_LOG: {
                    checkObjTestCtrExisted();
                    Bundle b = msg.getData();
                    String name = b.getString("file");
                    mObjectiveTestController.readCSVFile(mMainHandler, name);
                } break;
                case MSG_SWITCH_OSC_CC: {
                    checkObjTestCtrExisted();
                    mObjectiveTestController.switchOSRCC(mNodeAcc);
                    mMainHandler.sendEmptyMessage(MSG_END_OSR_CC_PROCESS);
                }
                default:
                    break;
            }
        }
    }
    public static class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case MSG_UPDATE_SNR_PROGRESSBAR: {
                    mObjectiveTestController.updateSNRProgressBar();
                    break;
                }
                case MSG_UPDATE_FILE_SAVED_RESULT: {
                    boolean result = (boolean) msg.obj;
                    int type = msg.arg1;
                    mObjectiveTestController.updateFileSavedResult(result, type, mContext);
                    break;
                }
                case MSG_UPDATE_PSENSOR_DETAIL_INFO: {
                    mObjectiveTestController.updatePSensorInfo();
                } break;
                case MSG_UPDATE_DATA_MONITOR_UI: {
                    mObjectiveTestController.updateDataMonitorUI();
                } break;
                case MSG_UPDATE_DATA_MONITOR_SETTINGS_PAGE: {
                    mObjectiveTestController.updateMonitorSettingPage(mWorkerHandler);
                } break;
                case MSG_SHOW_FOUND_MONITOR_LOGS_ALERT: {
                    mObjectiveTestController.showFoundLogsDialog(mWorkerHandler);
                } break;
                case MSG_UPDATE_MONITOR_CSV_LOG_CONTENT: {
                    mObjectiveTestController.updateCSVLogUI();
                } break;
                case MSG_CANCEL_RECORDING_NOTIFICATION: {
                    /* If cancel the recording notification, it also mean cancel the work of recording rawdata in the back ground*/
                    mObjectiveTestController.cancelRecordNotification();
                    if(mRecordServiceMessenger != null) {
                        Message m = Message.obtain();
                        Bundle b = new Bundle(msg.getData());
                        m.setData(b);
                        m.what = MSG_RECORD_END;
                        try {
                            mRecordServiceMessenger.send(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } break;
                case MSG_UPDATE_NOTIFICATION_PROGRESS: {
                    mObjectiveTestController.updateRecordNotificationProgress(msg.arg1, 100);
                } break;
                case MSG_START_OSR_CC_PROCESS: {
                    //show dialog
                    mObjectiveTestController.showProcessingDialog();

                    mWorkerHandler.sendEmptyMessage(MSG_SWITCH_OSC_CC);
                } break;
                case MSG_END_OSR_CC_PROCESS: {
                    //dismiss dialog
                    mObjectiveTestController.dismissProcessingDialog();
                } break;
                default:
                    break;
            }
        }
    }

    private static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRecordServiceMessenger = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if(mWorkerThread == null) {
            mWorkerThread = new HandlerThread("WorkerThread");
            mWorkerThread.start();
        }
        mWorkerHandler = new WorkerHandler(mWorkerThread.getLooper());
        mContext = getApplicationContext();

        Intent intent = new Intent(mContext, RecordRawDataService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mNodeAcc = new NodeDataSource(mContext);

        if(mICData == null) {
            mICData = new ICData(mContext);
        }

        checkObjTestCtrExisted();
    }

    private static void checkObjTestCtrExisted() {
        if (mObjectiveTestController == null) {
            mObjectiveTestController = new ObjectiveTestController();
        }
    }

    public ObjectiveTestController getObjectiveTestController() {
        if (mObjectiveTestController == null) {
            mObjectiveTestController = new ObjectiveTestController();
        }
        return mObjectiveTestController;
    }


    public static class RecordServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_RECORD_START.equals(intent.getAction())) {

                int[] params = intent.getIntArrayExtra("record");

                if(mObjectiveTestController.mDataMonitorModel.mNotification != null) {
                    mObjectiveTestController.mDataMonitorModel.mNotificationManager.cancel(NOTIFY_ID);
                    mObjectiveTestController.mDataMonitorModel.setupNotifcation(mObjectiveTestController.mDataMonitorModel.mActivityContext,
                            params[0], params[3]/60, params[3]%60, false);
                    mObjectiveTestController.mDataMonitorModel.mNotificationManager.notify(NOTIFY_ID, mObjectiveTestController.mDataMonitorModel.mNotification.build());

                    Message msg = Message.obtain();
                    msg.what = MSG_DATA_START_RECORD_RAW_DATA;
                    Bundle b = new Bundle();
                    b.putIntArray("record", params);
                    b.putString("path", mObjectiveTestController.mDataMonitorModel.mPathToWrite);
                    msg.setData(b);
                    mWorkerHandler.sendMessageDelayed(msg, 3000);
                } else {
                    NotificationManager m =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    m.cancel(NOTIFY_ID);
                }

            } else {
                if(mObjectiveTestController.mDataMonitorModel != null) {
                    mWorkerHandler.sendEmptyMessage(MSG_DATA_FINISH_RECORD_RAW_DATA);
                }
                NotificationManager m =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                m.cancel(NOTIFY_ID);
            }
        }
    }
}
