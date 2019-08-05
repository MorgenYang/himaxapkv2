package com.ln.himaxtouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Einim on 2016/8/11.
 */
public class osc_hopping extends Activity implements View.OnClickListener {


    static {
        System.loadLibrary("get_permission");

    }

    public native String writeCfg(String[] stringArray);

    public native String readCfg(String[] stringArray);

    ArrayAdapter adapter;
    MyAdapter my_adapter;

    TextView permission;
    TextView RF5H;
    ListView listView;
    Button hopping_btn;
    Button set_btn;
    Button Set_btn_RF5H[];
    GridView List_RF5HX[];
    ScrollView List_RF5H;
    LinearLayout ll;

    String proc_node_dir;
    String proc_diag_node;
    String proc_register_node;
    SharedPreferences sh_settings;

    int all_x = 0;
    int all_y = 0;

    int check_p = 0;
    int now_max = 0;
    int run_end = 0;
    String sum_value[] = new String[10];
    String osc_para[] = new String[8];
    String set_btn_list[] = new String[8];
    String Toshowrawdata;

    AlertDialog dialog_hopping;
    AlertDialog dialog_show_rawdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog alertDialog;
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setNegativeButton("OK", null);
        alertDialog = alert_builder.create();
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage(Html.fromHtml("This function is only for <font color=\"red\"><b>PA5731</b></font>!"));
        alertDialog.setCancelable(false);
        alertDialog.show();


        setContentView(R.layout.activity_oschopping);
        get_all_screen();
        // Toast.makeText(this,"test",Toast.LENGTH_SHORT).show();
        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_node_dir = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_diag_node = proc_node_dir + sh_settings.getString("SETUP_DIAG_NODE", "diag");
        proc_register_node = proc_node_dir + sh_settings.getString("SETUP_REGISTER_NODE", "register");


        permission = (TextView) findViewById(R.id.osc_hopp_permision);
        RF5H = (TextView) findViewById(R.id.RF5H);
        hopping_btn = (Button) findViewById(R.id.list_btn);
        set_btn = (Button) findViewById(R.id.set_btn);
        List_RF5H = (ScrollView) findViewById(R.id.list_result);

        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        List_RF5H.addView(ll);

        String temp[] = new String[10];
        Set_btn_RF5H = new Button[8];
        List_RF5HX = new GridView[8];


        for (int j = 0; j < 8; j++) {

            set_btn_list[j] = "RF5H <= 0x0" + Integer.toString(j);
        }
        check_p = check_permission();
        if (check_p == 0) {
            permission.setTextColor(Color.RED);
            permission.setText("Permission Fail");
        } else {
            permission.setTextColor(Color.GREEN);
            permission.setText("Permission Success");
        }
        getRF5H();


