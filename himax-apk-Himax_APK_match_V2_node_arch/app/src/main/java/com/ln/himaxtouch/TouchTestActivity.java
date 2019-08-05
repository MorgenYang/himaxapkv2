package com.ln.himaxtouch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ln.himaxtouch.TouchTest.AboutTestActivity;
import com.ln.himaxtouch.TouchTest.AccuracyActivity;
import com.ln.himaxtouch.TouchTest.ConfigurationActivity;
import com.ln.himaxtouch.TouchTest.DiagonalActivity;
import com.ln.himaxtouch.TouchTest.ManualEdgeActivity;
import com.ln.himaxtouch.TouchTest.ManualEventRateActivity;
import com.ln.himaxtouch.TouchTest.ManualTwoPointActivity;
import com.ln.himaxtouch.TouchTest.SensitivityActivity;
import com.ln.himaxtouch.TouchTest.SensitivityEdgeActivity;
import com.ln.himaxtouch.TouchTest.TestResultActivity;
import com.ln.himaxtouch.TouchTest.HorizontalActivity;
import com.ln.himaxtouch.TouchTest.VerticalActivity;

public class TouchTestActivity extends Activity {
    int itemid = 1;
    int itemid2 = 2;
    int itemid3 = 3;
    int check =9;
    ListView listView;
    TestItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test);

        listView = (ListView) findViewById(R.id.touch_test_view);
        String[] ids = new String[check];
        for (int i= 0; i < ids.length; i++){
            ids[i] = Integer.toString(i+1);
        }

        adapter = new TestItemAdapter(this,R.layout.activity_test_list, ids);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                ListView temp_list = (ListView) arg0;
                String temp_str = temp_list.getItemAtPosition(arg2).toString();
                Log.w("temp_str",temp_str);
                boolean flag = true;

                //first setup to check cfg,if no data goto settings page to fill
                SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
                if (    data.getString("bg_color_text","").equals("")
                        ||data.getString("lcd_height_text","").equals("")
                        ||data.getString("lcd_width_text","").equals("")
//                        ||data.getString("accuracy_total_test_times_text","").equals("")
                        ||data.getString("accuracy_center_threshold_text","").equals("")
                        ||data.getString("accuracy_edge_threshold_text","").equals("")
                        ||data.getString("accuracy_click_num_text","").equals("")
//                        ||data.getString("linearity_each_line_times_text","").equals("")
//                        ||data.getString("linearity_total_test_times_text","").equals("")
                        ||data.getString("lineation_center_threshold_text","").equals("")
                        ||data.getString("lineation_edge_threshold_text","").equals("")
                        ||data.getString("test_bar_radius_text","").equals("")
//                        ||data.getString("loop_times_text","").equals("")
//                        ||data.getString("long_test_times_text","").equals("")
//                        ||data.getString("sen_gap_text","").equals("")
                        ) {
                    flag = false;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if(temp_str.equals("1")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, AccuracyActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("2")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, DiagonalActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("3")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, HorizontalActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("4")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, VerticalActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("5")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, SensitivityActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("6")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, SensitivityEdgeActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("7")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, ManualTwoPointActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("8")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, ManualEdgeActivity.class);
                    startActivity(intent);
                }
                else if(temp_str.equals("9")){
                    intent.putExtras(bundle);
                    if (!flag){
                        Toast.makeText(TouchTestActivity.this,"please enter settings now!",Toast.LENGTH_LONG).show();
                        intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                        startActivity(intent);
                        return;
                    }
                    intent.setClass(TouchTestActivity.this, ManualEventRateActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, itemid, 0, "Settings");
        menu.add(0, itemid2, 0, "Test Result");
        menu.add(0, itemid3, 0, "About");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch(item.getItemId()){
            case 2:
                intent.putExtras(bundle);
                intent.setClass(TouchTestActivity.this, TestResultActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent.putExtras(bundle);
                intent.setClass(TouchTestActivity.this, ConfigurationActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent.putExtras(bundle);
                intent.setClass(TouchTestActivity.this, AboutTestActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
