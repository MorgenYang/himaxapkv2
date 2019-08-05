package com.ln.himaxtouch;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Einim on 2016/8/23.
 */


public class MultiRegisterRWActivity extends Activity implements View.OnClickListener
{
    public native String  writeCfg(String[] stringArray);
    public native String  readCfg(String[] stringArray);
    static
    {
        System.loadLibrary("reg_rw_multi");
    }

    String proc_register_node;
    String proc_dir_node;
    SharedPreferences sh_settings;

    int screen_width;
    int screen_height;
    int rw_select_id[];
    String command[];
    String mWrite_command[];
    int command_delay[];
    int tempselect=0;
    int num=0;

    TextView permission;
    TextView title;
    TextView cmd_title[];
    TextView write_cmd_title[];
    TextView delay_title[];
    EditText cmd_num;
    EditText cmd_input[];
    EditText write_cmd_input[];
    EditText delay_input[];
    Button diff_line[];
    Button cmd_num_btn;

    RadioGroup rw_select[];
    Button run_comand;

    LinearLayout cmd_num_layer;
    LinearLayout show_input_linear_layer;
    LinearLayout show_input_rw_select_layer[];
    LinearLayout show_input_cmd_layer[];
    LinearLayout show_input_write_cmd_layer[];
    LinearLayout show_input_delay_layer[];
    LinearLayout diff_layer[];
    ScrollView show_input_scroll_layer;

