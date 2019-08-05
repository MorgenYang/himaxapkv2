package com.ln.himaxtouch.ICInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.ln.himaxtouch.HimaxApplication.mNodeAcc;

public class ICData {
    private static final String TAG = "[HXTP]"+ICData.class.getName();

    public static final String HX_85XX_A_SERIES_PWON = "HX85xxA";
    public static final String HX_85XX_B_SERIES_PWON = "HX85xxB";
    public static final String HX_85XX_C_SERIES_PWON = "HX85xxC";
    public static final String HX_85XX_D_SERIES_PWON = "HX85xxD";
    public static final String HX_85XX_E_SERIES_PWON = "HX85xxE";
    public static final String HX_85XX_ES_SERIES_PWO = "HX85xxES";
    public static final String HX_85XX_ES_SERIES_PWO1 = "HX852xES";
    public static final String HX_85XX_F_SERIES_PWON = "HX85xxF";
    public static final String HX_85XX_H_SERIES_PWON = "HX85xxH";
    public static final String HX_83100A_SERIES_PWON = "HX83100A";
    public static final String HX_83102A_SERIES_PWON = "HX83102A";
    public static final String HX_83102B_SERIES_PWON = "HX83102B";
    public static final String HX_83102D_SERIES_PWON = "HX83102D";
    public static final String HX_83102E_SERIES_PWON = "HX83102E";
    public static final String HX_83103A_SERIES_PWON = "HX83103A";
    public static final String HX_83106A_SERIES_PWON = "HX83106A";
    public static final String HX_83110A_SERIES_PWON = "HX83110A";
    public static final String HX_83110B_SERIES_PWON = "HX83110B";
    public static final String HX_83111B_SERIES_PWON = "HX83111B";
    public static final String HX_83112A_SERIES_PWON = "HX83112A";
    public static final String HX_83112B_SERIES_PWON = "HX83112B";
    public static final String HX_83113A_SERIES_PWON = "HX83113A";
    public static final String HX_83112D_SERIES_PWON = "HX83112D";
    public static final String HX_83112E_SERIES_PWON = "HX83112E";
    public static final String HX_83191A_SERIES_PWON = "HX83191A";

    public static final int DIAG_NORMAL = 0;
    public static final int DIAG_IIR = 1;
    public static final int DIAG_DC = 2;
    //    public static final int DIAG_DC_SRAM = 12;
    //used event stack temporarily, because of sram hand shack not ready.
    public static  int DIAG_DC_SRAM = 12;

    private static final int NAME_HX83112A = 0x83112A;
    private static final int NAME_HX83102D = 0x83102D;
    private static final int NAME_HX852xES = 0x278505; /* 8525 | 8526 | 8527 | 8528 */

    public static boolean isOncell = false;

    public static int val_icid = 0;
    public static String str_val_icid = null;
    public static int adr_icid = 0x900000D0;
    public static int adr_hopping = 0x10007FC4;
    public static int adr_flash_rlod = 0x10007f00;
    public static int adr_tcon_dummy = 0x800204f4;
    public static int val_tcon_dummy_off = 0x00000002;
    public static int val_tcon_dummy_on = 0x00000000;
    public static int val_force_stop_hopping = 0x00;
    public static int val_force_start_hopping = 0x00;

    private static Context gContext;

    public static String en_intterupt = "1";
    public static String dis_intterupt = "0";
    public static String back2normal = "0";
    public static String sram_iir_cmd = "11";
    public static String transform_cmd = "4";
    public static String SCU_TCON_CLK_DIV_NUM = "r:x90000028";
    public static String TCON_SC_CLK2_PERIOD = "r:x80020134";
    public static String hopping_first = "w:x10007fc4:xA33AA33A";
    public static String hopping_second = "w:x10007fc4:xA55AA55A";
    public static String hopping_origin = "w:x10007fc4:xA33AA33A";
    public static String hopping_clear = "w:x10007fc4:x00000000";
    public static String hopping_read = "r:x10007fc4";
    public static String idle_mode_config = "r:x10007088";
    public static String idle_mode = "w:x10007088";
    public static String disable_flash_reload = "w:x10007f00:x0000A55A";
    public static String enable_flash_reload = "w:x10007f00:x00000000";
    public static String iir_config_c4 = "r:x100070c4";
    public static String iir_config_c8 = "r:x100070c8";
    public static String hopping_reg = "r:x10007b08";
    public static String iir_config_c4_write = "w:x100070c4";
    public static String irr_config_c8_write = "w:x100070c8";
    public static String hopping_reg_write = "w:x10007b08";
    public static String dc_max = "r:x10007fc8";
    public static String tcon_dummy_for_idle = "w:x800204f4:x00000002";
    public static String tcon_dummy_for_normal = "w:x800204f4:x00000000";
    public static String cmd_force_stop_hopping = "w:x10007088:x1072013F"; /* format for HX83102D */
    public static String cmd_force_start_hopping = "w:x10007088:x1072813F"; /* format for HX83102D */


