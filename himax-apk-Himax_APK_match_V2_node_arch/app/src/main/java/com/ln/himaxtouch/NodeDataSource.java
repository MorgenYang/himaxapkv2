package com.ln.himaxtouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ln.himaxtouch.DataMonitor.DataMonitorConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

import static com.ln.himaxtouch.HimaxApplication.mICData;
import static com.ln.himaxtouch.UpgradeFW.DUMP_PATH;

/**
 * Created by 903622 on 2018/3/30.
 */

public class NodeDataSource {

    private final static boolean DEBUG = true;
    private final static String TAG = "[HXTP]NodeDataSource";

    public static final String CMD_WRITE_STR = "w";
    public static final String CMD_READ_STR = "r";

    private int gDriverArchVal = -1;
    private String setup_driver_arch;
    private String setup_dir;
    public String setup_diag;
    private String setup_v2_diag_stack;
    private String setup_v2_diag_siir;
    private String setup_v2_diag_sdc;
    private String setup_v2_diag_sbank;

    private String setup_register;
    private String setup_debuglevel;
    private String setup_selftest;
    private String setup_reset;
    private String setup_flashdump;
    private String setup_debug;
    private String setup_vendor;
    private String setup_sense;
    private String setup_diag_arr;
    private String setup_Klog_tag;
    private String setup_save_dir_name;

    private boolean setup_diag_chk;
    private boolean setup_register_chk;
    private boolean setup_debuglevel_chk;
    private boolean setup_selftest_chk;
    private boolean setup_reset_chk;
    private boolean setup_flashdump_chk;
    private boolean setup_debug_chk;
    private boolean setup_vendor_chk;
    private boolean setup_sense_chk;
    private boolean setup_diag_arr_chk;

    static {
        System.loadLibrary("HimaxAPK");
    }

    //Native function
    public native String writeCfg(String[] stringArray);

    public native String readCfg(String[] stringArray);


    public SharedPreferences sh_settings;

    public NodeDataSource(Context context) {
        sh_settings = context.getSharedPreferences("HIAPK", 0);

        setup_driver_arch = sh_settings.getString("SETUP_DRIVER_ARCH", "v1");
        setup_dir = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        setup_diag = sh_settings.getString("SETUP_DIAG_NODE", "diag");

        setup_v2_diag_stack = sh_settings.getString("SETUP_DIAG_V2_STACK", "stack");
        setup_v2_diag_siir = sh_settings.getString("SETUP_DIAG_V2_SIIR", "iir_s");
        setup_v2_diag_sdc = sh_settings.getString("SETUP_DIAG_V2_SDC", "dc_s");
        setup_v2_diag_sbank = sh_settings.getString("SETUP_DIAG_V2_SBANK", "bank_s");

        setup_register = sh_settings.getString("SETUP_REGISTER_NODE", "register");
        setup_debuglevel = sh_settings.getString("SETUP_DEBUGLEVEL_NODE", "debug_level");
        setup_selftest = sh_settings.getString("SETUP_SELFTEST_NODE", "tp_self_test");
        setup_reset = sh_settings.getString("SETUP_RESET_NODE", "reset");
        setup_flashdump = sh_settings.getString("SETUP_FLASHDUMP_NODE", "flash_dump");
        setup_debug = sh_settings.getString("SETUP_DEBUG_NODE", "debug");
        setup_vendor = sh_settings.getString("SETUP_VENDOR_NODE", "vendor");
        setup_sense = sh_settings.getString("SETUP_SENSE_NODE", "SenseOnOff");
        setup_diag_arr = "diag_arr";//sh_settings.getString("SETUP_DIAG_ARR_NODE", "diag_arr");
        setup_Klog_tag = sh_settings.getString("SETUP_KLOG_TAG", "HXTP");
        setup_save_dir_name = sh_settings.getString("SETUP SAVE DIR NAME", "HimaxAPK");


        setup_diag_chk = sh_settings.getBoolean("SETUP_DIAG_NODE_CHK", false);
        setup_register_chk = sh_settings.getBoolean("SETUP_REGISTER_NODE_CHK", false);
        setup_debuglevel_chk = sh_settings.getBoolean("SETUP_DEBUGLEVEL_NODE_CHK", false);
        setup_selftest_chk = sh_settings.getBoolean("SETUP_SELFTEST_NODE_CHK", false);
        setup_reset_chk = sh_settings.getBoolean("SETUP_RESET_NODE_CHK", false);
        setup_flashdump_chk = sh_settings.getBoolean("SETUP_FLASHDUMP_NODE_CHK", false);
        setup_debug_chk = sh_settings.getBoolean("SETUP_DEBUG_NODE_CHK", false);
        setup_vendor_chk = sh_settings.getBoolean("SETUP_VENDOR_NODE_CHK", false);
        setup_sense_chk = sh_settings.getBoolean("SETUP_SENSE_NODE_CHK", false);
        setup_diag_arr_chk = sh_settings.getBoolean("SETUP_DIAG_ARR_NODE_CHK", false);

        if (setup_driver_arch.indexOf("v2") >= 0) {
            gDriverArchVal = 2;
        } else {
            gDriverArchVal = 1;
        }

    }

