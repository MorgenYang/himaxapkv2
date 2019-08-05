package com.ln.himaxtouch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Readevent extends Activity {//implements View.OnClickListener {

	int itemid_delete = 1;
	int itenid_kernel=2;
	int itenid_normal=3;
	public TextView text;
	//public Button show_msg;
	//public int show_msg_flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readevent);
		text = (TextView) findViewById(R.id.text);
		//show_msg = (Button) findViewById(R.id.show_msg_flag);
		text.setMovementMethod(new ScrollingMovementMethod());
		StringBuilder textp = new StringBuilder();
		File dir;
		File file;
		/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			dir = getApplicationContext().getExternalFilesDir(null);
		}
		else{
			dir = getApplicationContext().getFilesDir();
		}*/
		dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HimaxAPK/");
		if (!dir.exists()) {
			dir.mkdir();
			Log.e("Himax]", "Himax APK dir setup");

		}

		file = new File(dir, "Move_event_log.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				textp.append(line);
				textp.append('\n');
			}
			br.close();
			text.setText(textp.toString());
		} catch (Exception e) {
			text.setText("File does not exist\nGo to use Motion Events\n");
		}

		//show_msg.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,itenid_normal,0,"Normal log");
		menu.add(0,itenid_kernel,0,"Kernel log");
		menu.add(0, itemid_delete, 0, "Delete");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HimaxAPK/");
		File log = new File(dir, "Move_event_log.txt");
		File kernel_log = new File(dir, "Move_event_kernel_log.txt");
		StringBuilder textp = new StringBuilder();
		switch (item.getItemId()) {
			case 1:
				/*if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					File dir = getApplicationContext().getExternalFilesDir(null);
					File file = new File(dir,"Move_event_log.txt");
					file.delete();
		    		}
				
				else{
					File dir = getApplicationContext().getDir("MyFileStorage",MODE_APPEND);
					File file = new File(dir,"Move_event_log.txt");
					file.delete();
				}*/

				try {
					log.delete();
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "There is no log file, " + e.toString(), Toast.LENGTH_SHORT);
				}
				try {
					kernel_log.delete();
				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "There is no kernel log  file, " + e.toString(), Toast.LENGTH_SHORT);
				}

				Toast.makeText(getApplicationContext(), "File is removed", Toast.LENGTH_SHORT).show();
				text.setText("File is removed\n");
				break;
			case 2:
				try {
					BufferedReader br = new BufferedReader(new FileReader(kernel_log));
					String line;
					while ((line = br.readLine()) != null) {
						textp.append(line);
						textp.append('\n');
					}
					br.close();
					text.setText(textp.toString());
				} catch (Exception e) {
					text.setText("File does not exist\nGo to use Motion Events\n");
				}
				break;
			case 3:
				try {
					BufferedReader br = new BufferedReader(new FileReader(log));
					String line;
					while ((line = br.readLine()) != null) {
						textp.append(line);
						textp.append('\n');
					}
					br.close();
					text.setText(textp.toString());
				} catch (Exception e) {
					text.setText("File does not exist\nGo to use Motion Events\n");
				}
				break;
		}
		return true;
	}

	/*@Override
	public void onClick(View v)
	{
		if(v==show_msg)
		{
			File dir;
			File file;
			dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HimaxAPK/");
			if (!dir.exists()) {
				dir.mkdir();
				Log.e("Himax]", "Himax APK dir setup");

			}
			StringBuilder textp = new StringBuilder();
			if (show_msg_flag == 0) {
				show_msg_flag=2;
				file = new File(dir, "Move_event_log.txt");
				show_msg.setText("Show Kernel log");
			} else {
				show_msg_flag=0;
				file = new File(dir, "Move_event_kernel_log.txt");
				show_msg.setText("Show Normal log");
			}
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					textp.append(line);
					textp.append('\n');
				}
				br.close();
				text.setText(textp.toString());
			} catch (Exception e) {
				text.setText("File does not exist\nGo to use Motion Events\n");
			}
		}
	}*/

}
