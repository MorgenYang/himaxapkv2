package com.ln.himaxtouch.GeneralSettings;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ln.himaxtouch.HomeCatalog.HimaxListAdapter;
import com.ln.himaxtouch.R;


import static com.ln.himaxtouch.HimaxApplication.mContext;

/**
 * Created by 903622 on 2018/6/19.
 */

public class SettingsListActivity extends Activity{

    private ListView mListView;
    private TypedArray mItemClassList;
    private HimaxListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingslist);

        mListView = (ListView) findViewById(R.id.list_view);

        TypedArray itemNameList = getResources().obtainTypedArray(R.array.setpage_item_name_list);
        mItemClassList = getResources().obtainTypedArray(R.array.setpage_item_class_list);
        String[] itemList = new String[itemNameList.length()];
        for (int i = 0; i < itemNameList.length(); i++)
        {
            itemList[i] = itemNameList.getString(i);
        }

        mAdapter = new HimaxListAdapter(this, R.layout.main_list_item, itemList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String name = mItemClassList.getString(i);
                    Intent intent = new Intent();
                    intent.setClassName(mContext, name);
                    startActivity(intent);
                } catch(Exception e) {
                    e.fillInStackTrace();
                }
            }
        });

    }
}