    public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String SYS = android.os.Environment.getRootDirectory().getAbsolutePath();
    public static String diag_path = "diag";
    public static String reg_path = "register";
    public static String f1_result_path = SDCARD+"/f1_result.csv";
    public static String diag_arr_path = "diag_arr";
    public static String senseOnOff = "SenseOnOff";
    public static String int_en_path = "int_en";

    public static String IIR_CMD[] = {diag_path, sram_iir_cmd};
    public static String DC_CMD[] = {diag_path, "2"};
    public static String back2normal_CMD[] = {diag_path, back2normal};
    public static String rx_freq_clk_CMD[] = {reg_path, SCU_TCON_CLK_DIV_NUM};
    public static String rx_freq_clk2_CMD[] = {reg_path, TCON_SC_CLK2_PERIOD};
    public static String hopping_CMD[][] = {{reg_path, hopping_first},{reg_path, hopping_second}, {reg_path, hopping_origin}};
    public static String hopping_clear_CMD[] = {reg_path, hopping_clear};
    public static String transform[] = {diag_arr_path, transform_cmd};
    public static String senseOn_CMD[] = {senseOnOff, "1"};
    public static String senseOff_CMD[] = {senseOnOff, "0"};
    public static String hopping_freq[] = {reg_path, hopping_read};
    public static String idle_mode_CMD[] = {reg_path, idle_mode};
    public static String idle_mode_read_CMD[] = {reg_path, idle_mode_config};
    public static String enable_flash_reload_CMD[] = {reg_path, enable_flash_reload};
    public static String disable_flash_reload_CMD[] = {reg_path, disable_flash_reload};
    public static String iir_config_c4_cmd[] = {reg_path, iir_config_c4};
    public static String iir_config_c8_cmd[] = {reg_path, iir_config_c8};
    public static String hopping_reg_cmd[] = {reg_path, hopping_reg};
    public static String en_int_cmd[] = {int_en_path, en_intterupt};
    public static String dis_int_cmd[] = {int_en_path, dis_intterupt};
    public static String tcon_dummy_for_idle_cmd[] = {reg_path, tcon_dummy_for_idle}; /* Do not enter IDLE*/
    public static String tcon_dummy_for_normal_cmd[] = {reg_path, tcon_dummy_for_normal}; /* back to Normal*/
    public static String exe_force_stop_hopping[] = {reg_path, cmd_force_stop_hopping};
    public static String exe_force_start_hopping[] = {reg_path, cmd_force_start_hopping};



    public ICData(Context context)
    {
        gContext = context;
        SharedPreferences sh_settings = context.getSharedPreferences("HIAPK", 0);
        diag_path = sh_settings.getString("SETUP_DIAG_NODE", "diag");
        reg_path = sh_settings.getString("SETUP_REGISTER_NODE", "register");
    }

    public static void readICIDByNode()
    {
        String rslt;
        rslt = mNodeAcc.getICIDfromDebug(mNodeAcc.getTPinfo());
        str_val_icid = rslt;
        Log.d(TAG, "Now ICID in str="+ str_val_icid);
    }

