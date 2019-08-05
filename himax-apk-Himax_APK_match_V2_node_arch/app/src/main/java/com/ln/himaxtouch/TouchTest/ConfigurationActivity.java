package com.ln.himaxtouch.TouchTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.R;
import com.ln.himaxtouch.TouchTestActivity;

public class ConfigurationActivity extends Activity implements View.OnClickListener{

    TextView lcdView;
    public int screenWidth;
    public int screenHeight;

    EditText bg_color_text;
    EditText lcd_height_text;
    EditText lcd_width_text;
//    EditText accuracy_total_test_times_text;
    EditText accuracy_center_threshold_text;
    EditText accuracy_edge_threshold_text;
    EditText accuracy_click_num_text;
//    EditText linearity_each_line_times_text;
//    EditText linearity_total_test_times_text;
    EditText lineation_center_threshold_text;
    EditText lineation_edge_threshold_text;
    EditText test_bar_radius_text;
//    EditText loop_times_text;
//    EditText long_test_times_text;
    EditText sen_gap_text;

    Button save_BTN;
    CheckBox vir_key;

    public String bg_color;
    public String lcd_height;
    public String lcd_width;
//    public String accuracy_total_test_times;
    public String accuracy_center_threshold;
    public String accuracy_edge_threshold;
    public String accuracy_click_num;
//    public String linearity_each_line_times;
//    public String linearity_total_test_times;
    public String lineation_center_threshold;
    public String lineation_edge_threshold;
    public String test_bar_radius;
//    public String loop_times;
//    public String long_test_times;
    public String sen_gap;
    public boolean key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        //get touch resolution and set resolution
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics rmetrics = new DisplayMetrics();
        display.getRealMetrics(rmetrics);
        screenWidth = rmetrics.widthPixels;
        screenHeight = rmetrics.heightPixels;

        lcdView = (TextView) findViewById(R.id.lcd_size_text);
        lcdView.setText(screenWidth + "*" + screenHeight + " (pix)");

        // find node
        bg_color_text = (EditText) findViewById(R.id.bg_color_text);
        lcd_height_text = (EditText) findViewById(R.id.lcd_height_text);
        lcd_width_text = (EditText) findViewById(R.id.lcd_width_text);
//        accuracy_total_test_times_text = (EditText) findViewById(R.id.accuracy_total_test_times_text);
        accuracy_center_threshold_text = (EditText) findViewById(R.id.accuracy_center_threshold_text);
        accuracy_edge_threshold_text = (EditText) findViewById(R.id.accuracy_edge_threshold_text);
        accuracy_click_num_text = (EditText) findViewById(R.id.accuracy_click_num_text);
//        linearity_each_line_times_text = (EditText) findViewById(R.id.linearity_each_line_times_text);
//        linearity_total_test_times_text = (EditText) findViewById(R.id.linearity_total_test_times_text);
        lineation_center_threshold_text = (EditText) findViewById(R.id.lineation_center_threshold_text);
        lineation_edge_threshold_text = (EditText) findViewById(R.id.lineation_edge_threshold_text);
        test_bar_radius_text = (EditText) findViewById(R.id.test_bar_radius_text);
//        loop_times_text = (EditText) findViewById(R.id.loop_times_text);
//        long_test_times_text = (EditText) findViewById(R.id.long_test_times_text);
        sen_gap_text = (EditText) findViewById(R.id.sen_gap_text);
        vir_key = (CheckBox) findViewById(R.id.vir_key);



        //read default value
        SharedPreferences getData = getSharedPreferences("data",MODE_PRIVATE);
        bg_color = getData.getString("bg_color_text","");
        lcd_height = getData.getString("lcd_height_text","");
        lcd_width = getData.getString("lcd_width_text","");
//        accuracy_total_test_times = getData.getString("accuracy_total_test_times_text","");
        accuracy_center_threshold = getData.getString("accuracy_center_threshold_text","");
        accuracy_edge_threshold = getData.getString("accuracy_edge_threshold_text","");
        accuracy_click_num = getData.getString("accuracy_click_num_text","");
//        linearity_each_line_times = getData.getString("linearity_each_line_times_text","");
//        linearity_total_test_times = getData.getString("linearity_total_test_times_text","");
        lineation_center_threshold = getData.getString("lineation_center_threshold_text","");
        lineation_edge_threshold = getData.getString("lineation_edge_threshold_text","");
        test_bar_radius = getData.getString("test_bar_radius_text","");
//        loop_times = getData.getString("loop_times_text","");
//        long_test_times = getData.getString("long_test_times_text","");
        sen_gap = getData.getString("sen_gap_text","");
        key = getData.getBoolean("vir_key",false);

        if (bg_color.equals("")){
            //set default value
            bg_color_text.setText("111111111");
            accuracy_center_threshold_text.setText("1");
            accuracy_edge_threshold_text.setText("1.5");
            accuracy_click_num_text.setText("3");
            lineation_center_threshold_text.setText("1");
            lineation_edge_threshold_text.setText("1.5");
            test_bar_radius_text.setText("3");
            sen_gap_text.setText("3");
        }else {
            //fill latest value
            bg_color_text.setText(bg_color);
            lcd_height_text.setText(lcd_height);
            lcd_width_text.setText(lcd_width);
//        accuracy_total_test_times_text.setText(accuracy_total_test_times);
            accuracy_center_threshold_text.setText(accuracy_center_threshold);
            accuracy_edge_threshold_text.setText(accuracy_edge_threshold);
            accuracy_click_num_text.setText(accuracy_click_num);
//        linearity_each_line_times_text.setText(linearity_each_line_times);
//        linearity_total_test_times_text.setText(linearity_total_test_times);
            lineation_center_threshold_text.setText(lineation_center_threshold);
            lineation_edge_threshold_text.setText(lineation_edge_threshold);
            test_bar_radius_text.setText(test_bar_radius);
//        loop_times_text.setText(loop_times);
//        long_test_times_text.setText(long_test_times);
            sen_gap_text.setText(sen_gap);
            vir_key.setChecked(key);
        }
        save_BTN = (Button) findViewById(R.id.save_BTN);

