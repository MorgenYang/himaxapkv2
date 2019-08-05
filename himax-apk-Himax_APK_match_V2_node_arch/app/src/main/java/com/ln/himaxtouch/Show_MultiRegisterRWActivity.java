package com.ln.himaxtouch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Einim on 2016/8/24.
 */
public class Show_MultiRegisterRWActivity extends Activity {

    public native String writeCfg(String[] stringArray);

    public native String readCfg(String[] stringArray);

    static {
        System.loadLibrary("reg_rw_multi");
    }

    public String proc_register_node;
    public String proc_dir_node;
    SharedPreferences sh_settings;

    public int screen_width;
    public int screen_height;

    int total = 0;
    ScrollView Result_View;
    LinearLayout result_show_include[];
    LinearLayout result_layer;
    //TextView write_result[];
    Button result_command[];
    Button result_delay[];
    GridView result_show[];

    int rw_select[];
    String command[];
    String write_command[];
    int delay[];

    LinearLayout.LayoutParams width_match_parent;
    LinearLayout.LayoutParams all_wrap_content;

    Result_adapter result_adapter;
    String processed_result[] = new String[128];

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_show_multi_register_rw);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        Bundle input = getIntent().getExtras();
        rw_select = input.getIntArray("rw_select");
        command = input.getStringArray("command");
        write_command = input.getStringArray("write_command");
        delay = input.getIntArray("delay");

        width_match_parent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        all_wrap_content = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_register_node = proc_dir_node + sh_settings.getString("SETUP_REGISTER_NODE", "register");

        total = command.length;

        Result_View = (ScrollView) findViewById(R.id.Result_View);
        result_layer = (LinearLayout) findViewById(R.id.result_layer);

        result_show_include = new LinearLayout[total];
        result_show = new GridView[total];
        result_command = new Button[total];
        result_delay = new Button[total];
        //write_result = new TextView[total];

        Show_Result();

    }

    void waiting(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log.d("[himax]", "wainting: fail");
        }
    }

    String write_command(String[] command) {
        String write_in = writeCfg(command);
        return write_in;

    }


    String retry_readcfg(int retry, String[] command) {
        String result = "";

        while (retry > 0) {
            result = readCfg(command);
            if (result == null || result == "" || result.length() == 0) {
                retry--;
                if (retry == 0)
                    result = "";
                continue;
            } else
                break;
        }
        return result;
    }

    void parsing_result(String result) {
        int now = result.indexOf('\n');

        for (int i = 0; i < 128; i++) {
            if (i % 16 == 15) {
                int str_start = now + 1;
                int str_end = result.indexOf('\n', str_start);
                processed_result[i] = result.substring(str_start, str_end);
                now = str_end;
            } else {
                int str_start = now + 1;
                int str_end = result.indexOf(' ', str_start);
                processed_result[i] = result.substring(str_start, str_end);
                now = str_end;
            }

        }
    }

    public class Result_adapter extends ArrayAdapter<String> {
        String[] objects;
        Context context;

        public Result_adapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.objects = objects;

        }

        public View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tv = (TextView) inflater.inflate(R.layout.processed_result_list, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(objects[position]);
            if (position % 2 == 0)
                tv.setBackgroundColor(Color.parseColor("#FF8888"));
            else
                tv.setBackgroundColor(Color.parseColor("#66FF66"));

            return tv;
        }
    }

    String format_hex(String org) {
        if (org.length() > 1)
            return org;
        else
            return "0" + org;
    }

    private String mReadRegisterNode(String[] command) {
        String result = "";

        result = write_command(command);
        Log.d("HXTP", "Write result:" + result);
        try {
            Thread.sleep(30);
        } catch (Exception e) {
            Log.e("HXTP", e.toString());
        }
        result = retry_readcfg(3, command);
        Log.d("HXTP", "Read result:" + result);
        return result;
    }

    void Show_Result() {
        for (int i = 0; i < command.length; i++) {
            String cmd_show_str = "";
            String delay_show_str = "";
            String result = "";

            result_show_include[i] = new LinearLayout(this);
            result_show[i] = new GridView(this);
            result_command[i] = new Button(this);
            result_delay[i] = new Button(this);
            //write_result[i] = new TextView(this);


            result_show_include[i].setLayoutParams(width_match_parent);
            result_show[i].setLayoutParams(width_match_parent);
            result_command[i].setLayoutParams(width_match_parent);
            //write_result[i].setLayoutParams(width_match_parent);

            result_show_include[i].setOrientation(LinearLayout.VERTICAL);


            if (rw_select[i] == 0) {

                cmd_show_str = "(" + Integer.toString(i) + ")" + " r:x" + command[i];
                delay_show_str = "(" + Integer.toString(i) + ")" + "  Delay:" + Integer.toString(delay[i]) + " ms";
                // Toast.makeText(this, Integer.toString(delay[0]), Toast.LENGTH_SHORT).show();
                result_command[i].setBackgroundColor(Color.parseColor("#AA7700"));
                result_delay[i].setBackgroundColor(Color.parseColor("#CC6600"));

                String cmd[] = {proc_register_node, "r:x" + command[i] + "\n"};

                result = mReadRegisterNode(cmd);


                parsing_result(result);
                String test_result[] = new String[256];
                int coounter = 0;
                for (int j = 0; j < 256; j++) {

                    if (j % 2 == 0) {
                        test_result[j] = format_hex(Integer.toHexString(coounter).toString());
                    } else
                        test_result[j] = processed_result[coounter++];
                }

                result_adapter = new Result_adapter(getBaseContext(), R.layout.processed_result_list, test_result);

                result_command[i].setText(cmd_show_str);
                result_delay[i].setText(delay_show_str);

                result_show[i].setAdapter(result_adapter);
                result_show[i].setNumColumns(10);
                result_show[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1065));

                result_show_include[i].addView(result_command[i]);
                result_show_include[i].addView(result_show[i]);
                result_show_include[i].addView(result_delay[i]);
                result_layer.addView(result_show_include[i]);

                waiting(delay[i]);
            } else {
                cmd_show_str = "(" + Integer.toString(i) + ")" + " w:x" + command[i] + ":x" + write_command[i];
                delay_show_str = "(" + Integer.toString(i) + ")" + "  Delay:" + Integer.toString(delay[i]) + " ms";
                result_command[i].setBackgroundColor(Color.parseColor("#008888"));
                result_delay[i].setBackgroundColor(Color.parseColor("#007799"));
                String cmd[] = {proc_register_node, "w:x" + command[i] + ":x" + write_command[i] + "\n"};

                result = mReadRegisterNode(cmd);


                parsing_result(result);
                String test_result[] = new String[256];
                int coounter = 0;
                for (int j = 0; j < 256; j++) {

                    if (j % 2 == 0) {
                        test_result[j] = format_hex(Integer.toHexString(coounter).toString());
                    } else
                        test_result[j] = processed_result[coounter++];
                }

                result_adapter = new Result_adapter(getBaseContext(), R.layout.processed_result_list, test_result);

                result_command[i].setText(cmd_show_str);
                result_delay[i].setText(delay_show_str);

                result_show[i].setAdapter(result_adapter);
                result_show[i].setNumColumns(10);
                result_show[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1065));
                /*write_result[i].setText(result);
                write_result[i].setGravity(View.TEXT_ALIGNMENT_CENTER);
                write_result[i].setTextColor(Color.parseColor("#770077"));
                //write_result[i].setFontFeatureSettings();
                write_result[i].setTextSize(20);*/


                result_show_include[i].addView(result_command[i]);
                result_show_include[i].addView(result_show[i]);
                result_show_include[i].addView(result_delay[i]);
                result_layer.addView(result_show_include[i]);

                waiting(delay[i]);
            }
            Log.d("HXTP", "cmd[" + Integer.toString(i) + "]=" + command[i]);
        }

        //Result_View.addView(result_layer);

    }

}