   public static Long readICID()
    {
        Long rslt = Long.valueOf(0);
        rslt = mNodeAcc.readRegister(String.format("%08X",adr_icid));
        Log.d(TAG, String.format("0x%08X", rslt));

        rslt = rslt >> 8;
        Log.d(TAG, String.format("0x%08X", rslt));

        val_icid = rslt.intValue();
        return rslt;
    }
   public static Long readICID(String icid_str)
    {
        Long rslt = Long.valueOf(0);
        rslt = mNodeAcc.readRegister(icid_str);
        Log.d(TAG, String.format("0x%08X", rslt));

        rslt = rslt >> 8;
        Log.d(TAG, String.format("0x%08X", rslt));
        val_icid = rslt.intValue();
        return rslt;
    }
    public static void matchICIDStr2Int() {
        if(str_val_icid == "" || str_val_icid == null)
            readICIDByNode();
        Log.d(TAG, "Now IC ID String="+str_val_icid);
        if(str_val_icid.indexOf('\n') >= 0) {
            Log.d(TAG, "There is new line" + str_val_icid);
            str_val_icid = str_val_icid.substring(0, str_val_icid.indexOf('\n') - 1);
        }
        if(str_val_icid.indexOf(HX_83102D_SERIES_PWON) >= 0) {
            val_icid = NAME_HX83102D;
        } else if(str_val_icid.indexOf(HX_83112A_SERIES_PWON) >= 0) {
            val_icid = NAME_HX83112A;
        } else if(str_val_icid.indexOf(HX_85XX_ES_SERIES_PWO) >= 0 || str_val_icid.indexOf(HX_85XX_ES_SERIES_PWO1) >= 0) {
            val_icid = NAME_HX852xES;
            DIAG_DC_SRAM = DIAG_DC;
            isOncell = true;
        } else {

        }

    }
    public static void reInitByDiffIC(Long icid)
    {
        Log.d(TAG,String.format("Now icid=0x%08X", icid));
        Long temp = Long.valueOf(0);
        switch(icid.intValue()) {
            case NAME_HX83102D:
                adr_tcon_dummy = 0x10007088;
                adr_hopping = 0x10007088;
                /* about idle reject or not*/
                temp = (mNodeAcc.readRegister(String.format("%08X", adr_tcon_dummy)) & 0xFFFFFFF7);
                val_tcon_dummy_off = temp.intValue();
                temp = (mNodeAcc.readRegister(String.format("%08X", adr_tcon_dummy)) | 0x00000008);
                val_tcon_dummy_on = temp.intValue();
                /* about hopping or not*/
                temp = (mNodeAcc.readRegister(String.format("%08X", adr_tcon_dummy)) & 0xFFFF0FFF);
                val_force_stop_hopping = temp.intValue();
                temp = ((mNodeAcc.readRegister(String.format("%08X", adr_tcon_dummy)) & 0xFFFF0FFF) | 0x00008000);
                val_force_start_hopping = temp.intValue();
                break;
            case NAME_HX852xES:
                adr_tcon_dummy = -1;
                val_tcon_dummy_off = -1;
                val_tcon_dummy_on = -1;
                break;
            default:
                break;
        }

        reInitPara();
    }

    public static void reInitPara() {

        if(adr_tcon_dummy != -1) {
            tcon_dummy_for_idle = String.format("w:x%08X:x%08X", adr_tcon_dummy, val_tcon_dummy_off);
            tcon_dummy_for_normal = String.format("w:x%08X:x%08X", adr_tcon_dummy, val_tcon_dummy_on);
            cmd_force_stop_hopping = String.format("w:x%08X:x%08X", adr_hopping, val_force_stop_hopping);
            cmd_force_start_hopping = String.format("w:x%08X:x%08X", adr_hopping, val_force_start_hopping);

            Log.d(TAG, "Now tcon_dummy_for_idle="+tcon_dummy_for_idle);
            Log.d(TAG, "Now tcon_dummy_for_normal="+tcon_dummy_for_normal);
            Log.d(TAG, "Now cmd_force_stop_hopping="+cmd_force_stop_hopping);
            Log.d(TAG, "Now cmd_force_start_hopping="+cmd_force_start_hopping);
        } else {
            tcon_dummy_for_idle = "";
            tcon_dummy_for_normal = "";
        }

        /* Node path Modify*/
        IIR_CMD[0]= diag_path;
        DC_CMD[0] = diag_path;
        back2normal_CMD[0] = diag_path;
        rx_freq_clk_CMD[0] = reg_path;
        rx_freq_clk2_CMD[0] = reg_path;
        transform[0] = diag_arr_path;
        senseOn_CMD[0] = senseOnOff;
        senseOff_CMD[0] = senseOnOff;
        hopping_freq[0] = reg_path;
        idle_mode_CMD[0] = reg_path;
        idle_mode_read_CMD[0] = reg_path;
        enable_flash_reload_CMD[0] = reg_path;
        disable_flash_reload_CMD[0] = reg_path;
        iir_config_c4_cmd[0] = reg_path;
        iir_config_c8_cmd[0] = reg_path;
        hopping_reg_cmd[0] = reg_path;
        en_int_cmd[0] = int_en_path;
        dis_int_cmd[0] = int_en_path;
        for(int i = 0; i < hopping_CMD.length; i++)
            hopping_CMD[i][0] =reg_path;
        hopping_clear_CMD[0] = reg_path;

        tcon_dummy_for_normal_cmd[0] = reg_path;
        tcon_dummy_for_idle_cmd[0] = reg_path;
        exe_force_stop_hopping[0] = reg_path;
        exe_force_start_hopping[0] = reg_path;



        /* IC register modify*/
        tcon_dummy_for_normal_cmd[1] = tcon_dummy_for_normal;
        tcon_dummy_for_idle_cmd[1] = tcon_dummy_for_idle;

        exe_force_stop_hopping[1] = cmd_force_stop_hopping;
        exe_force_start_hopping[1] = cmd_force_start_hopping;
    }

}
