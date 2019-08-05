package com.ln.himaxtouch.RawdataRecord;

import android.util.Log;

/**
 * Created by Einim on 2017/10/26.
 */

public class RawdataProcess {
    //for test
    public String node2csv(String input) {
        StringBuffer process;
        int rx = 0, tx = 0;
        String result = input;
        String TXRX = input.indexOf("\n") >= 0 ? input.substring(0, input.indexOf("\n")) : null;
        int temp_addr = 0;
        /*remove header*/
        for (int i = 0; i < 3; i++) {
            temp_addr = result.indexOf("\n");
            if (temp_addr >= 0) {

                result = result.substring(temp_addr + 1);

            }
            Log.d("HXTP", Integer.toString(temp_addr));
            //result = Integer.toString(temp_addr)+"=="+result;
        }
        /* get TX RX*/
        temp_addr = TXRX.indexOf(',');
        Log.d("HXTP", TXRX.substring(temp_addr + 4, temp_addr + 6));
        rx = Integer.valueOf(TXRX.substring(temp_addr - 2, temp_addr));
        tx = Integer.valueOf(TXRX.substring(temp_addr + 4, temp_addr + 6));

        /* remove */
        while ((temp_addr = result.indexOf(']')) > 0) {

            process = new StringBuffer(result);
            process.delete(temp_addr - 3, temp_addr + 1);
            result = process.toString();
        }
        /* remove */
        result = result.replaceAll("    ", "");
        result = result.replaceAll(" ", ",");
        // result =  result.replaceAll(",,,",",");
        // result =  result.split;



        /*remove tail*/
        result = result.substring(0, result.indexOf("ChannelEnd"));

        return result;
    }

    boolean diff_str_neg(String num) {
        Log.d("HXTP", Integer.toString(num.indexOf('-')));
        if (num.indexOf('-') >= 0) {
            Log.d("HXTP", "negtive");
            return true;
        } else
            return false;
    }

    String find_edge_str(String data, int max) {
        //BEGIN:Steve_Ke
        if ("read fail".equals(data)) {
            return null;
        }
        //END:Steve_Ke

        String result = null;
        int max_num = 0;
        int min_num = 0;
        String max_tag = "Max:,";
        String min_tag = ",Min:,";
        int max_tag_len = 5;
        int min_tag_len = 6;
        int max_tag_position = data.indexOf(max_tag);
        int min_tag_position = data.indexOf(min_tag);
        String min_str = data.substring(min_tag_position + min_tag_len, data.length() - 1);  // last char is \n
        String max_str = data.substring(max_tag_position + max_tag_len, min_tag_position);

        if (max == 1) {
            result = max_str;
        } else {
            result = min_str;
        }


        return result;
    }

    int find_edge_val(String data, int max) {
        //BEGIN:Steve_Ke
        if ("read fail".equals(data)) {
            return 0;
        }
        //END:Steve_Ke

        int result = 0;
        int max_num = 0;
        int min_num = 0;
        String max_tag = "Max:,";
        String min_tag = ",Min:,";
        int max_tag_len = 5;
        int min_tag_len = 6;
        int max_tag_position = data.indexOf(max_tag);
        int min_tag_position = data.indexOf(min_tag);
        String min_str = data.substring(min_tag_position + min_tag_len, data.length() - 1);  // last char is \n
        String max_str = data.substring(max_tag_position + max_tag_len, max_tag_position);

        if (max == 1) {

            if (diff_str_neg(max_str)) {
                max_str = max_str.substring(1, max_str.length());
                Log.d("HXTP", max_str);
                max_num = Integer.parseInt(max_str);
                max_num = 0 - max_num;
            } else
                max_num = Integer.parseInt(max_str);
            result = max_num;
        } else {
            if (diff_str_neg(min_str)) {
                min_str = min_str.substring(1, min_str.length());
                Log.d("HXTP", min_str);
                min_num = Integer.parseInt(min_str);
                min_num = 0 - min_num;
            } else {
                Log.d("HXTP", min_str);
                min_num = Integer.parseInt(min_str);
            }
            result = min_num;
        }

        Log.d("HXTP", Integer.toString(max_tag_position) + "  " + max_str + "__" + min_str +
                "num" + Integer.toString(min_num) + " x " + Integer.toString(max_num));

        return result;
    }
}