    public void msDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public String simpleWriteCMD(String cmd[]) {
        String result = "";
        String[] write_cmd = reGenNodeCMD(cmd[0], cmd[1], CMD_WRITE_STR);

        result = WriteCMD(write_cmd);
        return result;
    }


    private String WriteCMD(String cmd[]) {
        String temp = null;
        String result = null;
        String t_str[] = new String[3];
        int retry = 3;
        for (int i = 0; i < 3; i++) {
            do {
                temp = writeCfgByJava(cmd);
                if (temp.indexOf("fail") < 0 && temp != null && !temp.isEmpty())
                    break;

            } while (retry-- >= 0);

            t_str[i] = temp;
        }

        if (Objects.equals(t_str[0], t_str[1]))
            result = t_str[0];
        else if (Objects.equals(t_str[1], t_str[2]))
            result = t_str[1];
        else
            result = t_str[2];
        return result;
    }

    public String simpleReadCMD(String cmd[]) {
        String result = "";
        String[] read_cmd = reGenNodeCMD(cmd[0], cmd[1], CMD_READ_STR);

        result = ReadCMD(read_cmd);
        return result;
    }

    private String ReadCMD(String cmd[]) {
        String temp = null;
        String result = null;
        String t_str[] = new String[3];
        int retry = 3;
        for (int i = 0; i < 3; i++) {
            do {
                temp = readCfgByJava(cmd);
                if (temp.indexOf("fail") < 0 && temp != null && !temp.isEmpty())
                    break;
            } while (retry-- >= 0);
            t_str[i] = temp;
        }

        if (Objects.equals(t_str[0], t_str[1]))
            result = t_str[0];
        else if (Objects.equals(t_str[1], t_str[2]))
            result = t_str[1];
        else
            result = t_str[2];
        return result;
    }

