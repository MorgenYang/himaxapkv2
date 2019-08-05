package com.ln.himaxtouch.TouchTest;

import android.content.Context;
import android.util.Log;

import com.ln.himaxtouch.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 903622 on 2018/4/17.
 */

public class CsvEditor {

    private final static String TAG = "CsvEditor";
    private final static boolean DEBUG = true;
    private Context mContext;

    public CsvEditor(Context context) {
        mContext = context;
    }

    public String createNewHeader(String test_item, long duration_sec, long start_sec, long end_sec, boolean result) {
        DateFormat formatterD = new SimpleDateFormat("mm:ss");
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getResources().getString(R.string.objective_csv_test_item));
        sb.append(",");
        sb.append(test_item);
        sb.append("\n");
        sb.append(mContext.getResources().getString(R.string.objective_csv_test_duration));
        sb.append(",");
        sb.append(formatterD.format(new Date(duration_sec)));
        sb.append("\n");
        sb.append(mContext.getResources().getString(R.string.objective_csv_test_start_time));
        sb.append(",");
        sb.append(formatter.format(new Date(start_sec)));
        sb.append("\n");
        sb.append(mContext.getResources().getString(R.string.objective_csv_test_end_time));
        sb.append(",");
        sb.append(formatter.format(new Date(end_sec)));
        sb.append("\n");
        sb.append(mContext.getResources().getString(R.string.objective_csv_test_result));
        sb.append(",");
        sb.append(result);
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    private boolean isFileExist(String file_path) {
        File f = null;
        try {
            f = new File(file_path);
            return f.exists();
        } catch (Exception e) {
            return false;
        }
    }

    public String transferRecordToString(ArrayList<float[]> records) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        sb.append("NUM,X,Y\n");
        for(float[] record: records) {
            sb.append(index);
            sb.append(",");
            sb.append(record[0]);
            sb.append(",");
            sb.append(record[1]);
            sb.append("\n");
            index++;
        }
        return sb.toString();
    }

    public boolean appendStringToFile(String input, String file_path) {
        FileWriter fw = null;
        boolean isNeedAppend = isFileExist(file_path);
        try {
            fw = new FileWriter(file_path, isNeedAppend);
            fw.append(input);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.fillInStackTrace();
            return false;
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.fillInStackTrace();
                return false;
            }
        }
        return true;
    }
}
