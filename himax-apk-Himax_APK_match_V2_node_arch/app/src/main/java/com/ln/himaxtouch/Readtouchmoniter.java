package com.ln.himaxtouch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Readtouchmoniter extends Activity {

	int itemid = 1;
	int itemid2 = 2;
	int itemid3 = 3;
	int itemid4 = 4;
	int itemid5 = 5;
	public TextView text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readtouchmoniter);
		text = (TextView) findViewById(R.id.text);
		text.setMovementMethod(new ScrollingMovementMethod());
		text.setText("Choose file to read\n");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0,itemid,0 ,"Read DC.txt");
		menu.add(0,itemid2,0,"Read IIR.txt");
		menu.add(0,itemid3,0,"Read IIR2.txt");
		menu.add(0,itemid4,0,"Read BANK.txt");
		menu.add(0,itemid5,0,"Read Move.txt");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		StringBuilder textp = new StringBuilder();
		File dir;
		File file;
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			dir = getApplicationContext().getExternalFilesDir(null);
		}
		else{
			dir = getApplicationContext().getFilesDir();
		}
		file = new File(dir,"no.txt");
		switch(item.getItemId()){
			case 1:
				file = new File(dir,"DC_log.txt");
				break;
			case 2:
				file = new File(dir,"IIR_log.txt");
				break;
			case 3:
				file = new File(dir,"IIR2_log.txt");
				break;
			case 4:
				file = new File(dir,"BANK_log.txt");
				break;
			case 5:
				file = new File(dir,"Move_log.txt");
				break;
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
		}
		catch (Exception e) {
			text.setText("File does not exist\n");
		}
		return true;		
	}
}