    /* 190702,Now it doesn't use */
    public boolean setRawDataTransform(int type) {
        try {
            String[] writeCommand = reGenNodeCMD(setup_diag_arr, type + "", CMD_WRITE_STR);
            writeCfgByJava(writeCommand);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setRawDataTransform error: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
    }

    public int HexStr2HexInt(String input) {
        int result = 0;

        Log.d(TAG, "Now input = " + input);

        for (int i = 0; i < input.length(); i++) {
            int temp_int = 0;
            char temp = input.charAt(i);
            if (temp >= '0' && temp <= '9')
                temp_int = temp - '0';
            else if (temp >= 'A' && temp <= 'F')
                temp_int = temp - 'A' + 10;
            else if (temp >= 'a' && temp <= 'f')
                temp_int = temp - 'a' + 10;
            result += (temp * Math.pow(16, input.length() - i - 1));
        }
        Log.d(TAG, String.format("Now output = 0x%08X", result));

        return result;
    }

    public boolean dumpFWCommandToSDCARD(int sizeK) {
        try {

            File f = new File(DUMP_PATH);
            f.delete();

            String[] writeCommand = reGenNodeCMD(setup_flashdump, "2_" + sizeK, CMD_WRITE_STR);
            WriteCMD(writeCommand);

            boolean exists = false;
            int count = 0;
            while (!exists) {
                Thread.sleep(500);
                f = new File(DUMP_PATH);
                exists = f.exists();
                if (count >= 10) {
                    return false;
                }
                count++;
            }
        } catch (Exception e) {
            Log.e(TAG, "dump flash error: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
        return true;
    }


    public boolean getRawDataRowAndColumn(int[] channel, boolean includeSelfMsg) {
        try {
            String[] writeCommand = reGenNodeCMD(setup_diag, mICData.DIAG_DC + "", CMD_WRITE_STR);
            WriteCMD(writeCommand);
            Thread.sleep(10);
            String[] readCommand = reGenNodeCMD(setup_diag, mICData.DIAG_DC + "", CMD_READ_STR);
            String result = ReadCMD(readCommand);
            if (DEBUG) {
                Log.d(TAG, result);
            }
            getChannels(result, channel, includeSelfMsg);

            Thread.sleep(10);

            String[] writeCommand_back = reGenNodeCMD(setup_diag, mICData.DIAG_NORMAL + "", CMD_WRITE_STR);
            WriteCMD(writeCommand_back);

            Thread.sleep(10);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Parsing Raw data Error: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
    }

    public boolean sensingOffAndOn() {
        try {
            String[] offCommand = reGenNodeCMD(setup_sense, 0 + "", CMD_WRITE_STR);
            WriteCMD(offCommand);
            Thread.sleep(200);
            String[] onCommand = reGenNodeCMD(setup_sense, 1 + "", CMD_WRITE_STR);
            WriteCMD(onCommand);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "sensingOffAndOn Error: " + e.toString());
            e.fillInStackTrace();
            return false;
        }

    }

    public String[] reGenNodeCMD(String node, String cmd, String rwcmd) {
        String result_write[] = {"", ""};
        String result_read[] = {"", ""};


        List<String> nodes = Arrays.asList(setup_register, setup_diag, setup_debuglevel, setup_selftest, setup_reset, setup_flashdump, setup_debug, setup_vendor, setup_sense, setup_diag_arr);
        boolean nodes_chk[] = {setup_register_chk, setup_diag_chk, setup_debuglevel_chk, setup_selftest_chk, setup_reset_chk, setup_flashdump_chk, setup_debug_chk, setup_vendor_chk, setup_sense_chk, setup_diag_arr_chk};
        int index = nodes.indexOf(node);

        if (index != -1) {
            if (setup_driver_arch.indexOf("v2") >= 0) {
                if (node.indexOf(setup_register) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "register," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (node.indexOf(setup_debuglevel) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "debug_level," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;

                } else if (node.indexOf(setup_flashdump) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "flashdump," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (result_write[0].indexOf(setup_sense) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "senseonoff," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (node.indexOf(setup_reset) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "reset," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (node.indexOf(setup_debug) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (node.indexOf(setup_diag_arr) >= 0) {
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "diag_arr," + cmd;

                    result_read[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                } else if (node.indexOf(setup_diag) >= 0) {
                    int cmdVal = Integer.valueOf(cmd);

                    Log.d(TAG, String.format("Now cmd str=%s, str_val=%d", cmd, cmdVal));

                    if (cmdVal >= 10) {
                        switch (cmdVal % 10) {
                            case 1:
                                Log.d(TAG, "Sram IIR");
                                result_read[0] = (nodes_chk[index] == true) ? setup_diag + "/" + setup_v2_diag_siir : setup_dir + setup_diag + "/" + setup_v2_diag_siir;
                                break;
                            case 2:
                                Log.d(TAG, "Sram rawdata");
                                result_read[0] = (nodes_chk[index] == true) ? setup_diag + "/" + setup_v2_diag_sdc : setup_dir + setup_diag + "/" + setup_v2_diag_sdc;
                                break;
                            case 3:
                                Log.d(TAG, "Sram DC");
                                result_read[0] = (nodes_chk[index] == true) ? setup_diag + "/" + setup_v2_diag_sbank : setup_dir + setup_diag + "/" + setup_v2_diag_sbank;
                                break;
                            default:
                                Log.d(TAG, String.format("Other command,val = %d, using iir node", cmdVal));
                                result_read[0] = (nodes_chk[index] == true) ? setup_diag + "/" + setup_v2_diag_siir : setup_dir + setup_diag + "/" + setup_v2_diag_siir;
                                break;
                        }
                    } else if (cmdVal > 0 && cmdVal < 10) {
                        Log.d(TAG, "Stack");
                        result_read[0] = (nodes_chk[index] == true) ? setup_diag + "/" + setup_v2_diag_stack : setup_dir + setup_diag + "/" + setup_v2_diag_stack;
                    } else {
                        Log.e(TAG, String.format("Fail cmd=%d", cmdVal));
                    }
                    result_write[0] = (nodes_chk[index] == true) ? setup_debug : setup_dir + setup_debug;
                    result_write[1] = "diag," + cmd;

                } else {
                    Log.e(TAG, "Enter other cmd " + node);
                }
            } else {
                result_write[0] = (nodes_chk[index] == true) ? nodes.get(index) : setup_dir + nodes.get(index);
                result_write[1] = cmd;

                result_read[0] = (nodes_chk[index] == true) ? nodes.get(index) : setup_dir + nodes.get(index);
            }
        } else {
            Log.e(TAG, "We can't find this node:" + node);
        }

        result_write[1] = result_write[1] + "\n";


        if (rwcmd.matches("[Rr]{1}")) {
            Log.d(TAG, "It is Read cmd!");
            Log.d(TAG, "resul_read[0]:" + result_read[0]);
            Log.d(TAG, "resul_read[1]:" + result_read[1]);
            return result_read;
        } else if (rwcmd.matches("[Ww]{1}")) {
            Log.d(TAG, "It is Write cmd!");
            Log.d(TAG, "resul_write[0]:" + result_write[0]);
            Log.d(TAG, "resul_write[1]:" + result_write[1]);
            return result_write;
        } else {
            Log.e(TAG, "It is not R/W cmd!");
            return null;
        }

    }

    public String getDiagData(int type, boolean isFirstTime) {
        String node = setup_diag;
        String result = "";
        String w_cmd[] = reGenNodeCMD(node, type + "", CMD_WRITE_STR);
        String r_cmd[] = reGenNodeCMD(node, type + "", CMD_READ_STR);
        boolean type_sram_can_chk = type <= 13 && type >= 11 ? true : false;
        try {
            if (isFirstTime && !(gDriverArchVal == 2 && type_sram_can_chk)) {
                Log.d(TAG, "Ready to write diag cmd");
                writeCfgByJava(w_cmd);
            }
            if (type >= 10) {
                Thread.sleep(50);
            } else {
                Thread.sleep(100);
            }
            result = readCfgByJava(r_cmd);
            Log.d(TAG, result);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

    public boolean readSpecificDiag(int type, int[][] output, boolean isFirstTime, StringBuilder raw, boolean includeSelfMsg) {
        return readSpecificDiag(type, output, isFirstTime, raw, 0, includeSelfMsg, null);
    }

    public boolean readSpecificDiag(int type, int[][] output, boolean isFirstTime, boolean includeSelfMsg) {
        return readSpecificDiag(type, output, isFirstTime, null, 0, includeSelfMsg, null);
    }

    public boolean readSpecificDiag(int type, int[][] output, boolean isFirstTime, StringBuilder raw, int transferType, boolean includeSelfMsg, int[][] areaInfo) {
        try {

            String result = getDiagData(type, isFirstTime);

            if (DEBUG) {
                Log.d(TAG, result);
            }

            if (raw != null) {
                raw.append(result);
            }
            if (output != null) {
//                String tmp = result
//                        + "\n" +
//                        "Major/Minor Info:\n" +
//                        "[0] = 195,[0] = 201,    [1] = 195,[1] = 201,    [2] = 195,[2] = 201,    [3] = 260,[3] = 402,    [4] = 195,[4] = 201,\n" +
//                        "[5] =   0,[5] =   0,    [6] =   0,[6] =   0,    [7] =   0,[7] =   0,    [8] =   0,[8] =   0,    [9] =   0,[9] =   0,\n";
                parsingRawData(result, output, transferType, includeSelfMsg, areaInfo);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Parsing Raw data Error: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
    }

    private void getChannels(String input, int[] channel, boolean includeSelfMsg) {
        int i = input.indexOf(",");
        int i_end = input.indexOf("\n");
        String colS = input.substring(0, i).replaceAll("\\D+", "");
        String rowS = input.substring(i, i_end).replaceAll("\\D+", "");
        channel[1] = (includeSelfMsg) ? Integer.valueOf(colS) + 1 : Integer.valueOf(colS);
        channel[0] = (includeSelfMsg) ? Integer.valueOf(rowS) + 1 : Integer.valueOf(rowS);
    }

    private void parsingRawData(String input, int[][] output, int transformType, boolean includeSelfMsg, int[][] areaInfo) {

        int index_area = input.indexOf("Major/Minor Info:");
        if (areaInfo != null && index_area != -1) {
            String area = input.substring(index_area + 17, input.length());
            String[] rr = area.split(",");
            for (int i = 0; i < rr.length; i++) {
                int j = i / 2;
                if (j >= 10) {
                    break;
                }
                String[] rrr = rr[i].split("=");
                int val = Integer.valueOf(rrr[1].replaceAll("[^\\d.]", ""));
                areaInfo[i % 2][j] = val;
            }
        }

        int index = input.indexOf("\n", input.indexOf("\n") + 1);
        String startAear = input.substring(index, (index_area == -1) ? input.length() : index_area);
        int rl = 0;
        int outputIndex = 0;
        int count = 0;
        int finalCount = output.length * output[0].length;
        int temp_i = 0;
        int temp_j = startAear.indexOf("\n");
        String temp = startAear.substring(temp_i, temp_j);
        while (count < finalCount) {
            if (temp.length() < output.length) {
                temp_i = temp_j + 1;
                temp_j = startAear.indexOf("\n", temp_i);
                if (temp_j < 0) {
                    break;
                }
                temp = startAear.substring(temp_i, temp_j);
                continue;
            }
            int q = temp.indexOf("]");
            if (q >= 0) {
                // new format
                if (temp.indexOf("]", q + 1) < 0) {
                    temp = temp.substring(q + 1, temp.length());
                } else {
                    temp_i = temp_j + 1;
                    temp_j = startAear.indexOf("\n", temp_i);
                    temp = startAear.substring(temp_i, temp_j);
                    continue;
                }
            }
            String[] r = temp.split(" ");
            int ri = 0;

            if (rl == 0) {
                for (int i = 0; i < r.length; i++) {
                    if (r[i].trim().length() == 0) {
                        continue;
                    }
                    rl++;
                }
                if (!includeSelfMsg) rl--;
            }

            for (int i = 0; i < r.length; i++) {
                if (r[i].trim().length() == 0) {
                    continue;
                }

                if (rl == output.length) {
                    if (ri >= output.length || outputIndex >= output[0].length) {
                        continue;
                    }
                } else {
                    if (ri >= output[0].length || outputIndex >= output.length) {
                        continue;
                    }
                }

                int transformX = 0, transformY = 0;
                switch (transformType) {
                    case DataMonitorConfig.TRANSFORM_DATA_ONE: {
                        transformX = (rl == output.length) ? ri : outputIndex;
                        transformY = (rl == output.length) ? outputIndex : ri;
                    }
                    break;
                    case DataMonitorConfig.TRANSFORM_DATA_TWO: {
                        transformX = (rl == output.length) ? (output.length - ri - 1) : outputIndex;
                        transformY = (rl == output.length) ? outputIndex : (output[0].length - ri - 1);
                    }
                    break;
                    case DataMonitorConfig.TRANSFORM_DATA_THREE: {
                        transformX = (rl == output.length) ? ri : (output.length - outputIndex - 1);
                        transformY = (rl == output.length) ? (output[0].length - outputIndex - 1) : ri;
                    }
                    break;
                    case DataMonitorConfig.TRANSFORM_DATA_FOUR: {
                        transformX = (rl == output.length) ? (output.length - ri - 1) : (output.length - outputIndex - 1);
                        transformY = (rl == output.length) ? (output[0].length - outputIndex - 1) : (output[0].length - ri - 1);
                    }
                    break;
                }
                try {
                    output[transformX][transformY] = Integer.valueOf(r[i]);
                    ri++;
                } catch (Exception e) {
                    e.fillInStackTrace();
                }

            }
            count += (ri);
            outputIndex++;


            temp_i = temp_j + 1;
            temp_j = startAear.indexOf("\n", temp_i);

            if (temp_i >= startAear.length() || temp_j >= startAear.length()) {
                count = finalCount;
                continue;
            }
            temp = startAear.substring(temp_i, temp_j);
        }

    }


    private void parsingRawDataOrigin(String input, int[][] output) {
        String safeArea1 = input.substring(input.lastIndexOf("[00]"), input.indexOf("ChannelEnd"));
        int ptrRow = safeArea1.indexOf("\n");
        String safeArea = safeArea1.substring(ptrRow, safeArea1.length() - 1);

        String numRowStr = safeArea1.substring(ptrRow - 3, ptrRow - 1);
        int numRow = Integer.valueOf(numRowStr);

        int columnChs = output[0].length;
        int rowChs = output.length;

        boolean transform = (columnChs > numRow);

        int index_row = -1;
        int index_col = -1;

        int maxNumLength = 6;

        int pointer = 0;
        boolean rowStart = false;
        StringBuilder numBuf = new StringBuilder();
        while (pointer < safeArea.length()) {
            Character p_s = safeArea.charAt(pointer);
            if (!rowStart) {
                if ("]".equals(p_s.toString())) {
                    rowStart = true;
                    index_row++;
                    index_col = -1;
                    numBuf.delete(0, numBuf.length());
                }
            } else {
                if (" ".equals(p_s.toString())) {
                    String numF = numBuf.toString().trim();
                    if (numF.length() > 0) {
                        int overflowCount = numF.length() / maxNumLength;
                        int firstLength = numF.length() % maxNumLength;
                        if (overflowCount >= 1) {
                            if (firstLength == 0) {
                                overflowCount--;
                                firstLength = maxNumLength;
                            }
                            for (int x = 0; x <= overflowCount; x++) {
                                if ((index_col + 1) < columnChs) {
                                    index_col++;
                                    int value = 0;
                                    String s = (x == 0) ? numF.substring(0, firstLength) : numF.substring(firstLength + ((x - 1) * maxNumLength), firstLength + (x * maxNumLength));
                                    try {
                                        value = Integer.valueOf(s);
                                    } catch (Exception e) {
                                        Log.e(TAG, "multiple transfer num to int error: \n" + s + " " + numF + "\n" + input);
                                    }
                                    if (transform) {
                                        output[index_col][index_row] = value;
                                    } else {
                                        output[index_row][index_col] = value;
                                    }
                                }
                            }
                            numBuf.delete(0, numBuf.length());
                        } else {
                            if ((index_col + 1) < columnChs) {
                                int value = 0;
                                try {
                                    value = Integer.valueOf(numF);
                                } catch (Exception e) {
                                    Log.e(TAG, "transfer num to int error: \n" + numF + "\n" + input);
                                }
                                if (transform) {
                                    output[++index_col][index_row] = value;
                                } else {
                                    output[index_row][++index_col] = value;
                                }
                                numBuf.delete(0, numBuf.length());
                            }
                        }
                    }
                } else if ("\n".equals(p_s.toString())) {
                    rowStart = false;
                } else {
                    numBuf.append(p_s);
                }
            }
            pointer++;
        }

        if (DEBUG) {
            for (int i = 0; i < rowChs; i++) {
                String row = "";
                for (int j = 0; j < columnChs; j++) {
                    row = row + " " + output[i][j];
                }
                Log.d(TAG, "row" + i + ":" + row);
            }
        }
    }

    public void writeRegister(String address, String value) {
        String node = setup_dir;
        String register = node + setup_register;

        StringBuilder sb = new StringBuilder();
        sb.append("w:x");
        sb.append(address);
        sb.append(":x");
        sb.append(value);
        //sb.append("\n");
        String[] cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
        String result = WriteCMD(cmd);
        if (DEBUG) {
            Log.d(TAG, result);
        }
    }

    public void wrtieRegister(String address, int value) {
        String valueHex = Integer.toHexString(value);

        StringBuilder sb = new StringBuilder();
        sb.append("w:x");
        sb.append(address);
        sb.append(":x");
        sb.append(addZeroForNum(valueHex, 8));
        //sb.append("\n");
        String[] cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
        String result = WriteCMD(cmd);
        if (DEBUG) {
            Log.d(TAG, result);
        }
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

    /* echo d > debug
     *  cat debug
     *  [0]: Himax Touch IC Information :
     *  [1]: HX83102D
     *  [2]: IC Checksum : CRC
     *  [3]: Driver register Interrupt : EDGE TIRGGER
     *  [4]: Protocol : TYPE_B
     *  [5]: RX Num : 32
     *  [6]: TX Num : 18
     *  [7]: BT Num : 0
     *  [8]: X Resolution : 720
     *  [9]: Y Resolution : 1560
     *  [10]:Max Point : 10
     *  [11]:XY reverse : 1
     *
     *  */
    public ArrayList<String> getTPinfo() {
        ArrayList<String> reslt = new ArrayList<String>();
        String[] w_cmd = reGenNodeCMD(setup_debug, "d", CMD_WRITE_STR);
        String[] r_cmd = reGenNodeCMD(setup_debug, "", CMD_READ_STR);
        WriteCMD(w_cmd);

        String rslt_str = ReadCMD(r_cmd);
        int index_start = 0;
        int index_end = 0;
        int buffer_idx = 0;
        do {

            index_end = rslt_str.indexOf('\n', index_end == 0 ? index_end : index_end + 1);
            String tmp = rslt_str.substring(index_start, index_end);
            reslt.add(buffer_idx++, tmp);
            index_start = index_end + 1;
            Log.d(TAG, String.format("index_start=%d, index_end=%d, buffer_idx=%d, tmp=%s", index_start, index_end, buffer_idx, tmp));
        } while (index_start < rslt_str.length());

        if (false) {
            Log.d(TAG, String.format("Size=%d", reslt.size()));
            for (int i = 0; i < reslt.size(); i++) {
                Log.d(TAG, "cccc:" + reslt.get(i));
            }
        }
        return reslt;
    }

    public String getICIDfromDebug(ArrayList<String> input) {
        String rslt;
        String tmp;
        int str_tmp_end;
        /* ic id*/
        tmp = input.get(1);
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf("HX") > 0) { /* != 0 && !=-1*/
            rslt = tmp.substring(tmp.indexOf("HX"), str_tmp_end);
        } else {
            rslt = tmp.substring(0, str_tmp_end);
        }
        Log.d(TAG, "Now ICID in String:" + rslt);

        return rslt;
    }

    /* test and sample
     *  [0]: Himax Touch IC Information :
     *  [1]: HX83102D
     *  [2]: IC Checksum : CRC
     *  [3]: Driver register Interrupt : EDGE TIRGGER
     *  [4]: Protocol : TYPE_B
     *  [5]: RX Num : 32
     *  [6]: TX Num : 18
     *  [7]: BT Num : 0
     *  [8]: X Resolution : 720
     *  [9]: Y Resolution : 1560
     *  [10]:Max Point : 10
     *  [11]:XY reverse : 1*/
    public void parseTPinfo(ArrayList<String> input) {
        String ic_id;
        String ic_chksum;
        String irq_type;
        String protocol;
        int resolution[] = new int[3]; /*0:x, 1:y, 2:Max point*/
        int channel[] = new int[3]; /*0:RX, 1:TX, 2:BT Num*/

        String tmp;
        int str_tmp_end;

        /* ic id*/
        tmp = input.get(1);
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf("HX") > 0) { /* != 0 && !=-1*/
            ic_id = tmp.substring(tmp.indexOf("HX"), str_tmp_end);
        } else {
            ic_id = tmp.substring(0, str_tmp_end);
        }
        if (ic_id.indexOf(mICData.HX_83102D_SERIES_PWON) >= 0)
            Log.d(TAG, "Same:" + ic_id);
        else
            Log.d(TAG, "Different:" + ic_id);

        /*ic checksum*/
        tmp = input.get(2).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            ic_chksum = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "ic chksum:" + ic_chksum);
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }

        /* Protocol */
        tmp = input.get(4).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            protocol = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "Protocol:" + protocol);
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }

        /* IRQ type*/
        tmp = input.get(3).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            irq_type = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "irq type:" + irq_type);
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }

        /*channel*/
        /*RX*/
        tmp = input.get(5).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "RX:" + tmp);
            channel[0] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("RX in INT:%d", channel[0]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }
        /*TX*/
        tmp = input.get(6).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "TX:" + tmp);
            channel[1] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("TX in INT:%d", channel[1]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }
        /*BT*/
        tmp = input.get(7).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "BT:" + tmp);
            channel[2] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("BT in INT:%d", channel[2]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }


        /*resolution*/
        /*X-res*/
        tmp = input.get(8).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "X-res:" + tmp);
            resolution[0] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("X-res in INT:%d", resolution[0]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }
        /*Y-res*/
        tmp = input.get(9).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "Y-res:" + tmp);
            resolution[1] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("Y-res in INT:%d", resolution[1]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }
        /*Max Point*/
        tmp = input.get(10).replaceAll(" ", "");
        str_tmp_end = tmp.indexOf('\n') == (tmp.length() - 1) ? tmp.length() - 1 : tmp.length();
        if (tmp.indexOf(':') >= 0) {
            tmp = tmp.substring(tmp.indexOf(':') + 1, str_tmp_end);
            Log.d(TAG, "MAX point:" + tmp);
            resolution[2] = Integer.parseInt(tmp);
            Log.d(TAG, String.format("MAX point in INT:%d", resolution[2]));
        } else {
            Log.e(TAG, "There is no \':\': fail format");
        }

    }

    public Long readMaxDC(String address) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("r:x");
        sb.append(address);
        String[] w_cmd = reGenNodeCMD(setup_register, address, CMD_WRITE_STR);
        String[] r_cmd = reGenNodeCMD(setup_register, address, CMD_READ_STR);
        WriteCMD(w_cmd);

        String result = ReadCMD(r_cmd);
        if (DEBUG) {
            Log.d(TAG, result);
        }
        int index = result.indexOf("x");
        StringBuilder hexString = new StringBuilder();
        hexString.append(result.substring(index + 6, index + 8));
        hexString.append(result.substring(index + 1, index + 3));
        return Long.parseLong(hexString.toString(), 16);
    }

