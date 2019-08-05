package com.ln.himaxtouch.Advance;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ln.himaxtouch.HomeCatalog.HimaxListAdapter;
import com.ln.himaxtouch.R;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.ln.himaxtouch.HimaxApplication.mContext;

/**
 * Created by 903622 on 2018/6/25.
 */

public class AdvanceActivity extends Activity {
    private ListView mListView;
    private TypedArray mItemClassList;
    private HimaxListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancelist);

        mListView = (ListView) findViewById(R.id.list_view);

        TextView tv = (TextView) findViewById(R.id.iptext);
        tv.setText("IP Address:" + getLocalIpAddress());

        TypedArray itemNameList = getResources().obtainTypedArray(R.array.advancepage_item_name_list);
        mItemClassList = getResources().obtainTypedArray(R.array.advancepage_item_class_list);
        String[] itemList = new String[itemNameList.length()];
        for (int i = 0; i < itemNameList.length(); i++) {
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
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        });

    }

    public String getLocalIpAddress() {
        String no = "Not connect to internet";
        String ip;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip = inetAddress.getHostAddress().toString())) {
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpAddress's", ex.toString()); //getLocalIpAddress's SocketException:
        }
        return no;
    }


}