        save_BTN.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        //get fill text
        bg_color = bg_color_text.getText().toString();
        lcd_height = lcd_height_text.getText().toString();
        lcd_width = lcd_width_text.getText().toString();
//        accuracy_total_test_times = accuracy_total_test_times_text.getText().toString();
        accuracy_center_threshold = accuracy_center_threshold_text.getText().toString();
        accuracy_edge_threshold = accuracy_edge_threshold_text.getText().toString();
        accuracy_click_num = accuracy_click_num_text.getText().toString();
//        linearity_each_line_times = linearity_each_line_times_text.getText().toString();
//        linearity_total_test_times = linearity_total_test_times_text.getText().toString();
        lineation_center_threshold = lineation_center_threshold_text.getText().toString();
        lineation_edge_threshold = lineation_edge_threshold_text.getText().toString();
        test_bar_radius = test_bar_radius_text.getText().toString();
//        loop_times = loop_times_text.getText().toString();
//        long_test_times = long_test_times_text.getText().toString();
        sen_gap = sen_gap_text.getText().toString();
        key = vir_key.isChecked();

        if (    bg_color.equals("")
                ||lcd_height.equals("")
                ||lcd_width.equals("")
//                ||accuracy_total_test_times.equals("")
                ||accuracy_center_threshold.equals("")
                ||accuracy_edge_threshold.equals("")
                ||accuracy_click_num.equals("")
//                ||linearity_each_line_times.equals("")
//                ||linearity_total_test_times.equals("")
                ||lineation_center_threshold.equals("")
                ||lineation_edge_threshold.equals("")
                ||test_bar_radius.equals("")
//                ||loop_times.equals("")
//                ||long_test_times.equals("")
                ||sen_gap.equals(""))
        {
            Toast.makeText(ConfigurationActivity.this,"some value is empty!",Toast.LENGTH_LONG).show();
            return;
        }
        int red;
        int green;
        int blue;
        String bgc = bg_color;
        if (bgc.length()!=9){
            Toast.makeText(ConfigurationActivity.this,"Color is not 9 number,please check!",Toast.LENGTH_LONG).show();
            return;
        }
        red = Integer.parseInt(bgc.substring(0,3));
        green = Integer.parseInt(bgc.substring(3,6));
        blue = Integer.parseInt(bgc.substring(6));
        if (red > 255 || green > 255 || blue > 255 ){
            Toast.makeText(ConfigurationActivity.this,bgc + "  Wrong Number! Enter Again!",Toast.LENGTH_LONG).show();
            return;
        }
        double center = Double.parseDouble(accuracy_center_threshold);
        double edge = Double.parseDouble(accuracy_edge_threshold);
        double radius = Double.parseDouble(test_bar_radius);
        if(center > radius || edge > radius){
            Toast.makeText(ConfigurationActivity.this,"ACC center threshold OR edge threshold Wrong Number!",Toast.LENGTH_LONG).show();
            return;
        }
        if(Double.parseDouble(test_bar_radius)>10){
            Toast.makeText(ConfigurationActivity.this,"Radius:" + test_bar_radius + "should <= 10!",Toast.LENGTH_LONG).show();
            return;
        }
        if(Double.parseDouble(lcd_height) > 500 || Double.parseDouble(lcd_width) > 500){
            Toast.makeText(ConfigurationActivity.this,"LCD (mm) too large!",Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();

        editor.putString("bg_color_text", bg_color);
        editor.putString("lcd_height_text", lcd_height);
        editor.putString("lcd_width_text", lcd_width);
//        editor.putString("accuracy_total_test_times_text", accuracy_total_test_times);
        editor.putString("accuracy_center_threshold_text", accuracy_center_threshold);
        editor.putString("accuracy_edge_threshold_text", accuracy_edge_threshold);
        editor.putString("accuracy_click_num_text", accuracy_click_num);
//        editor.putString("linearity_each_line_times_text", linearity_each_line_times);
//        editor.putString("linearity_total_test_times_text", linearity_total_test_times);
        editor.putString("lineation_center_threshold_text", lineation_center_threshold);
        editor.putString("lineation_edge_threshold_text", lineation_edge_threshold);
        editor.putString("test_bar_radius_text", test_bar_radius);
//        editor.putString("loop_times_text", loop_times);
//        editor.putString("long_test_times_text", long_test_times);
        editor.putString("sen_gap_text", sen_gap);
        editor.putBoolean("vir_key", key);
        editor.commit();
        Toast.makeText(this, "Save Completed.", Toast.LENGTH_SHORT).show();

        //save Complete goto TouchTestActivity ui
        Intent intent = new Intent();
        intent.setClass(ConfigurationActivity.this,TouchTestActivity.class);
        startActivity(intent);
    }
}
