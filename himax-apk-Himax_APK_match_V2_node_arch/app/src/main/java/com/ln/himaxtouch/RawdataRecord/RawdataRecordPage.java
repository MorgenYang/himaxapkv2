package com.ln.himaxtouch.RawdataRecord;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ln.himaxtouch.R;

import com.ln.himaxtouch.NodeDataSource;

public class RawdataRecordPage extends Activity {
    SharedPreferences mSP;
    NodeDataSource na;
    String[] list_ids = {"Hopping Noise Test","Hopping Noise Colletion Chart"};
    ArrayAdapter<String> listAdapter;

    ListView list_view;

    PageListAdapter mItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        na = new NodeDataSource(this);
        mSP = this.getSharedPreferences("HIAPK",0);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rawdatarecord);

         list_view = (ListView)findViewById(R.id.main_list_view);

        mItemAdapter = new PageListAdapter(this, R.layout.main_list_item, list_ids);
        list_view.setAdapter(mItemAdapter);



         list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                ListView temp_list = (ListView) arg0;
                String temp_str = temp_list.getItemAtPosition(position).toString();
                Toast.makeText(RawdataRecordPage.this,temp_str, Toast.LENGTH_SHORT).show();
                if(temp_str.equals(list_ids[0]))
                {
                    Intent intent = new Intent();
                    intent.setClass(RawdataRecordPage.this,HoppingNoiseGet.class);
                    startActivity(intent);
                }
                if(temp_str.equals(list_ids[1]))
                {
                    Intent intent = new Intent();
                    intent.setClass(RawdataRecordPage.this,HoppingCollectionChart.class);
                    startActivity(intent);
                }


            }
        });

    }
}
