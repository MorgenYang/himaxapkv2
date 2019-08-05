package com.ln.himaxtouch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.ln.himaxtouch.TouchMonitorActivity.mMenu_list.start_record;

public class TouchMonitorActivity extends Activity implements View.OnClickListener
{
	public String nodes_dir;
	public String proc_diag_node;

	public String[] touch_monitor_spinner_list = {"5","6","7","8","9","10","11","12","13","14"};
	public ArrayAdapter<String> touch_monitor_spinner_listAdapter;
	public int touch_monitor_Font = 5;

	public int[] touch_monitor_Font_Btn; //DC, IIR, IIR2, IIR2_NEG,Bank,FIR2,BASELINE
	public int touch_monitor_ID;
	/* status flag list*/
	/* Service run or not */
	private int mRunServiceFlag = 0;
	/* Notification run status*/
	private static int mNotifcationRunFlag = 0;
	private int mDataRecordFlag = 0;
	private int mRawoutSelectType =0;
	int mNowGetDataFlag =0;
	int count=0;


	private NotificationCompat.Builder mNotificationBuilder;
	private Notification mNotification;
	private NotificationManager mNotificationManager;

	public Spinner touch_monitor_Font_spi;
	public TextView touch_monitor_TEXT_view ;
	public Button touch_monitor_CLOSE_btn;
	public Button touch_monitor_DC_btn;
	public Button touch_monitor_IIR_btn;
	public Button touch_monitor_IIR2_btn;
	public Button touch_monitor_IIR_2_NEG_btn;
	public Button touch_monitor_BANK_btn;
	public Button touch_monitor_FIR2_btn;
	public Button touch_monitor_BASELINE_btn;
	//public Button touch_monitor_COLOR_btn;
	public ToggleButton touch_monitor_DUMP_btn;

	private String mVisibleMenu_str[] = {"Hide Control Bar","Visible Control Bar"};
	private int mVisibleMenu_chk = 0;

	private String mBackGround_str[] = {"White","Black"};
	private int mBackGround_chk = 0;


	String blank = "\n";
	String[] record = new String[5000];
	int mTouchEventRecordCHK =0;
	int[] check = new int[10];
	int[] x = new int[10];
	int[] y = new int[10];
	SharedPreferences sh_settings;
	int itemid = 1;
	int itemid2 = 2;
	int itemid3 = 3;
	int itemid4 = 4;
	int itemid5 = 5;
	int itemid6 = 6;
	int itemid7 = 7;
	int itemid8 = 8;
	int itemid9 = 9;
	int itemid10 = 10;
	//int itemid11 = 11;
	int itemid13 = 13;
	int itemid14 = 14;

	int record_count=0;
	int record_count_now=0;

	public himax_config mhx_config;

	public static enum mMenu_list
	{
		dsram,
		start_record,
		stop_record,
		control_bar,
		control_bar_edit,
		background,
		arrange,
		read_dc,
		read_iir,
		read_iir2,
		read_iir2_neg,
		read_bank

	}
	private static  mMenu_list mMenu_list_var;

	private int mdiag_size = 0;
	private Button mdiag_btn[];
	private String mdiag_btn_name[] ;
	private String mdiag_btn_value[];

	//Native function
	public native String  writeCfg(String[] stringArray);
	public native String  readCfg(String[] stringArray);

	static
	{
		System.loadLibrary("HimaxAPK");
	}
	//

	//Run thread
	private boolean mRunWorkingFlag = false;
	public long srate = 10;
	private mBroadCastReciever mbr;
	private String mResultFromBroadcast;
	private Handler handler = new Handler();
	private Handler mtestReceive = new Handler();

