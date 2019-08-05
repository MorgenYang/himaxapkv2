package com.ln.himaxtouch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


public class Touchevent extends Activity {

	public TextView text;
	public Button button;
	public Button debug_level;
	public Button show_dmesg;
	int count=0;
	String blank = "\n";
	String[] record = new String[5000];
	String dmesg="";
	int check2=0;
	int debug_level_status=0;
	int show_dmesg_status=0;
	int[] check = new int[10];
	int[] x = new int[10];
	int[] y = new int[10];
	SharedPreferences sh_settings;
	public String nodes_dir;
	public String proc_debug_level_node;
	public String klog_tag;
	Long tsLong;
	String timeformat;

	//Native function
	public native String  writeCfg(String[] stringArray);
	public native String  readCfg(String[] stringArray);

	static
	{
		System.loadLibrary("HimaxAPK");
	}
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_touchevent);
		sh_settings= this.getSharedPreferences("HIAPK", 0);
		nodes_dir = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
		proc_debug_level_node = sh_settings.getString("SETUP_DEBUGLEVEL_NODE", "debug_level");
		klog_tag = sh_settings.getString("SETUP_KLOG_TAG","HXTP");

		text = (TextView) findViewById(R.id.text);
		text.setMovementMethod(new ScrollingMovementMethod());

		button = (Button)findViewById(R.id.button1);
		debug_level =  (Button)findViewById(R.id.button2);
		show_dmesg = (Button)findViewById(R.id.button3);

		String[] readcommand = {nodes_dir + proc_debug_level_node};
		String result = retry_readcfg(3, readcommand);
		debug_level_status=Integer.parseInt(result.substring(0,1));
		if(debug_level_status==2)
			debug_level.setText("set debug_level 0");
		else
			debug_level.setText("set debug_level 2");
		//Toast.makeText(this,"Now debug_lv is "+Integer.toString(debug_level_status),Toast.LENGTH_SHORT).show();

		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)&&Environment.isExternalStorageEmulated()){
			text.setText("File will be saved on Internal SD card\nPath  /Android/data/com.ln.himaxtouch/files "+getApplicationContext().getFilesDir().toString()+"\n");
		}
		else if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)&&Environment.isExternalStorageRemovable()){
			text.setText("File will be saved on External SD card\nPath  /Android/data/com.ln.himaxtouch/files"+getApplicationContext().getFilesDir().toString()+"\n");
		}
		else{
			text.setText("File will be saved on Internal storage\n");
		}

		debug_level.setOnClickListener(ClickListener);
		show_dmesg.setOnClickListener(ClickListener);
		button.setOnClickListener(ClickListener);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int index;
		int id;
		GregorianCalendar now = new GregorianCalendar();
		if(check2==0){
    	switch(event.getActionMasked())
    	{
    	case MotionEvent.ACTION_UP:

    		int savecheck = 0;
    		File dir;
    		File file;
    		index =event.getActionIndex();
    		id = event.getPointerId(index);
			tsLong= SystemClock.elapsedRealtime();
			//tsLong = java.lang.System.currentTimeMillis();
    		x[id] = (int) event.getX(event.findPointerIndex(id));
    		y[id] = (int)event.getY(event.findPointerIndex(id));
    		check[id] = 0;
			timeformat = String.format("%d.%03d",tsLong/1000,tsLong%1000);
			//timeformat = Long.toString(now.getTimeInMillis());
			//timeformat = Long.toString(now.MILLISECOND);
    		//record[count]="Time: "+df.format(tsLong)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		//record[count]="Time: "+(tsLong/1000)+"."+(tsLong%1000)+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		record[count]="Time: "+timeformat+"  Point:"+id+" Up (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
    			dir = getApplicationContext().getExternalFilesDir(null);
    		}
    		else{
    			dir = getApplicationContext().getFilesDir();
    		}*/
			dir=new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HimaxAPK/");
			if(!dir.exists()) {
				dir.mkdir();
				Log.e("[Himax]","Himax APK dir setup");

			}
    		file = new File(dir,"Move_event_log.txt");
			FileOutputStream out;
			for(int a=0;a<=count;a++){
    			text.append(record[a]);	
    			try {				
    			out = new FileOutputStream(file,true);
    			out.write(record[a].getBytes());
    			if(a==count){
    				out.write(blank.getBytes());
    				out.write(blank.getBytes());
    			}
    		    out.flush();
    		    out.close();
    		    savecheck=0;   		    
    			}
    			catch (Exception e) {
    				savecheck=1;	
    			}
    		}
    		if(savecheck==0){
    			Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();
    		}
    		else if(savecheck==1){
    			Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
    		}
			if(debug_level_status==2) {
				get_dmesg("[" + klog_tag + "]");
			}
    		check2=1;
    		count=0;
    		break;    		
    	case MotionEvent.ACTION_MOVE:
    		index =event.getActionIndex();
    		id = event.getPointerId(index);
			tsLong=  SystemClock.elapsedRealtime();
			//tsLong = java.lang.System.currentTimeMillis();
			//timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
			//timeformat = Long.toString(now.getTimeInMillis());
			timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
    		for(int a=0;a<10;a++){
    			if(check[a]==1){
    			x[a] = (int)event.getX(event.findPointerIndex(a));
					y[a] = (int)event.getY(event.findPointerIndex(a));
        		record[count]="Time: "+timeformat+"  Point:"+a+" Move (x,y)=("+x[a]+","+y[a]+")"+"\n";
        		count = count+1;
    			}
    			}  		
    		break;
    	case MotionEvent.ACTION_DOWN:
    		index =event.getActionIndex();
    		id = event.getPointerId(index);
			tsLong= SystemClock.elapsedRealtime();
			//tsLong = java.lang.System.currentTimeMillis();
			//timeformat = String.format("%d.%3d", tsLong / 1000, tsLong % 1000);
			//timeformat = Long.toString(now.getTimeInMillis());
			timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
			x[id] = (int)event.getX(event.findPointerIndex(id));
    		y[id] = (int)event.getY(event.findPointerIndex(id));
    		check[id] = 1;
    		record[count]="Time: "+timeformat+"  Point:"+id+" Down (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		count = count+1;
    		break;
    		
    	case MotionEvent.ACTION_POINTER_DOWN:
    		index =event.getActionIndex();
    		id = event.getPointerId(index);
			tsLong=  SystemClock.elapsedRealtime();
			//tsLong = java.lang.System.currentTimeMillis();
			//timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
			//timeformat = Long.toString(now.getTimeInMillis());
			timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
    		x[id] = (int)event.getX(event.findPointerIndex(id));
			y[id] = (int)event.getY(event.findPointerIndex(id));
    		check[id] = 1;
    		record[count]="Time: "+timeformat+"  Point:"+id+" PointerDown (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		count = count+1;
    		break;
    	case MotionEvent.ACTION_POINTER_UP:
    		index =event.getActionIndex();
			id = event.getPointerId(index);
			tsLong=  SystemClock.elapsedRealtime();
			//tsLong = java.lang.System.currentTimeMillis();
			//timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
			//timeformat = Long.toString(now.getTimeInMillis());
			timeformat = String.format("%d.%03d", tsLong / 1000, tsLong % 1000);
    		x[id] = (int)event.getX(event.findPointerIndex(id));
			y[id] = (int)event.getY(event.findPointerIndex(id));
    		check[id] = 0;
    		record[count]="Time: "+timeformat+"  Point:"+id+" PointerUp (x,y)=("+x[id]+","+y[id]+")"+"\n";
    		count = count+1;
    		break;
    	}}
    	
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 1, 0, "Show Kernel log");
		menu.add(0, 2, 0, "Close Kernel log");
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1:
				final EditText editor = new EditText(Touchevent.this);
				AlertDialog.Builder dialog = new AlertDialog.Builder(Touchevent.this);
				dialog.setTitle("Please enter password");
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.setView(editor);

				dialog.setPositiveButton("Define", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String answer = editor.getText().toString();
						if (answer.equals("himax")) {
							show_dmesg.setVisibility(View.VISIBLE);
							Toast.makeText(getApplicationContext(), "Password corrected and You can click button to how", Toast.LENGTH_SHORT).show();
						} else {
							show_dmesg.setVisibility(View.INVISIBLE);
							show_dmesg_status=0;
							Toast.makeText(getApplicationContext(), "Password Wrong", Toast.LENGTH_SHORT).show();
						}
					}
				});
				dialog.setNegativeButton("Cancel", null);
				dialog.show();
				break;
			case 2:
				show_dmesg.setVisibility(View.INVISIBLE);
				show_dmesg_status=0;
				Toast.makeText(getApplicationContext(), "Close for showing kernel log", Toast.LENGTH_SHORT).show();
				break;
		}
		return true;
	}
	public Button.OnClickListener ClickListener = new Button.OnClickListener(){
		public void onClick(View arg0){

			if(arg0==button)
			{
			if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)&&Environment.isExternalStorageEmulated()){
				text.setText("File will be saved on Internal SD card\nPath  /Android/data/com.ln.himaxtouch/files\n");
			}
			else if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)&&Environment.isExternalStorageRemovable()){
				text.setText("File will be saved on External SD card\nPath  /Android/data/com.ln.himaxtouch/files\n");
			}
			else{
				text.setText("File will be saved on Internal storage\n");
			}
			check2=0;
			}
			if(arg0== debug_level)
			{
				String[] readcommand = {nodes_dir + proc_debug_level_node};


				if(debug_level_status==0) {
					debug_level_status=2;
					String[] writecommand = {nodes_dir + proc_debug_level_node, "2\n"};
					writeCfg(writecommand);
					String result = retry_readcfg(3, readcommand);
					debug_level.setText("set debug_level 0");
					//Toast.makeText(getBaseContext(), "Now debug_level_status is "+Integer.toString(debug_level_status), Toast.LENGTH_SHORT).show();
					//Toast.makeText(getBaseContext(), "Now debug_level is " + result, Toast.LENGTH_SHORT).show();
				}
				else
				{

					debug_level_status=0;
					String[] writecommand = {nodes_dir + proc_debug_level_node, "0\n"};
					writeCfg(writecommand);
					String result = retry_readcfg(3, readcommand);
					debug_level.setText("set debug_level 2");
					//Toast.makeText(getBaseContext(), "Now debug_level_status is "+Integer.toString(debug_level_status), Toast.LENGTH_SHORT).show();
					//Toast.makeText(getBaseContext(), "Now debug_level is " + result, Toast.LENGTH_SHORT).show();

				}
			}

			if(arg0== show_dmesg)
			{
				if(show_dmesg_status==0)
				{
					show_dmesg_status=1;
					show_dmesg.setText("Close");
				}
				else
				{
					show_dmesg_status=0;
					show_dmesg.setText("Kernel log");
				}
			}
		}
	};

	void get_dmesg(String inlineSTR)
	{
		int savecheck = 0;
		File dir;
		File file;

		try {
			//Process pc = Runtime.getRuntime().exec("su");
			//ProcessBuilder b = new  ProcessBuilder("dmesg");
			//Process p = b.redirectErrorStream(true).start();
			Process p = Runtime.getRuntime().exec("dmesg");
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			String all="";
			Toast.makeText(this,"Kernel log tag: "+inlineSTR,Toast.LENGTH_SHORT).show();
			int leave_count=0;
			while ((line=br.readLine())!=null) {

				if(line.indexOf(inlineSTR)!=-1) {
					//text.append(line + "\n");

					if(line.indexOf("All Finger leave")!=-1)
					{
						//all += (line + "\n");
						all += (str2simple(line) + "\n");
						leave_count++;
					}
					else
					{
						//all += (line + "\n");
						all += (str2simple(line) + "\n");
					}
				}
			}
			//Toast.makeText(this,"Now leave count is "+Integer.toString(leave_count),Toast.LENGTH_SHORT).show();
			//Toast.makeText(this,"in",Toast.LENGTH_SHORT).show();
			int count=0;
			while(leave_count>1)
			{
				all=all.substring(all.indexOf("All Finger leave")+16,all.length());
				//Toast.makeText(this,"Count : "+Integer.toString(count++),Toast.LENGTH_SHORT).show();
				leave_count--;
			}


			dmesg=null;
			dmesg=all;
			if(show_dmesg_status==1)
				text.append(dmesg);

		}
		catch (IOException e)
		{
			Log.e("Himax",e.toString());
		}
		dir=new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/HimaxAPK/");
		if(!dir.exists()) {
			dir.mkdir();
			Log.e("Himax]","Himax APK dir setup");

		}
		file = new File(dir,"Move_event_kernel_log.txt");
		FileOutputStream out;

		try {
			out = new FileOutputStream(file,true);
			out.write(dmesg.getBytes());
			out.flush();
			out.close();
			savecheck=0;
		}
		catch (Exception e) {
			savecheck=1;
		}

		if(savecheck==0){
			Toast.makeText(getApplicationContext(), "Successfully save", Toast.LENGTH_SHORT).show();
		}
		else if(savecheck==1){
			Toast.makeText(getApplicationContext(), "Fail to save", Toast.LENGTH_SHORT).show();
		}
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
	String str2simple(String input)
	{
		int first_f_q = input.indexOf("[");
		int first_b_q = input.indexOf("]");
		int main = input.indexOf("["+klog_tag+"]");

		String time_str = input.substring(first_f_q, first_b_q+1);
		String main_str = input.substring(main,input.length());

		return time_str+main_str;
	}
	
}
