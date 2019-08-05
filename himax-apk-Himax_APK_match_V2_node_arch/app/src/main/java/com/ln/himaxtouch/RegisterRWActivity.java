package com.ln.himaxtouch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterRWActivity extends Activity implements View.OnClickListener
{
	public String proc_register_node;
	public String proc_dir_node;
	SharedPreferences sh_settings;
	public int screen_width;
	public int screen_height;
	public Spinner register_w_length_spinner;
	public Button register_read_btn;
	public Button register_write_btn;
	public EditText register_addr_edittext;
	public EditText command_input[];
	public ListView register_list_view;
	public EditText register_value;
	public CheckBox register_config_bank_checkBox;
	public RgisterListItemAdapter adapter;

	LinearLayout command_layout;
	LinearLayout command_list[];
	LinearLayout result_layer;
	LinearLayout.LayoutParams command_list_para;
	LinearLayout.LayoutParams diff_2part;
	LinearLayout.LayoutParams diff_4part;
	LinearLayout.LayoutParams diff_1part;

	TextView show0X[];
	TextView read_reult;
	GridView result_show;

	String processed_result[] = new String[128];

	Result_adapter result_adapter;

	int write_num=0;

	//Native function
	public native String  writeCfg(String[] stringArray);
	public native String  readCfg(String[] stringArray);
			
	static 
	{
		System.loadLibrary("HimaxAPK");
	}
	//
	String retry_readcfg(int retry,String[] command)
	{
		String result="";

		while(retry>0)
		{
			result= readCfg(command);
			if(result==null || result=="" || result.length()==0)
			{
				retry--;
				if(retry==0)
					result="";
				continue;
			}
			else
				break;
		}
		return result;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_rw);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screen_width = size.x;
		screen_height = size.y;

		command_layout = (LinearLayout)findViewById(R.id.command_layer);

		command_list_para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		diff_2part = new LinearLayout.LayoutParams(screen_width/2,LinearLayout.LayoutParams.MATCH_PARENT);
		diff_4part = new LinearLayout.LayoutParams(screen_width/5,LinearLayout.LayoutParams.WRAP_CONTENT);
		diff_1part = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		sh_settings = this.getSharedPreferences("HIAPK", 0);
		proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
		proc_register_node = proc_dir_node+sh_settings.getString("SETUP_REGISTER_NODE", "register");


		register_config_bank_checkBox = (CheckBox) findViewById(R.id.register_config_bank_checkBox);
		
		register_read_btn = (Button) findViewById(R.id.register_read_BTN);
		register_read_btn.setOnClickListener(this);
		
		register_write_btn = (Button) findViewById(R.id.register_write_BTN);
		register_write_btn.setOnClickListener(this);
		
		register_addr_edittext = (EditText) findViewById(R.id.register_addr_text);
		register_addr_edittext.setLayoutParams(diff_2part);

		register_w_length_spinner = (Spinner) findViewById(R.id.register_write_length_spinner);
		register_w_length_spinner.setLayoutParams(diff_2part);

		read_reult = (TextView) findViewById(R.id.read_result);

		result_layer = (LinearLayout) findViewById(R.id.result_layer);
		result_show = new GridView(this);



		String write_num_list[] = new String[9];

		for(int i=0;i<9;i++)
			write_num_list[i] = Integer.toString(i);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rw_write_number_spinner,write_num_list);

		register_w_length_spinner.setAdapter(adapter);
		register_w_length_spinner.setSelection(0);
		register_w_length_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				command_layout.removeAllViews();
				write_num=position;

				if(position == 0)
				{
					Log.d("HXTP","No set Write command number!");
					command_layout.setVisibility(View.INVISIBLE);
					command_layout.removeAllViews();
					command_input = null;
					show0X = null;
					return;
				}

				command_list = new LinearLayout[position];
				show0X = new TextView[position];
				command_input = new EditText[position];
				for (int i = 0; i < position; i++) {

					command_layout.setVisibility(View.VISIBLE);
					command_list[i] = new LinearLayout(getBaseContext());
					command_input[i] = new EditText(RegisterRWActivity.this);
					show0X[i] = new TextView(RegisterRWActivity.this);
					command_input[i].setText("");
					command_input[i].setGravity(Gravity.CENTER);
					show0X[i].setText(":0x");
					command_input[i].setEnabled(true);
					command_input[i].setLayoutParams(diff_1part);
					command_list[i].addView(show0X[i]);
					command_list[i].addView(command_input[i]);

					command_layout.addView(command_list[i]);

					/*
					if(i==0)
					{
						command_list[i] = new  LinearLayout(getBaseContext());
						command_list[i].setLayoutParams(command_list_para);
						command_layout.addView(command_list[i]);
					}
					else if(i==4){
						command_list[i/4] = new  LinearLayout(getBaseContext());
						command_list[i/4].setLayoutParams(command_list_para);
						command_layout.addView(command_list[i/4]);
					}

					if (i >= 0) {
						command_layout.setVisibility(View.VISIBLE);
						command_input[i] = new EditText(RegisterRWActivity.this);
						show0X[i] = new TextView(RegisterRWActivity.this);
						command_input[i].setText("");
						command_input[i].setGravity(Gravity.CENTER);
						show0X[i].setText(":0x");
						command_input[i].setEnabled(true);
						command_input[i].setLayoutParams(diff_4part);
						command_list[i/4].addView(show0X[i]);
						command_list[i/4].addView(command_input[i]);
					}
					else
						command_layout.removeAllViews();
					*/
				}


			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});





	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_rw, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) 
	{
		String addr = register_addr_edittext.getText().toString();
		String result="";
		String command = "w:x";
		String error_msg = "Register Value Error(index :";
		
		int write_length;
		String[] strings = {proc_register_node, "w:x82\n"};
		//Toast.makeText(this, strings[0], Toast.LENGTH_SHORT).show();
		addr = addr.trim();
		if(addr.length() == 1)
		{
			addr = "0" + addr;
		}
		
		if(arg0 == register_read_btn)
		{
			result_layer.removeAllViews();
			if(addr.length() !=2 &&  addr.length() !=8)
			{
				if(diff_FE(addr)==0) {
					Toast.makeText(this, "Register Address Error.", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if(StringIsHex(addr))
			{
				//Sense off
				//strings[1] = "w:x82\n";
				//writeCfg(strings);
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Toast.makeText(this, addr, Toast.LENGTH_SHORT).show();
				//Register read command

			
				if(register_config_bank_checkBox.isChecked()) //Config bank register
				{
					if(addr.length()!=2)
					{
						Log.e("HXTPE","In HW Register Read, wrong address!");
						return;
					}
					else {
						strings[1] = "r:xFE" + addr + "\n";
						Log.d("HXTP", "NOw command=" + strings[1]);
						writeCfg(strings);
					}
				}
				else //Normal register
				{
					strings[1] = "r:x"+addr+"\n";
					Log.d("HXTP","NOw command="+strings[1]);
					writeCfg(strings);
					//Read the result
					Log.d("HXTP","No set FE read");
					result = retry_readcfg(3, strings);
					//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
				}
				//Parse the result
				if(result == null || result.length()==0)
				{
					Toast.makeText(this, "Register Read Fail.", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{
					Log.d("HXTP","Read Cmmand prepare");
					//ParseRegisterRead(result);
					parsing_result(result);
					String test_result[] = new String[256];
					int coounter =0;
					for(int i =0;i<256;i++)
					{

						if(i%2==0)
						{
							test_result[i]=format_hex(Integer.toHexString(coounter).toString());
						}
						else
							test_result[i]= processed_result[coounter++];
					}

					result_adapter = new Result_adapter(getBaseContext(),R.layout.processed_result_list,test_result);


					result_show.setAdapter(result_adapter);
					result_show.setNumColumns(10);
					result_show.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screen_height / 2-50));

					result_layer.addView(result_show);
				}

				//sense on
				//strings[1] = "w:x83\n";
				//writeCfg(strings);
				if(result==null || result.length()==0)
					Toast.makeText(this, "Read Empty String Plz check Permission or command again.", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "It had read Register.", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "Register Address Error.", Toast.LENGTH_SHORT).show();
				return;
			}

		}	
		else if(arg0 == register_write_btn)
		{
			if(addr.length() > 8 || addr.length() == 0 )
			{
				if(diff_FE(addr)==0) {
					Toast.makeText(this, "Register Address Error.", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			
			if(!StringIsHex(addr))
			{
				Toast.makeText(this, "Register Address Error.", Toast.LENGTH_SHORT).show();
				return;
			}

			if(register_config_bank_checkBox.isChecked())
			{
				if(addr.length()!=2)
				{
					Log.e("HXTPE","In HW Register Write, wrong address!");
					return;
				}
				else {
					command +=("w:xFE" + addr );
					Log.d("HXTP", "NOw command=" + command);
				}
			}
			else
			{
				command += addr;
			}
			
			write_length =write_num;
			if(write_length==0)
			{
				Toast.makeText(this, "Warning: There is no input command.", Toast.LENGTH_SHORT).show();
			}
			for(int i=0;i<write_length;i++)
			{
				if(!StringIsHex(command_input[i].getText().toString()))
				{
					error_msg += Integer.toString(i);
					error_msg += ")(It is not Hex value)";
					Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(command_input[i].getText().toString().length() > 8)
				{
					error_msg += Integer.toString(i);
					error_msg += ")(Length error.)";
					Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
					return;
				}
				if(!StringIsHex(command_input[i].getText().toString()))
				{
					error_msg += Integer.toString(i);
					error_msg += ")(Not Hex Format.)";
					Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(command_input[i].getText().toString().length() < 2)
				{
					error_msg += Integer.toString(i);
					error_msg += ")(Length error.)";
					Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{
					command += ":x" +command_input[i].getText().toString();
				}
			}
			
			command += "\n";
			
			


			strings[1] = command;
			result = writeCfg(strings);
			Toast.makeText(this, "write command: "+command, Toast.LENGTH_SHORT).show();


			
			if(result.indexOf("fail")!=-1)
			{
				Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

			}
			else
			{
				result_layer.removeAllViews();
				Toast.makeText(this, "Write Success.", Toast.LENGTH_SHORT).show();

				result = retry_readcfg(3, strings);
				//ParseRegisterRead(result);
				parsing_result(result);
				String test_result[] = new String[256];
				int coounter =0;
				for(int i =0;i<256;i++)
				{

					if(i%2==0)
					{
						test_result[i]=format_hex(Integer.toHexString(coounter).toString());
					}
					else
						test_result[i]= processed_result[coounter++];
				}

				result_adapter = new Result_adapter(getBaseContext(),R.layout.processed_result_list,test_result);


				result_show.setAdapter(result_adapter);
				result_show.setNumColumns(10);
				result_show.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screen_height / 2-50));

				result_layer.addView(result_show);

			}
		}
	}
	
	private boolean StringIsHex(String input)
	{
		int counter=0;
		for(int i=0;i<input.length();i++)
		{
			if(input.substring(i, i+1).equals("0") ||
			   input.substring(i, i+1).equals("1") ||
			   input.substring(i, i+1).equals("2") ||
			   input.substring(i, i+1).equals("3") ||
			   input.substring(i, i+1).equals("4") ||
			   input.substring(i, i+1).equals("5") ||
			   input.substring(i, i+1).equals("6") ||
			   input.substring(i, i+1).equals("7") ||
			   input.substring(i, i+1).equals("8") ||
			   input.substring(i, i+1).equals("9") ||
			   input.substring(i, i+1).equals("A") ||
			   input.substring(i, i+1).equals("a") ||
			   input.substring(i, i+1).equals("B") ||
			   input.substring(i, i+1).equals("b") ||
			   input.substring(i, i+1).equals("C") ||
			   input.substring(i, i+1).equals("c") ||
			   input.substring(i, i+1).equals("D") ||
			   input.substring(i, i+1).equals("d") ||
			   input.substring(i, i+1).equals("E") ||
			   input.substring(i, i+1).equals("e") ||
			   input.substring(i, i+1).equals("F") ||
			   input.substring(i, i+1).equals("f"))
			{
				counter++;
			}
			else
				return false;

		}
		if(counter>0)
			return true;
		return false;
	}
	
	private void ParseRegisterRead(String input)
	{
		int index;
		String temp_str = "";
		if(register_config_bank_checkBox.isChecked())
		{
			for(int i=0;i<96;i++)
			{
				index = input.indexOf("0x");
				temp_str = input.substring(index+2, index+4);
				input = input.substring(index+4);
				
				if(i==0)
				{
					continue;
				}
				
				RegisterListDummyContent.ITEMS.get(i-1).content = temp_str;
			}
		}
		else
		{
			for(int i=0;i<96;i++)
			{
				index = input.indexOf("0x");

				temp_str = input.substring(index + 2, index+4);
				input = input.substring(index+4);
				
				RegisterListDummyContent.ITEMS.get(i).content = temp_str;
			}
			//Toast.makeText(this,input==null?"null":"himax "+input,Toast.LENGTH_SHORT).show();
		}
	}
	void parsing_result(String result)
	{
		int now = result.indexOf('\n');

		for(int i=0;i<128;i++)
		{
			if(i%16==15)
			{
				int str_start = now+1;
				int str_end = result.indexOf('\n',str_start);
				processed_result[i]=result.substring(str_start,str_end);
				now = str_end;
			}
			else{
				int str_start = now+1;
				int str_end = result.indexOf(' ',str_start);
				processed_result[i]=result.substring(str_start,str_end);
				now = str_end;
			}

		}
	}

	public class Result_adapter extends ArrayAdapter<String>
	{
		String[] objects;
		Context context;

		public  Result_adapter(Context context, int textViewResourceId, String[] objects)
		{
			super(context,textViewResourceId,objects);
			this.context=context;
			this.objects=objects;

		}

		public View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				tv = (TextView)inflater.inflate(R.layout.processed_result_list,parent,false);
			} else {
				tv = (TextView) convertView;
			}
			tv.setText(objects[position]);
			if (position%2==0)
				tv.setBackgroundColor(Color.parseColor("#FF8888"));
			else
				tv.setBackgroundColor(Color.parseColor("#66FF66"));

			return tv;
		}
	}

	int diff_FE(String cmd)
	{
		int FE =0;
		boolean hex;
		/*
		if(cmd.length()==2)
			return 1;
		*/
		FE=cmd.indexOf("FE");
		hex=StringIsHex(cmd.substring(2,3));


		if(FE!=-1 && hex==true && cmd.length()==4)
			return 1;
		else if(cmd.length()==8)
			return 1;
		else
			return 0;

	}
	String format_hex(String org)
	{
		if(org.length()>1)
			return org;
		else
			return "0"+org;
	}
	void waiting(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Log.d("[himax]", "wainting: fail");
		}
	}
}