    public Long readRegister(String address) {
        StringBuilder sb = new StringBuilder();
        sb.append("r:x");
        sb.append(address);
        //sb.append("\n");
        String[] w_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
        String[] r_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_READ_STR);
        WriteCMD(w_cmd);

        String result = ReadCMD(r_cmd);
        if (DEBUG) {
            Log.d(TAG, result);
        }
        int index = result.indexOf("x");
        StringBuilder hexString = new StringBuilder();
        hexString.append(result.substring(index + 16, index + 18));
        hexString.append(result.substring(index + 11, index + 13));
        hexString.append(result.substring(index + 6, index + 8));
        hexString.append(result.substring(index + 1, index + 3));
        return Long.parseLong(hexString.toString(), 16);
    }

    public String readRegister(String address, boolean isFourByte) {
//        String node = mShare.getString("SETUP_DIR_NODE", "/proc/android_touch/");
//        String register  = node + mShare.getString("SETUP_REGISTER_NODE", "register");
        String register = "/proc/touchpanel/debug_info/himax/register";
        StringBuilder sb = new StringBuilder();
        sb.append("r:x");
        sb.append(address);
        //sb.append("\n");
        String[] w_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
        String[] r_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_READ_STR);
        WriteCMD(w_cmd);

        String result = ReadCMD(r_cmd);
        if (DEBUG) {
            Log.d(TAG, result);
        }
        int index = result.indexOf("x");
        StringBuilder hexString = new StringBuilder();
        hexString.append(result.substring(index + 16, index + 18));
        hexString.append(result.substring(index + 11, index + 13));
        hexString.append(result.substring(index + 6, index + 8));
        hexString.append(result.substring(index + 1, index + 3));
        return hexString.toString();
    }

    public String readCfgByJava(String[] stringArray) {
        File file = new File(stringArray[0]);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return sb.toString();
    }

    public String writeCfgByJava(String[] stringArray) {
        File file = new File(stringArray[0]);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(stringArray[1].getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return readCfgByJava(stringArray);
    }

    public boolean isOSRCCOpend() {
        String node = setup_dir;
        String register = node + setup_register;
//        String register = "/proc/touchpanel/debug_info/himax/register";
        StringBuilder sb = new StringBuilder();
        sb.append("r:x");
        sb.append("10007088");
        //sb.append("\n");
        String[] w_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
        String[] r_cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_READ_STR);
        WriteCMD(w_cmd);
        String result = ReadCMD(r_cmd);

        if (DEBUG) {
            Log.d(TAG, result);
        }
        int index = result.indexOf("x");

        String rfeh02 = result.substring(index + 1, index + 3);
        int x = Integer.parseInt(rfeh02, 16);
        BitSet set = fromByte((byte) x);
//        String btye = Integer.toBinaryString(x);
        Log.d(TAG, set.toString() + "//" + set.get(4) + " // " + Integer.toBinaryString(x) + "// " + set.toByteArray());
        return set.get(4);
    }

    public void setOSRCCSwitch(boolean value) {
        String node = setup_dir;

        try {
            //sense off
            String senseOnOff = node + setup_sense;
            String[] offCommand = reGenNodeCMD(setup_sense, 0 + "", CMD_WRITE_STR);
            WriteCMD(offCommand);

            String register = node + setup_register;
            if (!value) {
                //disable reload
                StringBuilder sb = new StringBuilder();
                sb.append("w:x10007f00:x0000A55A");
//                sb.append("\n");
                String[] cmd = reGenNodeCMD(setup_register, sb.toString(), CMD_WRITE_STR);
                WriteCMD(cmd);
            }

            Thread.sleep(500);

            //set osc_cc value
            StringBuilder sb1 = new StringBuilder();
            sb1.append("r:x");
            sb1.append("10007088");
//            sb1.append("\n");
            String[] w_cmd1 = reGenNodeCMD(setup_register, sb1.toString(), CMD_WRITE_STR);
            String[] r_cmd1 = reGenNodeCMD(setup_register, sb1.toString(), CMD_READ_STR);
            WriteCMD(w_cmd1);
            String result = ReadCMD(r_cmd1);
            int index = result.indexOf("x");
            StringBuilder hexString = new StringBuilder();
            hexString.append(result.substring(index + 16, index + 18));
            hexString.append(result.substring(index + 11, index + 13));
            hexString.append(result.substring(index + 6, index + 8));
            String rfeh02 = result.substring(index + 1, index + 3);
            int x = Integer.parseInt(rfeh02, 16);
            BitSet set = fromByte((byte) x);
            if (value) {
                set.set(4, true);
                set.set(5, true);
                hexString.append(bytesToHex(set.toByteArray()));
            } else {
                set.set(4, false);
                set.set(5, false);
                hexString.append(bytesToHex(set.toByteArray()));
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append("w:x10007088");
            sb4.append(":x");
            sb4.append(hexString.toString());
//            sb4.append("\n");
            String[] cmd4 = reGenNodeCMD(setup_register, sb4.toString(), CMD_WRITE_STR);
            WriteCMD(cmd4);
            Log.d(TAG, sb4.toString());
            Thread.sleep(500);
            if (value) {
                //disable reload
                StringBuilder sb3 = new StringBuilder();
                sb3.append("w:x10007f00:x00005AA5");
//                sb3.append("\n");
                String[] cmd3 = reGenNodeCMD(setup_register, sb3.toString(), CMD_WRITE_STR);
                WriteCMD(cmd3);
            }

            Thread.sleep(500);
            //sense on
            String senseOn = node + setup_sense;
            String[] onCommand = reGenNodeCMD(setup_sense, 1 + "", CMD_WRITE_STR);
            WriteCMD(onCommand);

        } catch (Exception e) {
            e.fillInStackTrace();
        }

    }

    public static BitSet fromByte(byte b) {
        BitSet bits = new BitSet(8);
        for (int i = 0; i < 8; i++) {
            bits.set(i, (b & 1) == 1);
            b >>= 1;
        }
        return bits;
    }


    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


}
