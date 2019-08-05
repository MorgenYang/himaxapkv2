package com.ln.himaxtouch;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	//public String proc_register_node = "/proc/android_touch/register";

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */


	//Native function
	public native String writeCfg(String[] stringArray);

	public native String readCfg(String[] stringArray);

	static {
		System.loadLibrary("HimaxAPK");
		//System.loadLibrary("HimaxSorting");

		//HimaxSorting.SORT test = new HimaxSorting.SORT();
	}

	private int mCommonList_len = 19;
	private int mAllList_len = 19;

	private himax_config mhx_config;
	int itemid = 1;
	int itemid2 = 2;
	int itemid3 = 3;
	private int mCheck_len = mCommonList_len;
	ListView listView;
	ItemAdapter adapter;
	int is_himax = 0;

	// Storage Permissions
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mhx_config = new himax_config(this);

		mCommonList_len = mhx_config.list_item.length;
		mAllList_len = mhx_config.list_item.length;
		mCheck_len = mhx_config.list_item.length;

		setContentView(R.layout.activity_main);


		verifyStoragePermissions(this);

		TextView text = (TextView) findViewById(R.id.text);

		String tmp = getLocalIpAddress();
		text.setText("IP Address:" + tmp);
		listView = (ListView) findViewById(R.id.main_list_view);
		String[] ids = new String[mCheck_len];
		for (int i = 0; i < ids.length; i++) {

			ids[i] = Integer.toString(i + 1);
		}
		adapter = new ItemAdapter(this, R.layout.main_list_item, ids);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ListView temp_list = (ListView) arg0;
				String temp_str = temp_list.getItemAtPosition(arg2).toString();

				int idx_select = Integer.valueOf(temp_str) - 1;
				final String tmp_cnam = mhx_config.mlist_item_cname_header+mhx_config.mlist_item_cname[idx_select];

				/*if(idx_select == 13)
				{
					himax_config.mToast(MainActivity.this,"Nothing to be done!");
				}*/
				if (idx_select == 12)
				{
					String[] strings = {himax_config.mSenseNodePath, "0\n"};

					writeCfg(strings);
					himax_config.mToast(MainActivity.this,"Sense off!");

					himax_config.mWaitingTime(1000);

					strings[1] = "1\n";
					writeCfg(strings);
					himax_config.mToast(MainActivity.this,"Sense on!");
				}
				else
				{
					try {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						intent.putExtras(bundle);
//						intent.setClass(MainActivity.this, TouchMonitorActivity.class);

						intent.setClass(MainActivity.this, Class.forName(tmp_cnam));
						startActivity(intent);
					}
					catch(Exception e)
					{
						Log.e("HXTPE",e.toString());
					}

				}
			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, itemid, 0, "Start all functions");
		menu.add(0, itemid2, 0, "Close appended functions");
		menu.add(0, itemid3, 0, "About Himax APK");

		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1:
				final EditText editor = new EditText(MainActivity.this);
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("Please enter password");
				dialog.setIcon(R.drawable.ic_launcher);
				dialog.setView(editor);
				dialog.setPositiveButton("Define", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String answer = editor.getText().toString();
						if (answer.equals("himax")) {
							mCheck_len = mAllList_len;
							String[] ids = new String[mCheck_len];
							for (int i = 0; i < ids.length; i++) {

								ids[i] = Integer.toString(i + 1);
							}
							adapter = new ItemAdapter(MainActivity.this, R.layout.main_list_item, ids);
							listView.setAdapter(adapter);
							Toast.makeText(getApplicationContext(), "Password corrected", Toast.LENGTH_SHORT).show();
						} else {
							mCheck_len = mCommonList_len;
							String[] ids = new String[mCheck_len];
							for (int i = 0; i < ids.length; i++) {

								ids[i] = Integer.toString(i + 1);
							}
							adapter = new ItemAdapter(MainActivity.this, R.layout.main_list_item, ids);
							listView.setAdapter(adapter);
							Toast.makeText(getApplicationContext(), "Password Wrong", Toast.LENGTH_SHORT).show();
						}
					}
				});
				dialog.setNegativeButton("Cancel", null);
				dialog.show();
				break;
			case 2:
				mCheck_len = mCommonList_len;
				is_himax = 0;
				String[] ids = new String[mCheck_len];
				for (int i = 0; i < ids.length; i++) {

					ids[i] = Integer.toString(i + 1);
				}
				adapter = new ItemAdapter(MainActivity.this, R.layout.main_list_item, ids);
				listView.setAdapter(adapter);
				break;
			case 3:
				String versionName = "";
				try {
					PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(),  0 );
					versionName = manager.versionName;
				}catch(Exception ex) {
					ex.printStackTrace();
					Log.e("[HXTP]",ex.toString());
				}
				final EditText editor_about = new EditText(MainActivity.this);
				AlertDialog.Builder dialog_about = new AlertDialog.Builder(MainActivity.this);
				dialog_about.setTitle("It is " + versionName);
				dialog_about.setIcon(R.drawable.ic_launcher);
				dialog_about.setNegativeButton("OK", null);
				dialog_about.show();
				break;
		}
		return true;
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

	public static void verifyStoragePermissions(Activity activity) {
		// Check if we have write permission
		int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
		}
	}


}
