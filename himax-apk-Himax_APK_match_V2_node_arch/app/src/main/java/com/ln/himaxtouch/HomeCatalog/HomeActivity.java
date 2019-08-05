package com.ln.himaxtouch.HomeCatalog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.ln.himaxtouch.NodeDataSource;
import com.ln.himaxtouch.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 903622 on 2018/6/6.
 */

public class HomeActivity extends Activity {
    private static final String ITEM_TITLE = "Item title";
    private static final String ITEM_ICON = "Item icon";
    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    private TypedArray mItemClassList;
    private int mDisableItemStart;

    public  AlertDialog.Builder mBuilder;
    public Context mContext;

   private final static int MSG_START_ACTIVITY = 0;
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_ACTIVITY: {
                    try {
                        String name = (String) msg.obj;
                        Intent intent = new Intent();
                        intent.setClassName(mContext, name);
                        startActivity(intent);
                    } catch(Exception e) {
                        e.fillInStackTrace();
                    }
                } break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homecatalog);
        mGridView = (GridView) findViewById(R.id.grid_home);
        mContext = this;
        verifyStoragePermissions(this);

        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
        TypedArray itemNameList = getResources().obtainTypedArray(R.array.homepage_item_name_list);
        TypedArray itemIconList = getResources().obtainTypedArray(R.array.homepage_item_icon_list);
        mItemClassList = getResources().obtainTypedArray(R.array.homepage_item_class_list);

        boolean isPro = this.getIntent().getBooleanExtra("isPro", false);
        mDisableItemStart = (isPro) ? itemNameList.length()-1 : 1;
        int now_item_size = isPro == true ? itemNameList.length() : 2;

        for (int i = 0; i < now_item_size; i++)
        {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put(ITEM_TITLE, itemNameList.getString(i));
            item.put(ITEM_ICON, itemIconList.getResourceId(i, 0));
            itemList.add(item);
        }

        mGridViewAdapter = new GridViewAdapter(this, itemList, mDisableItemStart);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(GalleryOnItemClickListener);

//        if(!isNodeValid()) {
//            mBuilder = new AlertDialog.Builder(this);
//            mBuilder.setIcon(R.drawable.ic_launcher);
//            mBuilder.setTitle("Detect Nodes Invalid");
//            mBuilder.setMessage("Please make sure all steps below.\n\n1. terminal: adb root.\n2. terminal: adb shell setenforce 0.\n3. adb shell: go to driver path and type command 'chmod 777 ./*'\n\nplease press recheck after finish all steps.");
//            mBuilder.setCancelable(false);
//            mBuilder.setPositiveButton("Recheck", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if(isNodeValid()) {
//                        dialogInterface.dismiss();
//                    } else {
//                        Toast.makeText(mContext, "Please setup your driver path.", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.setClassName(mContext, "com.ln.himaxtouch.SetupActivity");
//                        startActivity(intent);
//                    }
//                }
//            });
//            mBuilder.show();
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
/* No use, Be marked in 190703 by Nim*/
//    public boolean isNodeValid() {
//        //test node.
//        NodeDataSource nd = new NodeDataSource(this);
//        //get Channels
//        int[] channels = new int[2];
//        nd.getRawDataRowAndColumn(channels, true);
//        int row = channels[0];
//        int col = channels[1];
//        int[][] frame = new int[row][col];
//        boolean testResult = nd.readSpecificDiag(2, frame, true, new StringBuilder(), true);
//        nd.readSpecificDiag(0, frame, true, new StringBuilder(), true);
//        return testResult;
//    }

    private AdapterView.OnItemClickListener GalleryOnItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mHanlder.removeMessages(MSG_START_ACTIVITY);

            if(i <= mDisableItemStart) {

                if(i == 2) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.newsunup.monitertest");
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                    return;
                }

                Message msg = Message.obtain();
                msg.what = MSG_START_ACTIVITY;
                msg.obj = mItemClassList.getString(i);
                mHanlder.sendMessageDelayed(msg, 300);
            } else {
                Toast.makeText(mContext, "You don't have permission.", Toast.LENGTH_LONG).show();
            }
        }
    };


    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    permissionList,
                    1
            );
        }
    }
}
