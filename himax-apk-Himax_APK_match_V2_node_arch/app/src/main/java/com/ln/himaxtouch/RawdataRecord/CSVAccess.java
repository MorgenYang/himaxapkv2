package com.ln.himaxtouch.RawdataRecord;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.ln.himaxtouch.himax_config.mHXPath;
import static java.lang.Math.abs;

/**
 * Created by eiNim on 2017/10/26.
 */

public class CSVAccess
{
    private static final String TAG = "[HXTP]CSVAccess";
    public CSVAccess() {
        File dir = new File(mHXPath);
        if(!(dir.exists() && dir.isDirectory())) {
            dir.mkdir();
        }
    }
    public void append_line(String file_path, String data) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file_path, true);
            fw.append(data);
            Log.d(TAG, file_path+data);
        } catch (Exception e) {
            e.fillInStackTrace();
            Log.e(TAG, e.toString());
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.fillInStackTrace();
                Log.e(TAG, e.toString());
            }
        }
    }

    public void write_line(String file_path,String data)
    {
       try {
           FileWriter fw = new FileWriter(file_path, false);
           BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
           bw.write(data);
           bw.newLine();
           bw.close();
       }
       catch (Exception e)
       {

       }
    }
    public void write_doc(String file_path,String data[])
    {
        for(int i = 0;i < data.length;i++)
            this.write_line(file_path,data[i]);
    }

    //BEGIN:Steve_Ke
    public int mMax = -100000;
    public int mMin = 100000;
    public boolean mFail = false;
    public void resetValue() {
        mMax = -100000;
        mMin = 100000;
        mFail = false;
    }

    public long mCurrentSec = Calendar.getInstance().getTimeInMillis();
    public String mFileName;
    public void newAnotherFileName(Context context) {
        mCurrentSec = Calendar.getInstance().getTimeInMillis();
        //mFileName = context.getFilesDir() + "/f1_result_" + mCurrentSec + ".csv";
        File folder0 = new File("/sdcard/Android");
        if(!folder0.exists()) {
            folder0.mkdir();
        }
        File folder = new File("/sdcard/Android/data");
        if(!folder.exists()) {
            folder.mkdir();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyymmddHHmmss");
        mCurrentSec = Calendar.getInstance().getTimeInMillis();
//        mFileName = context.getFilesDir() + "/f1_result_" + mCurrentSec + ".csv";
        mFileName = "/sdcard/Android/data/SNR_"+format.format(mCurrentSec)+"_all.csv";
//        mFailFileName = "/sdcard/Android/data/com.touchscreen.tptest/noisetest_"+mCurrentSec+"_fail.csv";
    }
    public void newAnotherFileName(Context context,String raw) {
        mCurrentSec = Calendar.getInstance().getTimeInMillis();
        mFileName = context.getFilesDir() + "/f1_result_"+raw + mCurrentSec + ".txt";
    }

    public int appendRawData(int type, int frameNum, int[][] raw) {
        StringBuilder s = new StringBuilder();
        for(int i=0; i<raw.length; i++) {
            for(int j=0; j<raw[i].length; j++) {
                s.append(raw[i][j]);
                s.append(", ");
            }
            s.append("\n");
        }
        return appendRawData(type, frameNum, s.toString(), 10000, false);
    }

    public int appendString(String str) {
        int status = 0;
        FileWriter fw = null;
        try {
            fw = new FileWriter(mFileName, true);
            fw.append(str+"\n");

        } catch(Exception e) {
            Log.d("[HXTP]Error", e.fillInStackTrace().toString());
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.d("[HXTP]Error", e.fillInStackTrace().toString());
                e.fillInStackTrace();
            }
        }

        return status;
    }

    public int appendRawData(int type, int frameNum, String rawData, int passStandard, boolean isTransform) {
        mFail = false;
        int status = 0;
        int headDummy = 3;
        int tailDummy = 2;
        FileWriter fw = null;

        try {
            fw = new FileWriter(mFileName, true);
            switch(type) {
                case 0: {
                    fw.append("Noise, Frame"+(frameNum+1)+"\n");
                    fw.append(rawData);
                } break;
                case 1: {
                    fw.append("Signal, Frame"+(frameNum+1)+"\n");
                    fw.append(rawData);
                } break;
                case 2: {
                    fw.append("BaseFrame, Frame"+(frameNum+1)+"\n");
                    fw.append(rawData);
                } break;
                case 3: {
                    fw.append("BaseFrame Average\n");
                    fw.append(rawData);
                } break;
                case 4: {
                    fw.append("(Noise - BaseFrame Average)^2, Frame"+(frameNum+1)+"\n");
                    fw.append(rawData);
                } break;
                case 5: {
                    fw.append("Noise value - getAverage of sigma, Frame"+(frameNum+1)+".\n");
                    fw.append(rawData);
                } break;
                case 6: {
                    fw.append("Signal Over Threshold Value, Frame"+(frameNum+1)+"\n");
                    fw.append(rawData);
                 } break;
                case 7: {
                    fw.append("Frame SKIP:"+(frameNum+1)+"\n");
                    fw.append(rawData);
                } break;
                default:
                    break;
            }


            fw.append("\n");
//            String[] rows = rawData.split("\n");
//            if(rows.length <= 0) {
//                return 1;
//            }
//            String rowcol = rows[0].split(":")[1].replaceAll("\\s+","");
//            int rowNum = Integer.valueOf(rowcol.split(",")[0]);
//            int colNum = Integer.valueOf(rowcol.split(",")[1]);
//            if (isTransform) {
//                int temp = rowNum;
//                rowNum = colNum;
//                colNum = temp;
//            }
//            for (int i = headDummy; i < (headDummy+colNum); i++) {
//                String perRowData;
//                String[] tempRow = rows[i].split("]");
//                String tempRow2 = tempRow[1].replaceAll(" 0", " 0 ");
//                perRowData = tempRow2.replaceAll("\\s+",",");
//                perRowData = perRowData.substring(1, perRowData.length());
//                fw.append(perRowData);
//                findMaxnMin(perRowData, passStandard);
//                fw.append("\n");
//            }


            /*for(int i=colNum+headDummy+tailDummy; i<rows.length; i++) {
                fw.append(rows[i]);
                fw.append("\n");
            }*/
//            String last = rows[rows.length-1];
//            fw.append(last.replace("Self", ""));
//            fw.append((mFail)?",FAIL":",PASS");
//            fw.append("\n");
//            fw.append("MAX:"+mMax+",MIN:"+mMin);
//            fw.append("\n");
            fw.append("\n");
            fw.append("\n");

        } catch (Exception e) {
            Log.d("Error", e.fillInStackTrace().toString());
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.d("Error", e.fillInStackTrace().toString());
                e.fillInStackTrace();
            }
        }