        hopping_btn.setOnClickListener(this);
        set_btn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == hopping_btn) {

            ll.removeAllViews();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialog_hopping = builder.create();
            dialog_hopping.setTitle("Alert Window");
            dialog_hopping.setMessage("Dont Touch Screen!!!");
            /*dialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getBaseContext(), "Close", Toast.LENGTH_SHORT).show();
                }

            });*/
            dialog_hopping.show();


            for (int i = 0; i < 8; i++) {
                OSC_calc run_write = new OSC_calc();
                run_write.execute(i);
            }



           /*
            ll.removeAllViews();

            Toast.makeText(getBaseContext(),"Enter list btn",Toast.LENGTH_SHORT).show();
            int temp=0;
            for (int position = 0; position < 8; position++)
            {
                int sum_osc_result[]= new int[10];
                String sum_osc_result_str[] = new String[10];
                int max_osc_result[]= new int[10];
                String max_osc_result_str[] = new String[10];
                String result2print[]=new String[22];
                result2print[0]="SUM";
                result2print[11]="MAX";

                int cal_result[]=new int[2];

                int MAX_INDEX=0;
                int MAX=0;
                String change_RF5H[] ={proc_register_node,"w:xf5:x0"+Integer.toString(position)+"\n"};
                writeCfg(change_RF5H);
                waiting(30);
                osc_para[position] = "RF5H set " + Integer.toString(position);

                Set_btn_RF5H[position] = new Button(this);
                Set_btn_RF5H[position].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                Set_btn_RF5H[position].setText(osc_para[position]);

                for (int i = 0; i < 10; i++) {
                    cal_result = calc_Sum_value(position);
                    waiting(100);
                    if(cal_result[0]>MAX)
                    {
                        MAX=cal_result[0];
                        MAX_INDEX=i;
                    }
                    sum_osc_result[i] = cal_result[0];
                    result2print[i+1]=Integer.toString(cal_result[0]);
                    max_osc_result[i] = cal_result[1];
                    result2print[i+12]=Integer.toString(cal_result[1]);
                   // Toast.makeText(getBaseContext(),Integer.toString(position)+":"+osc_result_str[i],Toast.LENGTH_SHORT).show();
                }
                now_max=MAX_INDEX;

               Toast.makeText(getBaseContext(),"Click "+Integer.toString(MAX_INDEX),Toast.LENGTH_SHORT).show();
                //adapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,temp);
                my_adapter = new MyAdapter(this, R.layout.osc_list, result2print);
                List_RF5HX[position] = new GridView(this);
                List_RF5HX[position].setAdapter(my_adapter);
                List_RF5HX[position].setNumColumns(11);
                List_RF5HX[position].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 200));

                ll.addView(Set_btn_RF5H[position]);
                ll.addView( List_RF5HX[position]);
                Set_btn_RF5H[position].setOnClickListener(this);


                //List_RF5HX0.setAdapter(adapter);

            }
            getRF5H();
            */
        }
        if (v == RF5H) {
            Toast.makeText(getBaseContext(), "Enter RF5H", Toast.LENGTH_SHORT).show();
        }
        if (v == set_btn) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Choose Your Set");
            dialog.setItems(set_btn_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Toast.makeText(osc_hopping.this, "You choose " + set_btn_list[which], Toast.LENGTH_SHORT).show();
                    String read_org_write_RF5H[] = {proc_register_node, "w:xf5:x0" + Integer.toString(which) + "\n"};
                    writeCfg(read_org_write_RF5H);
                    waiting(100);
                    getRF5H();
                }
            });
            dialog.show();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0,1,0,"Show Last Rawdata");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case 1:
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(osc_hopping.this);
                    dialog_show_rawdata = builder.create();
                    dialog_show_rawdata.setTitle("Show Last rawdata");
                    dialog_show_rawdata.setMessage(Toshowrawdata);
                }
                dialog_show_rawdata.show();
                break;

        }
        return true;
    }*/

    void getRF5H() {
        String read_org_write_RF5H[] = {proc_register_node, "r:xf5\n"};
        String read_org_read_RF5H[] = {proc_register_node};

        writeCfg(read_org_write_RF5H);
        String RF5H_read = retry_readcfg(3, read_org_read_RF5H);

        String RF5H_read_result = RF5H_read.substring(RF5H_read.indexOf('\n') + 1, RF5H_read.indexOf(' ', RF5H_read.indexOf('\n')));
        // Toast.makeText(this,RF5H_read_result,Toast.LENGTH_SHORT).show();
        RF5H.setText("Now RF5H: " + RF5H_read_result);
    }

    void get_all_screen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        all_x = size.x;
        all_y = size.y;
    }

    String retry_readcfg(int retry, String[] command) {
        String result = "";

        while (retry > 0) {
            result = readCfg(command);
            // result=run_read_reult;
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

    int check_permission() {
        int read_result = 0;
        String write_diag_command[] = {proc_diag_node, "0"};
        String diag_result[] = {proc_diag_node};
        String write_register_command[] = {proc_register_node, "r:xf6"};
        String regiter_result[] = {proc_register_node};

        String diag_writer = write_command(write_diag_command);
        String diag_Result = retry_readcfg(3, diag_result);
        waiting(30);

        String register_writer = write_command(write_register_command);
        String register_Result = retry_readcfg(3, regiter_result);

        if ((diag_Result == null || diag_Result.length() == 0) || (register_Result == null || register_Result.length() == 0))
            return 0;
        int diag_write_r = diag_writer.matches("[\\w\\W\\s\\S.]*fail[\\w\\W\\s\\S.]*") ? 1 : 0;
        int register_write_r = register_writer.matches("[\\w\\W\\s\\S.]*fail[\\w\\W\\s\\S.]*") ? 1 : 0;
        // Toast.makeText(getApplicationContext(), Result+Integer.toString(write_r), Toast.LENGTH_SHORT).show();
        if (diag_Result.length() > 0 && diag_write_r == 0 && register_Result.length() > 0 && register_write_r == 0)
            return 1;
        return 0;
    }

    int[] calc_Sum_value(int RF5H) {
        int result_sum[] = new int[2];
        int SUM = 0;
        int Max = 0;
        int times = 10;
        int now_last_point = 0;
        int now_string_point = 0;
        int getvalue;
        String TXRX;
        int TX;
        int RX;
        int totalTXRX;
        String now_string = "";
        //String write_cmd = write_command(IIR);
        //writeCfg(IIR);

        String[] IIR = {proc_diag_node, "1\n"};
        String read_diag[] = {proc_diag_node};
        writeCfg(IIR);
        // run = true;
        //handler.postDelayed(task, srate);
        String result = retry_readcfg(3, read_diag);
        //Toshowrawdata=Toshowrawdata+result+"\n\n"; for debug in menu
        //getTX RX
        TXRX = result.substring(0, result.indexOf('\n'));
        TX = Integer.valueOf(TXRX.substring(TXRX.indexOf(',') - 2, TXRX.indexOf(',')));
        RX = Integer.valueOf(TXRX.substring(TXRX.indexOf(',') + 4, TXRX.indexOf(',') + 6));
        totalTXRX = TX * RX;
        // Toast.makeText(getApplicationContext(), Integer.toString(totalTXRX), Toast.LENGTH_SHORT).show();
        while (now_string.indexOf("ChannelEnd") == -1 || totalTXRX > 0) {
            if (result.indexOf(' ', now_string_point) != -1) {
                now_last_point = result.indexOf(' ', now_string_point);
                now_string = result.substring(now_string_point, now_last_point);
                now_string_point = now_last_point + 1;
            } else if (result.indexOf('\n', now_string_point) != -1) {
                now_last_point = result.indexOf('\n', now_string_point);
                now_string = result.substring(now_string_point, now_last_point);
                now_string_point = now_last_point + 1;
            }
            try {
                getvalue = Integer.valueOf(now_string);

            } catch (Exception e) {
                getvalue = -1;
            }

            // Toast.makeText(getApplicationContext(), Integer.toString(getvalue), Toast.LENGTH_SHORT).show();
            if (getvalue != -1) {
                SUM += getvalue;
                if (getvalue > Max)
                    Max = getvalue;
                totalTXRX--;
            }
            if (totalTXRX == 0)
                break;
        }
        //Toast.makeText(getApplicationContext(), Integer.toString(Max), Toast.LENGTH_SHORT).show();
        // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        result_sum[0] = SUM;
        result_sum[1] = Max;
        return result_sum;
    }

    public class MyAdapter extends ArrayAdapter<String> {

        String[] objects;
        Context context;
        int max_position;

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.objects = objects;
        }


        @Override
        public View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tv = (TextView) inflater.inflate(R.layout.osc_list, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(objects[position]);
            if (position >= 11)
                tv.setBackgroundColor(Color.parseColor("#FF8888"));
            else
                tv.setBackgroundColor(Color.parseColor("#66FF66"));

            return tv;
        }

    }

    class OSC_calc extends AsyncTask<Integer, Integer, Integer> {

        int position = 0;
        int sum_osc_result[] = new int[10];
        String sum_osc_result_str[] = new String[10];
        int max_osc_result[] = new int[10];
        String max_osc_result_str[] = new String[10];
        String result2print[] = new String[22];

        @Override
        protected Integer doInBackground(Integer... Params) {
            // in background thread

            position = Params[0];

            //int temp=0;
            //for (; position < 8; position++)
            //{


            int cal_result[] = new int[2];

            int MAX_INDEX = 0;
            int MAX = 0;
            result2print[0] = "SUM";
            result2print[11] = "MAX";
            String change_RF5H[] = {proc_register_node, "w:xf5:x0" + Integer.toString(position) + "\n"};
            writeCfg(change_RF5H);
            waiting(30);
            osc_para[position] = "RF5H set " + Integer.toString(position);


            for (int i = 0; i < 10; i++) {
                cal_result = calc_Sum_value(position);
                waiting(100);
                if (cal_result[0] > MAX) {
                    MAX = cal_result[0];
                    MAX_INDEX = i;
                }
                sum_osc_result[i] = cal_result[0];
                result2print[i + 1] = Integer.toString(cal_result[0]);
                max_osc_result[i] = cal_result[1];
                result2print[i + 12] = Integer.toString(cal_result[1]);
                // Toast.makeText(getBaseContext(),Integer.toString(position)+":"+osc_result_str[i],Toast.LENGTH_SHORT).show();
            }


            // }

            return null;
        }


        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            Set_btn_RF5H[position] = new Button(getBaseContext());
            Set_btn_RF5H[position].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            Set_btn_RF5H[position].setText(osc_para[position]);
            Set_btn_RF5H[position].setClickable(false);

            my_adapter = new MyAdapter(getBaseContext(), R.layout.osc_list, result2print);
            List_RF5HX[position] = new GridView(getBaseContext());
            List_RF5HX[position].setAdapter(my_adapter);
            List_RF5HX[position].setNumColumns(11);
            List_RF5HX[position].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 200));

            ll.addView(Set_btn_RF5H[position]);
            ll.addView(List_RF5HX[position]);
            // Set_btn_RF5H[position].setOnClickListener(getBaseContext());
            // Toast.makeText(getBaseContext(),Integer.toString(position)+":"+List_RF5HX[position],Toast.LENGTH_SHORT).show();
            getRF5H();

            if (position == 7) {
                if (dialog_hopping.isShowing())
                    dialog_hopping.dismiss();
            }
            //dialog_hopping.miss
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // in main thread
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // in main thread
        }

        protected void onCancelled(String result) {
            // in main thread
        }
    }

}