	private Runnable task = new Runnable()
	{
		public void run()
		{
			File dir;
			File file;
			String[] strings = {proc_diag_node};
			String blank = "\n";
			String end="Getting Over\n";
			if (mRunWorkingFlag)
			{
				if(mRunServiceFlag ==1)
				{

					touch_monitor_TEXT_view.setText(mResultFromBroadcast);

					if(mNotifcationRunFlag == 0)
					{
						mRunServiceFlag = 0;
						Toast.makeText(TouchMonitorActivity.this, "StopService !\n", Toast.LENGTH_SHORT).show();

						mService = null;
						Intent intent = new Intent(TouchMonitorActivity.this,GetRawdataService.class);
						stopService(intent);

						mRunWorkingFlag =false;
						touch_monitor_ID = 0;
						mNowGetDataFlag =0;
						mRawoutSelectType = 0;
						String[] closecmd = {proc_diag_node, "0\n"};
						writeCfg(closecmd);
						touch_monitor_TEXT_view.setText("CLOSE");
						mNotificationManager.cancel(5);
					}
					else
						mtestReceive.postDelayed(this,20);
				}
				else{
					handler.postDelayed(this, srate);
					String result = "\n";
					String line = null;
					line = readCfg(strings);
					Log.d("HXTPTEST",line);
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssss");
					String date = sDateFormat.format(new java.util.Date());

					result +=("Rawout select "+Integer.toString(touch_monitor_ID)+"\n"+(line+"\n"));
					if(mDataRecordFlag ==1){
						/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
							dir = getApplicationContext().getExternalFilesDir(null);
						}
						else{
							dir = getApplicationContext().getFilesDir();
						}*/
						dir = new File(mhx_config.mHXPath);

						if(mRawoutSelectType ==1){
							file = new File(dir,"IIR_log.txt");
						}
						else if(mRawoutSelectType ==2){
							file = new File(dir,"DC_log.txt");
						}
						else if(mRawoutSelectType ==3){
							file = new File(dir,"BANK_log.txt");
						}
						else if(mRawoutSelectType ==4){
							file = new File(dir,"IIR2_log.txt");
						}
						else if(mRawoutSelectType ==5){
							file = new File(dir,"IIR2_Neg_log.txt");
						}
						else if(mRawoutSelectType ==6){
							file = new File(dir,"FIR2_log.txt");
						}
						else if(mRawoutSelectType ==7){
							file = new File(dir,"BASELINE_log.txt");
						}
						else{
							file = null;
						}

						if (mRawoutSelectType != 0)
						{
							/*if(record_count!=0) {
								//Toast.makeText(TouchMonitorActivity.this, Integer.toString(record_count), Toast.LENGTH_SHORT).show();
								//record_count=0;
							}*/
							FileOutputStream out;
							try {
								out = new FileOutputStream(file,true);
								out.write(date.getBytes());
								out.write(("\t"+Integer.toString(record_count_now)+" times\n").getBytes());
								out.write(result.getBytes());
								out.write(blank.getBytes());
								if(record_count_now==record_count) {
									out.write(end.getBytes());
									mDataRecordFlag =0;
									mTouchEventRecordCHK =0;
									Toast.makeText(TouchMonitorActivity.this, "Total "+Integer.toString(record_count_now)+"Times and Over!! ", Toast.LENGTH_SHORT).show();
								}
								else
									record_count_now++;
								out.flush();
								out.close();
							}
							catch (Exception e) {
								Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
							}
						}
					}
					touch_monitor_TEXT_view.setText(result);
				}
			}


		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{


		super.onCreate(savedInstanceState);
		mNotificationSetup();
		mbr = new mBroadCastReciever();
		IntentFilter filter = new IntentFilter();
		filter.addAction("himax.broadcast.rawdata");
		this.registerReceiver(mbr,filter);

		mhx_config = new himax_config(this);

		setContentView(R.layout.activity_touch_monitor);

		sh_settings = this.getSharedPreferences("HIAPK", 0);
		touch_monitor_Font = sh_settings.getInt("TOUCH_MONITOR_FONT", 5);
		nodes_dir = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
		proc_diag_node =nodes_dir+sh_settings.getString("SETUP_DIAG_NODE", "diag");
		Log.d("HXTP","Now dir = "+proc_diag_node);
		File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HimaxAPK/");
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		touch_monitor_CLOSE_btn = (Button) findViewById(R.id.touch_monitor_CLOSE_BTN);
		touch_monitor_CLOSE_btn.setOnClickListener(this);

		/* diag button*/
		mInitDiagBtnVal();
		mSetDiagBtnValfromSP();
		mSetDiagViewPage(0);

		/*font size*/
		touch_monitor_TEXT_view = (TextView) findViewById(R.id.touch_monitor_textview);
		touch_monitor_TEXT_view.setTextSize(touch_monitor_Font);
		touch_monitor_TEXT_view.setMovementMethod(new ScrollingMovementMethod());


		touch_monitor_Font_spi = (Spinner) findViewById(R.id.touch_monitor_spinner);

		touch_monitor_spinner_listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line ,touch_monitor_spinner_list);
		touch_monitor_Font_spi.setAdapter(touch_monitor_spinner_listAdapter);
		touch_monitor_Font_spi.setOnItemSelectedListener(
				new Spinner.OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
					{
						touch_monitor_Font = Integer.parseInt(arg0.getSelectedItem().toString());
						touch_monitor_TEXT_view.setTextSize(touch_monitor_Font);

						SharedPreferences.Editor temp_pe = sh_settings.edit();
						temp_pe.putInt("TOUCH_MONITOR_FONT", touch_monitor_Font);
						temp_pe.commit();

						if (touch_monitor_ID != 0)
							touch_monitor_Font_Btn[touch_monitor_ID-1] = touch_monitor_Font;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0)
					{
						//
					}
				}
		);
		touch_monitor_Font_spi.setSelection(touch_monitor_Font-5);


	}
	/*set data to shared preference
	* */
	private int mSetDiagBtn2SP(int count, String[] diag_name, String[] diag_value)
	{
		int result = 0;

		mhx_config.mSetSharedSettting(this,"DIAG_COUNT",Integer.toString(mdiag_size));

		for(int i = 0;i<count;i++)
		{
			mhx_config.mSetSharedSettting(this,"diag_name"+Integer.toString(i),diag_name[i]);
			mhx_config.mSetSharedSettting(this,"diag_value"+Integer.toString(i),diag_value[i]);
			Log.d("HXTP","mSetDiagBtn2SP:diag_name="+diag_name[i]);
			Log.d("HXTP","mSetDiagBtn2SP:diag_value="+diag_value[i]);
		}

		return result;
	}

	/*set(reset) diag values' variable
	* */
	private int mInitDiagBtnVal() {
		int result = 0;

		if(mdiag_size == 0)
			mdiag_size = Integer.parseInt(mhx_config.mGetSharedSettting(this,"DIAG_COUNT","4"));
		Log.d("HXTP","now mdiag_size="+Integer.toString(mdiag_size));
		touch_monitor_Font_Btn = new int[mdiag_size];
		mdiag_btn_name = new String[mdiag_size];
		mdiag_btn_value = new String[mdiag_size];
		mdiag_btn = new Button[mdiag_size];

		return result;
	}

	/*set diag buttons' value from shared preference
	* if there are settings in shared preference, it will be assigned.
	* if not will assign close,value = 0*/
	private int mSetDiagBtnValfromSP()
	{
		int result = 0;


		for(int i = 0; i < mdiag_size;i++) {
			mdiag_btn_name[i] = mhx_config.mGetSharedSettting(this, "diag_name" + Integer.toString(i), "close");
			Log.d("HXTP","mSetDiagBtnValfromSP:mdiag_btn_name="+mdiag_btn_name);
			mdiag_btn_value[i] = mhx_config.mGetSharedSettting(this,"diag_value"+Integer.toString(i),"0");
			Log.d("HXTP","mSetDiagBtnValfromSP:mdiag_btn_value="+mdiag_btn_value);
			touch_monitor_Font_Btn[i] = 4;
		}

		return result;
	}
	/*
	* draw the diag's button
	* if click_reset = 1, it will remove the buttons which is showing on the canvas.
	* if click_reset = 0, this is the first time to draw these buttons.
	* */
	private int mSetDiagViewPage(int click_reset)
	{
		int result = 0;

		LinearLayout.LayoutParams btn_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		btn_lp.setMargins(1,1,1,1);
		LinearLayout ll_ctrl_bar = (LinearLayout)findViewById(R.id.touch_monitor_sub_diag_linear_layout);
		if(click_reset==1) {
			int tmp_child_count = ll_ctrl_bar.getChildCount();
			for(int i =0;i< tmp_child_count;i++) {
				Log.d("HXTP", "getChildCount=" + Integer.toString(tmp_child_count));
				ll_ctrl_bar.removeView(ll_ctrl_bar.getChildAt(0));
			}
		}

		LinearLayout.LayoutParams ll_ctrl_bar_para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);


		for(int i = 0;i<mdiag_size;i++)
		{
			mdiag_btn[i] = new Button(TouchMonitorActivity.this);
			Log.d("HXTP", "mdiag_btn_name=" + mdiag_btn_name[i]);
			mdiag_btn[i].setText(mdiag_btn_name[i]);
			Log.d("HXTP", "mdiag_btn=" + mdiag_btn[i].getText().toString());
			ll_ctrl_bar.addView(mdiag_btn[i],btn_lp);
			mdiag_btn[i].setBackgroundColor(0xFF0088A8);
			mdiag_btn[i].setOnClickListener(this);
		}
		Log.d("HXTP", "getChildCount=" + Integer.toString(ll_ctrl_bar.getChildCount()));

		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.touch_monitor, menu);
		menu.add(0, start_record.ordinal(),0,"Start to record");
		menu.add(0,mMenu_list.stop_record.ordinal(),0,"Stop to record");
		menu.add(0,mMenu_list.control_bar.ordinal(),0,mVisibleMenu_str[mVisibleMenu_chk]);
		menu.add(0,mMenu_list.control_bar_edit.ordinal(),0,"edit control bar");
		//menu.add(0,itemid9,0,"Hide Control Bar");
		menu.add(0,mMenu_list.background.ordinal(),0,mBackGround_str[mBackGround_chk]);

		menu.add(0,mMenu_list.arrange.ordinal(),0,"Arrange");
		menu.add(0,mMenu_list.read_dc.ordinal(),0,"Read DC.txt");
		menu.add(0,mMenu_list.read_iir.ordinal(),0,"Read IIR.txt");
		menu.add(0,mMenu_list.read_iir2.ordinal(),0,"Read IIR2.txt");
		menu.add(0,mMenu_list.read_iir2_neg.ordinal(),0,"Read IIR2_Neg.txt");
		menu.add(0, mMenu_list.read_bank.ordinal(), 0, "Read BANK.txt");


		//menu.add(0,itemid7,0,"Read Move.txt");
		//menu.add(0,itemid14,0,"Text Size");

		//menu.add(0,itemid10,0,"Data Position Up");
		//menu.add(0,itemid11,0,"Data Position Down");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.touch_monitor, menu);
		if(mRunServiceFlag == 1)
			menu.findItem(R.id.service).setChecked(true);
		else
			menu.findItem(R.id.service).setChecked(false);

		menu.add(0, start_record.ordinal(),0,"Start to record");
		menu.add(0,mMenu_list.stop_record.ordinal(),0,"Stop to record");
		menu.add(0,mMenu_list.control_bar.ordinal(),0,mVisibleMenu_str[mVisibleMenu_chk]);
		menu.add(0,mMenu_list.control_bar_edit.ordinal(),0,"edit control bar");
		//menu.add(0,itemid9,0,"Hide Control Bar");
		menu.add(0,mMenu_list.background.ordinal(),0,mBackGround_str[mBackGround_chk]);

		menu.add(0,mMenu_list.arrange.ordinal(),0,"Arrange");
		menu.add(0,mMenu_list.read_dc.ordinal(),0,"Read DC.txt");
		menu.add(0,mMenu_list.read_iir.ordinal(),0,"Read IIR.txt");
		menu.add(0,mMenu_list.read_iir2.ordinal(),0,"Read IIR2.txt");
		menu.add(0,mMenu_list.read_iir2_neg.ordinal(),0,"Read IIR2_Neg.txt");
		menu.add(0, mMenu_list.read_bank.ordinal(), 0, "Read BANK.txt");

		return super.onPrepareOptionsMenu(menu);
	}
	/*Draw a dialog window for setting diag button*/
	private ScrollView mSetDiagViewDialog(EditText diag_count_val,EditText diag_name_diag[],EditText diag_val_diag[]) {

		/**/
		DisplayMetrics metrics = new DisplayMetrics();
		TouchMonitorActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int x_res = metrics.widthPixels;
		int y_res = metrics.heightPixels;
		Log.d("HXTP", "x_res=" + Integer.toString(x_res));
		Log.d("HXTP", "y_res=" + Integer.toString(y_res));

		/* Main Layer,it will include all of layer*/
		ScrollView sl = new ScrollView(TouchMonitorActivity.this);
		/* Because Srcoll view just include only one linear layer
		* we should include all layer into one linear layer*/
		LinearLayout ll = new LinearLayout(TouchMonitorActivity.this);
		/*layer parameter*/
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		ScrollView.LayoutParams slp = new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		slp.height = y_res / 10 * 7;
		slp.width = x_res / 10 * 7;
		slp.gravity = Gravity.CENTER;
		Log.d("HXTP", "llp.height=" + Integer.toString(llp.height));
		Log.d("HXTP", "llp.width=" + Integer.toString(llp.width));
		llp.weight = 1;

		ll.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams llp_per_layer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams rlp_per_layer = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		rlp_per_layer.addRule(RelativeLayout.CENTER_IN_PARENT);

		ll.setLayoutParams(llp);
		sl.setLayoutParams(slp);

		LinearLayout ll_per_layer[] = new LinearLayout[mdiag_size + 1];
		LinearLayout.LayoutParams per_field = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		per_field.width = x_res / 10 * 7 / 2;
		per_field.gravity = Gravity.CENTER;
		final TextView diag_count_title = new TextView(this);

		/*for setting the number of diag button*/
		ll_per_layer[0] = new LinearLayout(TouchMonitorActivity.this);
		;
		ll_per_layer[0].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		ll_per_layer[0].setLayoutParams(llp_per_layer);
		ll_per_layer[0].setOrientation(LinearLayout.HORIZONTAL);

		diag_count_title.setText("Diag Num:");
		diag_count_val.setText(Integer.toString(mdiag_size));

		ll_per_layer[0].addView(diag_count_title, per_field);
		ll_per_layer[0].addView(diag_count_val, per_field);
		ll.addView(ll_per_layer[0]);

		/*for setting diag buttons*/
		for (int i = 0; i < mdiag_size; i++) {
			ll_per_layer[i + 1] = new LinearLayout(TouchMonitorActivity.this);
			;


			ll_per_layer[i + 1].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

			ll_per_layer[i + 1].setLayoutParams(llp_per_layer);
			ll_per_layer[i + 1].setOrientation(LinearLayout.HORIZONTAL);


			diag_name_diag[i] = new EditText(TouchMonitorActivity.this);
			diag_name_diag[i].setBackgroundColor(0xFF0088A8);
			diag_val_diag[i] = new EditText(TouchMonitorActivity.this);

			diag_name_diag[i].setText(mdiag_btn_name[i]);
			diag_val_diag[i].setText(mdiag_btn_value[i]);

			ll_per_layer[i + 1].addView(diag_name_diag[i], per_field);
			ll_per_layer[i + 1].addView(diag_val_diag[i], per_field);

			ll.addView(ll_per_layer[i + 1]);
		}
		sl.addView(ll);

		return sl;
	}

	private void mNotificationSetup()
	{
		Intent intenta= new Intent(this,mNFBroadcastReciever.class);
		intenta.setAction("aaa");

		Intent intentb= new Intent(this,mNFBroadcastReciever.class);
		intentb.setAction("EndHXGetRawdataService");
		//		intenta[0].setComponent(new ComponentName(this,TouchMonitorActivity.class));
		PendingIntent pendingIntenta = PendingIntent.getBroadcast(this,0,intenta,0);
		PendingIntent pendingIntentb = PendingIntent.getBroadcast(this,0,intentb,0);

		mNotificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher))
				.setContentTitle("Now Getting Rawdata")
				.setContentText("Rawoutselect="+Integer.toString(touch_monitor_ID))
				.addAction(R.drawable.custom_btn_hint_blue,"end",pendingIntentb)
				//.setContent()
				//.setContentIntent(mGetDefaultIntent(this,Notification.))
				;
		mNotification = mNotificationBuilder.build();
		mNotificationManager=
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	public boolean onOptionsItemSelected(MenuItem item){

		StringBuilder textp = new StringBuilder();
		File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HimaxAPK/");;
		File file;
		int now_item_id = item.getItemId();
		//RemoteViews rvs = new RemoteViews();



// mId allows you to update the notification later on.


		if(now_item_id == R.id.service)
		{
			if(item.isChecked())
			{

				mRunServiceFlag = 0;
				Toast.makeText(TouchMonitorActivity.this, "StopService !\n", Toast.LENGTH_SHORT).show();

				mService = null;
				Intent intent = new Intent(this,GetRawdataService.class);
				stopService(intent);

				mRunWorkingFlag =false;
				touch_monitor_ID = 0;
				mNowGetDataFlag =0;
				mRawoutSelectType = 0;
				String[] strings = {proc_diag_node, "0\n"};
				writeCfg(strings);
				touch_monitor_TEXT_view.setText("CLOSE");
				mNotifcationRunFlag = 0;
				mNotificationManager.cancel(5);

				item.setChecked(false);
			}
			else
			{
				mNotifcationRunFlag = 1;
				mRunServiceFlag = 1;
				Toast.makeText(TouchMonitorActivity.this, "Service Ready to runn!\n", Toast.LENGTH_SHORT).show();
				mNotification.flags |= Notification.FLAG_INSISTENT;
				mNotification.flags |= Notification.FLAG_NO_CLEAR;
				mNotificationManager.notify(5, mNotification);


				item.setChecked(true);

			}
		}
		else  if(now_item_id == mMenu_list.start_record.ordinal()) {


			LayoutInflater inflater = LayoutInflater.from(TouchMonitorActivity.this);
			final View v = inflater.inflate(R.layout.view_dialog_counter, null);
			final EditText editor = (EditText) v.findViewById(R.id.dialog_num);
			Toast.makeText(getApplicationContext(), "Start to record", Toast.LENGTH_SHORT).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(TouchMonitorActivity.this);
			dialog.setTitle("Enter one number");
			dialog.setMessage("Input one number for recording times.\t" + "If the value is 0 , it will record untill press stop record!");
			dialog.setIcon(R.drawable.ic_launcher);
			dialog.setView(v);
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					String answer = editor.getText().toString();
					record_count = Integer.parseInt(answer);
					record_count_now = 1;
					mDataRecordFlag = 1;
					mTouchEventRecordCHK = 1;
				}
			});
			dialog.setNegativeButton("Cancel", null);
			dialog.show();
		}
		else if(now_item_id == mMenu_list.stop_record.ordinal()) {
			mDataRecordFlag = 0;
			mTouchEventRecordCHK = 0;
			Toast.makeText(getApplicationContext(), "Stop to record", Toast.LENGTH_SHORT).show();
		}
		else if(now_item_id == mMenu_list.control_bar.ordinal()) {
			if (mVisibleMenu_chk == 1) {
				LinearLayout VisibleLayoutControl;
				VisibleLayoutControl = (LinearLayout) findViewById(R.id.touch_monitor_sub_linear_layout);
				VisibleLayoutControl.setVisibility(View.VISIBLE);
				mVisibleMenu_chk = 0;
			} else {
				LinearLayout UnVisibleLayoutControl;
				UnVisibleLayoutControl = (LinearLayout) findViewById(R.id.touch_monitor_sub_linear_layout);
				UnVisibleLayoutControl.setVisibility(View.GONE);

				View decorView = getWindow().getDecorView();
				// Hide the status bar.
				int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);
				mVisibleMenu_chk = 1;
			}

		}
		else if(now_item_id == mMenu_list.background.ordinal()) {
			if (mBackGround_chk == 0) {
				touch_monitor_TEXT_view.setBackgroundColor(Color.WHITE);
				touch_monitor_TEXT_view.setTextColor(Color.BLACK);
				mBackGround_chk = 1;
			} else {
				touch_monitor_TEXT_view.setBackgroundColor(Color.BLACK);
				touch_monitor_TEXT_view.setTextColor(Color.WHITE);
				mBackGround_chk = 0;
			}
		}
		else if(now_item_id == mMenu_list.arrange.ordinal()) {
			final String[] str_arr = {"set 0", "set 1", "set 2", "set 3", "set 4", "set 5", "set 6", "set 7"};
			//LayoutInflater inflater_arr = LayoutInflater.from(TouchMonitorActivity.this);
			//final View v_arr = inflater_arr.inflate(R.layout.view_dialog_arrage, null);
			AlertDialog.Builder dialog_arr = new AlertDialog.Builder(TouchMonitorActivity.this);
			dialog_arr.setTitle("Press One choice...");
			//dialog_arr.setMessage("Please choose one kinds on arrange format!");
			dialog_arr.setItems(str_arr, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String diag_arr[] = {"/proc/android_touch/diag_arr", "0"};
					switch (which) {
						case 0:
							diag_arr[1] = "0\n";
							writeCfg(diag_arr);
							break;
						case 1:
							diag_arr[1] = "1\n";
							writeCfg(diag_arr);
							break;
						case 2:
							diag_arr[1] = "2\n";
							writeCfg(diag_arr);
							break;
						case 3:
							diag_arr[1] = "3\n";
							writeCfg(diag_arr);
							break;
						case 4:
							diag_arr[1] = "4\n";
							writeCfg(diag_arr);
							break;
						case 5:
							diag_arr[1] = "5\n";
							writeCfg(diag_arr);
							break;
						case 6:
							diag_arr[1] = "6\n";
							writeCfg(diag_arr);
							break;
						case 7:
							diag_arr[1] = "7\n";
							writeCfg(diag_arr);
							break;
					}
				}
			});
			dialog_arr.setIcon(android.R.drawable.ic_dialog_info);
			//dialog_arr.setView(v_arr);

			//dialog_arr.setNegativeButton("Cancel", null);
			dialog_arr.create();
			dialog_arr.show();
		}
		else if(now_item_id == mMenu_list.read_dc.ordinal()) {
			if (mNowGetDataFlag == 1) {
				Toast.makeText(getApplicationContext(), "please click close first", Toast.LENGTH_SHORT).show();
				return true;
			}
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir = getApplicationContext().getExternalFilesDir(null);
				}
				else{
					dir = getApplicationContext().getFilesDir();
				}*/
			file = new File(dir, "DC_log.txt");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				touch_monitor_TEXT_view.setText(textp.toString());
			} catch (Exception e) {
				touch_monitor_TEXT_view.setText("File does not exist\n");
			}
		}
		else if(now_item_id == mMenu_list.read_iir.ordinal()) {
			if (mNowGetDataFlag == 1) {
				Toast.makeText(getApplicationContext(), "please click close first", Toast.LENGTH_SHORT).show();

			}
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir = getApplicationContext().getExternalFilesDir(null);
				}
				else{
					dir = getApplicationContext().getFilesDir();
				}*/
			file = new File(dir, "IIR_log.txt");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				touch_monitor_TEXT_view.setText(textp.toString());
			} catch (Exception e) {
				touch_monitor_TEXT_view.setText("File does not exist\n");
			}
		}
		else if(now_item_id == mMenu_list.read_iir2.ordinal()) {
			if (mNowGetDataFlag == 1) {
				Toast.makeText(getApplicationContext(), "please click close first", Toast.LENGTH_SHORT).show();
				return true;
			}
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir = getApplicationContext().getExternalFilesDir(null);
				}
				else{
					dir = getApplicationContext().getFilesDir();
				}*/
			file = new File(dir, "IIR2_log.txt");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				touch_monitor_TEXT_view.setText(textp.toString());
			} catch (Exception e) {
				touch_monitor_TEXT_view.setText("File does not exist\n");
			}
		}
		else if(now_item_id == mMenu_list.read_iir2_neg.ordinal()) {
			if (mNowGetDataFlag == 1) {
				Toast.makeText(getApplicationContext(), "please click close first", Toast.LENGTH_SHORT).show();
				return true;
			}
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir = getApplicationContext().getExternalFilesDir(null);
				}
				else{
					dir = getApplicationContext().getFilesDir();
				}*/
			file = new File(dir, "IIR2_Neg_log.txt");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				touch_monitor_TEXT_view.setText(textp.toString());
			} catch (Exception e) {
				touch_monitor_TEXT_view.setText("File does not exist\n");
			}

		}
		else if(now_item_id == mMenu_list.read_bank.ordinal()) {
			if (mNowGetDataFlag == 1) {
				Toast.makeText(getApplicationContext(), "please click close first", Toast.LENGTH_SHORT).show();
				return true;
			}
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir = getApplicationContext().getExternalFilesDir(null);
				}
				else{
					dir = getApplicationContext().getFilesDir();
				}*/
			file = new File(dir, "BANK_log.txt");
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				touch_monitor_TEXT_view.setText(textp.toString());
			} catch (Exception e) {
				touch_monitor_TEXT_view.setText("File does not exist\n");
			}
		}
		else if(now_item_id == mMenu_list.control_bar_edit.ordinal()) {

			final EditText  diag_count_val = new EditText(this);
			final EditText diag_name_diag[] = new EditText[mdiag_size];

			final EditText diag_val_diag[] = new EditText[mdiag_size];

			ScrollView sl = mSetDiagViewDialog(diag_count_val,diag_name_diag,diag_val_diag);
			Log.d("HXTP","diag_count_val="+diag_count_val.getText().toString());


			AlertDialog.Builder dialog = new AlertDialog.Builder(TouchMonitorActivity.this);
			dialog.setTitle("Edit your button name and value!");
			dialog.setIcon(R.drawable.ic_launcher);
			dialog.setView(sl);
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int tmp_size = mdiag_size;
					mdiag_size = Integer.valueOf(diag_count_val.getText().toString());
					// TODO Auto-generated method stub
					for(int i = 0;i<tmp_size;i++) {
						String tmp_name = diag_name_diag[i].getText().toString();
						String tmp_value =diag_val_diag[i].getText().toString();
						mdiag_btn_name[i] = tmp_name;
						mdiag_btn_value[i] = tmp_value;

						himax_config.mLogPrinter('d', " mdiag_btn_name[i]=" + mdiag_btn_name[i]);
					}

					/*assign value to shared preference*/
					mSetDiagBtn2SP(tmp_size,mdiag_btn_name,mdiag_btn_value);

					/*resize the varible*/
					mInitDiagBtnVal();

					/*assign from shared preference*/
					mSetDiagBtnValfromSP();

					mSetDiagViewPage(1);
				}
			});
			dialog.setNegativeButton("Cancel", null);
			dialog.show();
		}
		else
		{
			himax_config.mLogPrinter('e',"fail,nothing to be done!\n");
		}

		return true;
	}

	@Override
	public void onStop()
	{
		super.onStop();
		if(mRunServiceFlag ==0) {

			String[] strings = {proc_diag_node, "0\n"};
			writeCfg(strings);
			touch_monitor_TEXT_view.setText("CLOSE");
			mRunWorkingFlag = false;
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v == touch_monitor_CLOSE_btn)
		{
			touch_monitor_ID = 0;
			mNowGetDataFlag =0;
			mRawoutSelectType = 0;
			String[] strings = {proc_diag_node, "0\n"};
			writeCfg(strings);
			touch_monitor_TEXT_view.setText("CLOSE");
			mRunWorkingFlag = false;
		}

		/*event operation of diag buttons*/
		for(int i = 0 ;i<mdiag_size;i++ )
		{
			if(v == mdiag_btn[i])
			{
				touch_monitor_ID = Integer.valueOf(mdiag_btn_value[i]);
				if(touch_monitor_ID ==  0)
				{
					touch_monitor_ID = 0;
					mNowGetDataFlag =0;
					mRawoutSelectType = 0;
					String[] strings = {proc_diag_node, "0\n"};
					writeCfg(strings);
					touch_monitor_TEXT_view.setText("CLOSE");
					mRunWorkingFlag = false;
				}
				else {
					if(mRunServiceFlag == 1)
					{
						Toast.makeText(TouchMonitorActivity.this, "RunService!\n", Toast.LENGTH_SHORT).show();



						String[] strings = {proc_diag_node, mdiag_btn_value[i] + "\n"};
						writeCfg(strings);

						Intent intenta= new Intent(getBaseContext(),GetRawdataService.class);
						startService(intenta);
						mService = null;

						mRunWorkingFlag = true;
						mtestReceive.postDelayed(task, srate);


					}
					else {
						mNowGetDataFlag = 1;
						mRawoutSelectType = Integer.valueOf(mdiag_btn_value[i]);
						if (mRunServiceFlag == 1 && touch_monitor_ID > 3) {
							Toast.makeText(TouchMonitorActivity.this, "Not support in rawout select " + Integer.toString(touch_monitor_ID), Toast.LENGTH_SHORT).show();
						}
						String[] strings = {proc_diag_node, mdiag_btn_value[i] + "\n"};
						writeCfg(strings);
						mRunWorkingFlag = true;
						handler.postDelayed(task, srate);
					}
				}
			}
			if (touch_monitor_ID != 0)
			{
				if (touch_monitor_Font_Btn[i] == 4) //default value
				{
					sh_settings = this.getSharedPreferences("HIAPK", 0);
					touch_monitor_Font_Btn[i] = sh_settings.getInt("TOUCH_MONITOR_FONT",5);
				}

				touch_monitor_TEXT_view.setTextSize(touch_monitor_Font_Btn[i]);
				touch_monitor_Font_spi.setSelection(touch_monitor_Font_Btn[i]-5);
			}

		}


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			touch_monitor_ID = 0;
			mNowGetDataFlag =0;
			mRawoutSelectType = 0;
			String[] strings = {proc_diag_node, "0\n"};
			writeCfg(strings);
			touch_monitor_TEXT_view.setText("CLOSE");
			mRunWorkingFlag = false;

		}
		return super.onKeyDown(keyCode,event);
	}

	static private GetRawdataService mService = null;
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	private class mBroadCastReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context,Intent intent)
		{
			Bundle bundle = intent.getExtras();
			mResultFromBroadcast = bundle.getString("hx_rawdata");
			//Log.d("HXTP","onReceive="+test);

		}
	}

	public static class mNFBroadcastReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context,Intent intent) {
			if (intent.getAction().indexOf("aaa")>=0) {


				Toast.makeText(context,"aaaaaaaaaaa",Toast.LENGTH_LONG).show();
				Log.d("HXTP","aaaaaaaaaaa");
			}
			else if(intent.getAction().indexOf("EndHXGetRawdataService")>=0){

				Toast.makeText(context,"End Himax Get Rawdata Service",Toast.LENGTH_LONG).show();
				Log.d("HXTP","End Himax Get Rawdata Service");
				mNotifcationRunFlag = 0;

			}

		}
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int index;
		int id;
		if(mTouchEventRecordCHK ==1){
			switch(event.getActionMasked())
			{
				case MotionEvent.ACTION_UP:
					File dir;
					File file;
					index =event.getActionIndex();
					id = event.getPointerId(index);
					x[id] = (int)event.getX(event.findPointerIndex(id));
					y[id] = (int)event.getY(event.findPointerIndex(id));
					check[id] = 0;
					record[count]="Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
					if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
						dir = getApplicationContext().getExternalFilesDir(null);
					}
					else{
						dir = getApplicationContext().getFilesDir();
					}
					file = new File(dir,"Move_log.txt");
					FileOutputStream out;
					for(int a=0;a<=count;a++){
						try {
							out = new FileOutputStream(file,true);
							out.write(record[a].getBytes());
							if(a==count){
								out.write(blank.getBytes());
								out.write(blank.getBytes());
							}
							out.flush();
							out.close();
						}
						catch (Exception e) {
							Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
						}
					}
					count=0;
					break;
				case MotionEvent.ACTION_MOVE:
					index =event.getActionIndex();
					id = event.getPointerId(index);
					for(int a=0;a<10;a++){
						if(check[a]==1){
							x[a] = (int)event.getX(event.findPointerIndex(a));
							y[a] = (int)event.getY(event.findPointerIndex(a));
							record[count]="Point:"+a+" Move (x,y)=("+x[a]+","+y[a]+")"+"\n";
							count = count+1;
						}
					}
					break;
				case MotionEvent.ACTION_DOWN:
					index =event.getActionIndex();
					id = event.getPointerId(index);
					x[id] = (int)event.getX(event.findPointerIndex(id));
					y[id] = (int)event.getY(event.findPointerIndex(id));
					check[id] = 1;
					record[count]="Point:"+id+" Down (x,y)=("+x[id]+","+y[id]+")"+"\n";
					count = count+1;
					break;

				case MotionEvent.ACTION_POINTER_DOWN:
					index =event.getActionIndex();
					id = event.getPointerId(index);
					x[id] = (int)event.getX(event.findPointerIndex(id));
					y[id] = (int)event.getY(event.findPointerIndex(id));
					check[id] = 1;
					record[count]="Point:"+id+" PointerDown (x,y)=("+x[id]+","+y[id]+")"+"\n";
					count = count+1;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					index =event.getActionIndex();
					id = event.getPointerId(index);
					x[id] = (int)event.getX(event.findPointerIndex(id));
					y[id] = (int)event.getY(event.findPointerIndex(id));
					check[id] = 0;
					record[count]="Point:"+id+" PointerUp (x,y)=("+x[id]+","+y[id]+")"+"\n";
					count = count+1;
					break;
			}}
		return true;
	}

}