//        if(mFail) {
//            appendRawDataFail(freq, frameNum, rawData, passStandard, isTransform);
//        }
        return status;
    }

    public int appendRawData1(double freq, int frameNum, String rawData, int passStandard, boolean isTransform) {
        mFail = false;
        int status = 0;
        int headDummy = 3;
        int tailDummy = 2;
        FileWriter fw = null;

        try {
            fw = new FileWriter(mFileName, true);
            fw.append("Frequency,"+freq+",Frame"+(frameNum+1)+"\n");
            String[] rows = rawData.split("\n");
            if(rows.length <= 0) {
                return 1;
            }
            String rowcol = rows[0].split(":")[1].replaceAll("\\s+","");
            int rowNum = Integer.valueOf(rowcol.split(",")[0]);
            int colNum = Integer.valueOf(rowcol.split(",")[1]);
            if (isTransform) {
                int temp = rowNum;
                rowNum = colNum;
                colNum = temp;
            }
            for (int i = headDummy; i < (headDummy+colNum); i++) {
                String perRowData;
                String[] tempRow = rows[i].split("]");
                String tempRow2 = tempRow[1].replaceAll(" 0", " 0 ");
                perRowData = tempRow2.replaceAll("\\s+",",");
                perRowData = perRowData.substring(1, perRowData.length());
                fw.append(perRowData);
                findMaxnMin(perRowData, passStandard);
                fw.append("\n");
            }

            /*for(int i=colNum+headDummy+tailDummy; i<rows.length; i++) {
                fw.append(rows[i]);
                fw.append("\n");
            }*/
            String last = rows[rows.length-1];
            fw.append(last.replace("Self", ""));
            fw.append((mFail)?",FAIL":",PASS");
            fw.append("\n");

            fw.append("\n");
            fw.append("\n");

        } catch (Exception e) {
            Log.d("Error", e.fillInStackTrace().toString());
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.d("Error", e.fillInStackTrace().toString());
                e.fillInStackTrace();
            }
        }
        return status;
    }
    private void findMaxnMin(String data, int passStandard) {
        String[] strArray = data.split(",");
        int[] intArray = new int[strArray.length];
        for(int i=0; i<intArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
            if(mMax<intArray[i]) {
                mMax = intArray[i];
            }
            if(mMin>intArray[i]) {
                mMin = intArray[i];
            }
            if(abs(intArray[i]) > passStandard) {
                mFail|=true;
            }
        }
    }

    public String mFileNameFTwo;
    public void changeAnotherFileTwo(Context context) {

    }
    public int[][] parseValueNappendFile(Context context, double freq, int frameNum, String rawData,
                                         boolean isNeedOverride, boolean isTransform) {

        int[][] data = null;
        int headDummy = 3;
        int tailDummy = 2;

        int dummyCol = 1;

        FileWriter fw = null;

        if (isNeedOverride) {
            mCurrentSec = Calendar.getInstance().getTimeInMillis();
            mFileNameFTwo = context.getFilesDir() + "/f2_result_" + mCurrentSec + ".csv";
        }

        try {
            fw = new FileWriter(mFileNameFTwo, !isNeedOverride);
            fw.append("Frequency,"+freq+",Frame"+(frameNum+1)+"\n");


            String[] rows = rawData.split("\n");
            if(rows.length <= 0) {
                return null;
            }
            String rowcol = rows[0].split(":")[1].replaceAll("\\s+","");
            int rowNum = Integer.valueOf(rowcol.split(",")[0]);
            int colNum = Integer.valueOf(rowcol.split(",")[1]);
            if (isTransform) {
                int temp = rowNum;
                rowNum = colNum;
                colNum = temp;
            }
            data = new int [colNum][rowNum];
            for (int i = headDummy; i < (headDummy+colNum); i++) {
                String perRowData;
                String[] tempRow = rows[i].split("]");
                String tempRow2 = tempRow[1].replaceAll(" 0", " 0 ");
                perRowData = tempRow2.replaceAll("\\s+",",");
                perRowData = perRowData.substring(1, perRowData.length());
                fw.append(perRowData);
                fw.append("\n");

                String[] rowTemp = perRowData.split(",");
                for (int j=0; j<rowTemp.length-dummyCol; j++) {
                    data[i-headDummy][j] = Integer.valueOf(rowTemp[j]);
                }
            }
            fw.append("\n");
            fw.append("\n");

        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
        return (data != null) ? data : null;
    }
    public void appedMaxMinDIff(Double freq, int[][] maxData, int[][] minData, int[][] diffData) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(mFileNameFTwo, true);
            fw.append("\n");
            fw.append("Freq(KHZ):,"+freq+",");
            fw.append("Max/Min/diff Data :");
            fw.append("\n");
            StringBuilder sb = new StringBuilder();
            int rowSize = maxData.length;
            int colSize = maxData[0].length;
            for(int row=0; row<rowSize; row++) {
                for(int col=0; col<colSize; col++) {
                    sb.append(maxData[row][col]);
                    sb.append("/");
                    sb.append(minData[row][col]);
                    sb.append("/");
                    sb.append(diffData[row][col]);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append("\n");
            }
            fw.append(sb.toString());

        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }
    public int appendRawData2(double freq, int frameNum, String rawData, int passStandard, boolean isTransform) {
        mFail = false;
        int status = 0;
        int headDummy = 3;
        int tailDummy = 2;
        FileWriter fw = null;

        try {
            fw = new FileWriter(mFileName, true);
            fw.append("Frequency,"+freq+",Frame"+(frameNum+1)+"\n");
            Log.d("HXTP", "Frequency,"+freq+",Frame"+(frameNum+1));
            Log.d("HXTP", rawData);
            String[] rows = rawData.split("\n");
            if(rows.length <= 0) {
                return 1;
            }



            /*for(int i=colNum+headDummy+tailDummy; i<rows.length; i++) {
                fw.append(rows[i]);
                fw.append("\n");
            }*/

            fw.append(rawData);

            fw.append("\n");
            fw.append("\n");

        } catch (Exception e) {
            Log.d("Error", e.fillInStackTrace().toString());
            e.fillInStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.d("Error", e.fillInStackTrace().toString());
                e.fillInStackTrace();
            }
        }
        return status;
    }
    //END:Steve_Ke
}