    LinearLayout.LayoutParams diff2part;
    LinearLayout.LayoutParams wrap_content;
    LinearLayout.LayoutParams width_match_parent;


    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_multi_register_rw);

        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_register_node = proc_dir_node+sh_settings.getString("SETUP_REGISTER_NODE", "register");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        permission = (TextView) findViewById(R.id.permission);

        if (check_permission() == 0) {
            permission.setTextColor(Color.RED);
            permission.setText("Permission Fail");
        } else {
            permission.setTextColor(Color.GREEN);
            permission.setText("Permission Success");
        }

        diff2part = new LinearLayout.LayoutParams(screen_width/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrap_content = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        width_match_parent = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cmd_num_layer = (LinearLayout) findViewById(R.id.cmd_num_layer);
        show_input_scroll_layer = (ScrollView)findViewById(R.id.input_cmd_layer);

        title = (TextView) findViewById(R.id.title);
        cmd_num = (EditText) findViewById(R.id.cmd_num);
        cmd_num.setLayoutParams(diff2part);
        cmd_num_btn = (Button)findViewById(R.id.cmd_num_btn);
        cmd_num_btn.setLayoutParams(diff2part);
        cmd_num_btn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v)
    {
        if(v==cmd_num_btn)
        {


            try{
                num=Integer.decode(cmd_num.getText().toString());
            }
            catch (NumberFormatException e)
            {
                Log.d("Himax ", e.toString());
                Toast.makeText(MultiRegisterRWActivity.this,"It is not Decimal number",Toast.LENGTH_SHORT).show();
                return;
            }
            if(num<0)
            {
                Toast.makeText(MultiRegisterRWActivity.this,"It is negtive number",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MultiRegisterRWActivity.this,"number "+Integer.toString(num),Toast.LENGTH_SHORT).show();
            show_input_view();

        }
        if(v==run_comand)
        {
            //Toast.makeText(this,"run go"+Integer.toString(rw_select_id[0]),Toast.LENGTH_SHORT).show();
            if(num!=0)
            {

                if(checkNull(rw_select_id)!=0)
                {
                    int check =checkNull(rw_select_id);
                    Toast.makeText(this,"("+Integer.toString(check)+")R/W choose One.",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<num;i++)
                {
                    if(cmd_input[i].getText().toString().isEmpty())
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")command is empty or fail.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!StringIsHex(cmd_input[i].getText().toString()))
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")command is not HEX \n Plz check it again!.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(cmd_input[i].length()!=2 && cmd_input[i].length()!=4  && cmd_input[i].length()!=8)
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")command is wrong size.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(rw_select_id[i] == 1 && write_cmd_input[i].length()==0)
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")write command is wrong size.",Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    else if(!StringIsHex(write_cmd_input[i].getText().toString()))
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")write command is not HEX \n Plz check it again!.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(write_cmd_input[i].length()%2!=0 || write_cmd_input[i].length()>8)
                    {

                        Toast.makeText(this,"("+Integer.toString(i)+")write command is wrong size.",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if(delay_input[i].getText().toString().isEmpty())
                    {
                        Toast.makeText(this,"("+Integer.toString(i)+")Delay time is empty or fail.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    command[i]= cmd_input[i].getText().toString();
                    mWrite_command[i] = write_cmd_input[i].getText().toString();
                    //Toast.makeText(this,command[0],Toast.LENGTH_SHORT).show();
                    command_delay[i]=Integer.parseInt(delay_input[i].getText().toString());
                    //Toast.makeText(this,"String "+delay_input[i].getText().toString()+" "+command_delay[i],Toast.LENGTH_SHORT).show();
                }

                Intent intent2show = new Intent();
                intent2show.setClass(MultiRegisterRWActivity.this, Show_MultiRegisterRWActivity.class);
                intent2show.putExtra("rw_select", rw_select_id);
                intent2show.putExtra("command", command);
                intent2show.putExtra("write_command", mWrite_command);
                intent2show.putExtra("delay",command_delay);

                startActivity(intent2show);
            }



        }
    }

    int check_permission() {
        int read_result=0;

        String write_register_command[] = {proc_register_node, "r:xf6"};
        String regiter_result[] = {proc_register_node};


        String register_writer = write_command(write_register_command);
        String register_Result = retry_readcfg(3, regiter_result);

        if ((register_Result == null || register_Result.length() == 0) )
            return 0;
        int register_write_r = register_writer.matches("[\\w\\W\\s\\S.]*fail[\\w\\W\\s\\S.]*") ? 1 : 0;
        // Toast.makeText(getApplicationContext(), Result+Integer.toString(write_r), Toast.LENGTH_SHORT).show();
        if ( register_Result.length() > 0 && register_write_r == 0)
            return 1;
        return 0;
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


    String retry_readcfg(int retry,String[] command)
    {
        String result="";

        while(retry>0)
        {
            result= readCfg(command);
            if(result==null || result=="" || result.length()==0)
            {
                retry--;
                if(retry==0)
                    result="";
                continue;
            }
            else
                break;
        }
        return result;
    }

    void show_input_view()
    {
        show_input_scroll_layer.removeAllViews();


        rw_select_id = new int[num];
        Arrays.fill(rw_select_id,-1);

        command = new String[num];
        Arrays.fill(command,null);

        mWrite_command = new String[num];
        Arrays.fill(mWrite_command,null);

        command_delay = new int[num];
        Arrays.fill(command_delay,-1);

        // run_comand = (Button)findViewById(R.id.run_command_btn);
        run_comand =new Button(this);
        run_comand.findViewById(R.id.run_command_btn);
        run_comand.setBackgroundColor(Color.parseColor("#00AAAA"));
        run_comand.setText("Run Command");
        run_comand.setGravity(Gravity.CENTER_HORIZONTAL);
        run_comand.setLayoutParams(diff2part);

        show_input_linear_layer = new LinearLayout(MultiRegisterRWActivity.this);
        show_input_linear_layer.setLayoutParams(width_match_parent);
        show_input_linear_layer.setOrientation(LinearLayout.VERTICAL);

        show_input_cmd_layer =new LinearLayout[num];
        show_input_write_cmd_layer = new LinearLayout[num];
        show_input_delay_layer = new LinearLayout[num];
        show_input_rw_select_layer = new LinearLayout[num];

        cmd_title = new TextView[num];

        write_cmd_title = new TextView[num];

        delay_title = new TextView[num];

        cmd_input = new EditText[num];

        write_cmd_input = new EditText[num];

        delay_input = new EditText[num];

        rw_select = new RadioGroup[num];

        diff_layer = new LinearLayout[num];
        diff_line = new Button[num];

        for(int i=0;i<num;i++)
        {
            show_input_cmd_layer[i] = new LinearLayout(MultiRegisterRWActivity.this);
            show_input_write_cmd_layer[i] = new LinearLayout(MultiRegisterRWActivity.this);
            show_input_delay_layer[i] = new LinearLayout(MultiRegisterRWActivity.this);
            show_input_rw_select_layer[i] = new LinearLayout(MultiRegisterRWActivity.this);
            diff_layer[i] =new LinearLayout(MultiRegisterRWActivity.this);

            rw_select[i]= new RadioGroup(MultiRegisterRWActivity.this);

            show_input_rw_select_layer[i].setLayoutParams(width_match_parent);
            show_input_cmd_layer[i].setLayoutParams(width_match_parent);
            show_input_write_cmd_layer[i].setLayoutParams(width_match_parent);
            show_input_delay_layer[i].setLayoutParams(width_match_parent);
            diff_layer[i].setLayoutParams(width_match_parent);

            RadioButton read = new RadioButton(MultiRegisterRWActivity.this);
            RadioButton write = new RadioButton(MultiRegisterRWActivity.this);
            read.setText("read");
            write.setText("write");
            read.setLayoutParams(wrap_content);
            write.setLayoutParams(wrap_content);

            rw_select[i].setLayoutParams(width_match_parent);
            rw_select[i].setOrientation(LinearLayout.HORIZONTAL);
            read.setId(i * 2);
            write.setId(1 + i * 2);
            rw_select[i].addView(read);
            rw_select[i].addView(write);
            rw_select[i].setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton tempButton = (RadioButton) findViewById(checkedId);
                            int now = checkedId / 2;
                            Toast.makeText(MultiRegisterRWActivity.this, Integer.toString(checkedId) + " " + tempButton.getText().toString(), Toast.LENGTH_SHORT).show();

                            rw_select_id[now] = checkedId % 2;

                            if (rw_select_id[now] == 0) {
                                cmd_title[now].setText("Command (" + Integer.toString(now) + ")  r:x");
                                show_input_write_cmd_layer[now].setVisibility(View.INVISIBLE);
                            } else {
                                cmd_title[now].setText("Command (" + Integer.toString(now) + ")  w:x");
                                show_input_write_cmd_layer[now].setVisibility(View.VISIBLE);
                            }
                        }
                    });

            show_input_rw_select_layer[i].addView(rw_select[i]);

            cmd_title[i] = new TextView(MultiRegisterRWActivity.this);
            cmd_title[i].setText("Command (" + Integer.toString(i) + ") ");
            cmd_title[i].setLayoutParams(wrap_content);
            show_input_cmd_layer[i].addView(cmd_title[i]);

            cmd_input[i] = new EditText(MultiRegisterRWActivity.this);
            cmd_input[i].setLayoutParams(diff2part);
            show_input_cmd_layer[i].addView(cmd_input[i]);

            write_cmd_title[i] = new TextView(MultiRegisterRWActivity.this);
            write_cmd_title[i].setText("write Command (" + Integer.toString(i) + ") ");
            write_cmd_title[i].setLayoutParams(wrap_content);
            show_input_write_cmd_layer[i].addView(write_cmd_title[i]);


            write_cmd_input[i] = new EditText(MultiRegisterRWActivity.this);
            write_cmd_input[i].setLayoutParams(diff2part);
            show_input_write_cmd_layer[i].addView(write_cmd_input[i]);

            show_input_write_cmd_layer[i].setVisibility(View.INVISIBLE);


            delay_title[i] = new TextView(MultiRegisterRWActivity.this);
            delay_title[i].setText("Delay (" + Integer.toString(i) + ") ");
            delay_title[i].setLayoutParams(wrap_content);
            show_input_delay_layer[i].addView(delay_title[i]);

            delay_input[i] = new EditText(MultiRegisterRWActivity.this);
            delay_input[i].setLayoutParams(new LinearLayout.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
            delay_input[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            show_input_delay_layer[i].addView(delay_input[i]);

            diff_line[i] = new Button(MultiRegisterRWActivity.this);
            diff_line[i].setBackgroundColor(Color.parseColor("#009FCC"));
            diff_line[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50 ));
            diff_layer[i].addView(diff_line[i]);

            show_input_rw_select_layer[i].setOrientation(LinearLayout.HORIZONTAL);
            show_input_rw_select_layer[i].setTop(100);

            show_input_linear_layer.addView(show_input_rw_select_layer[i]);
            show_input_linear_layer.addView(show_input_cmd_layer[i]);
            show_input_linear_layer.addView(show_input_write_cmd_layer[i]);
            show_input_linear_layer.addView(show_input_delay_layer[i]);
            show_input_linear_layer.addView( diff_layer[i]);

        }

        run_comand.setVisibility(View.VISIBLE);
        run_comand.setOnClickListener(this);

        show_input_linear_layer.setGravity(Gravity.CENTER);


        show_input_linear_layer.addView(run_comand);
        show_input_scroll_layer.addView(show_input_linear_layer);
    }

    int checkNull(int array[])
    {
        int checked;
        for(int i=0;i<array.length;i++)
            if(array[i]<0)
                return i;
        return 0;
    }
    int checkNull(String array[])
    {
        int checked;
        for(int i=0;i<array.length;i++)
            if(array[i]==null)
                return i;
        return 0;
    }
    private boolean StringIsHex(String input)
    {
        int counter=0;
        for(int i=0;i<input.length();i++)
        {
            if(input.substring(i, i+1).equals("0") ||
                    input.substring(i, i+1).equals("1") ||
                    input.substring(i, i+1).equals("2") ||
                    input.substring(i, i+1).equals("3") ||
                    input.substring(i, i+1).equals("4") ||
                    input.substring(i, i+1).equals("5") ||
                    input.substring(i, i+1).equals("6") ||
                    input.substring(i, i+1).equals("7") ||
                    input.substring(i, i+1).equals("8") ||
                    input.substring(i, i+1).equals("9") ||
                    input.substring(i, i+1).equals("A") ||
                    input.substring(i, i+1).equals("a") ||
                    input.substring(i, i+1).equals("B") ||
                    input.substring(i, i+1).equals("b") ||
                    input.substring(i, i+1).equals("C") ||
                    input.substring(i, i+1).equals("c") ||
                    input.substring(i, i+1).equals("D") ||
                    input.substring(i, i+1).equals("d") ||
                    input.substring(i, i+1).equals("E") ||
                    input.substring(i, i+1).equals("e") ||
                    input.substring(i, i+1).equals("F") ||
                    input.substring(i, i+1).equals("f"))
            {
                counter++;
            }
            else
                return false;

        }
        if(counter==(input.length()))
            return true;
        return false;
    }
}
