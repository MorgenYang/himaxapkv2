package com.ln.himaxtouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phonestate extends Activity {



	private Handler handler = new Handler();
	private Handler read_handler;
	private HandlerThread read_thread;

	Panel mPanel;
	String manufacturer;
	String model;
	String sdk;
	String dis;
	String dpi;
	String sizeo;
	String version;
	String Read_FW_ver;
	String FWver;
	String CFGver;
	Pattern pattern;
	Matcher matcher;

	String proc_dir_node;
	String proc_vendor_node;
	String proc_register_node;
	String node_string;
	SharedPreferences sh_settings;
	int xr;
	int yr;

	int readFail=0;
	int index_cfg;
	int index_fw;
	int retry=3;

	int mdetail_width = 0;
	int mdetail_height = 0;

	String string_cfg;
	String string_fw;

	Rect detail_btn;


	public native String  readCfg(String[] stringArray);
	public native String  writeCfg(String[] stringArray);
	static
	{
		System.loadLibrary("HimaxAPK");
	}

	void read_vendor()
	{
		sh_settings = this.getSharedPreferences("HIAPK", 0);
		proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
		proc_vendor_node = proc_dir_node+sh_settings.getString("SETUP_VENDOR_NODE", "vendor");
		proc_register_node = proc_dir_node+sh_settings.getString("SETUP_REGISTER_NODE", "register");
		//node_string = proc_vendor_node.toString();
		String FW_ver[] = {proc_vendor_node, "\n"};
		String rw_reg[]={proc_register_node,"w:x82\n"};
		writeCfg(rw_reg);
		String result;
		result = readCfg(FW_ver);
		Read_FW_ver = result;
		detail_btn = new Rect();

		//Toast.makeText(this, "Enter "+result, Toast.LENGTH_SHORT).show();
		while (retry > 0)
		{
			if(Read_FW_ver == null || Read_FW_ver.indexOf("fail") != -1 || Read_FW_ver.length() == 0)
			{
				retry--;
				result = readCfg(FW_ver);
				Read_FW_ver = result;
				continue;

			}
			else
			{
				readFail=0;
				int first_ = Read_FW_ver.indexOf('_');
				index_fw = Read_FW_ver.indexOf('_', first_ + 1);
				index_cfg = Read_FW_ver.indexOf('_', index_fw + 1);
				if(index_cfg!=-1 && index_fw!=-1) {
					string_fw = Read_FW_ver.substring(0, index_fw);
					string_cfg = Read_FW_ver.substring(index_fw + 1, index_cfg);
				}
				//Toast.makeText(this, Read_FW_ver, Toast.LENGTH_SHORT).show();
				//ast.makeText(this, "FW: " + Integer.toString(index_fw) + "CFG: " + Integer.toString(index_cfg), Toast.LENGTH_SHORT).show();
				break;
			}
		}
		retry=3;
		if (Read_FW_ver == null || Read_FW_ver.indexOf("fail") != -1 || Read_FW_ver.length() == 0) {
			Toast.makeText(this, "[Himax] Fail Read: "+(Read_FW_ver == null || Read_FW_ver.length() == 0?"Null":Read_FW_ver), Toast.LENGTH_SHORT).show();
			Read_FW_ver = "Null,Check permission";
			readFail = 1;
		}
		else if( string_fw==null || string_cfg==null)
		{
			Toast.makeText(this, "[Himax] Fail format: "+Read_FW_ver, Toast.LENGTH_SHORT).show();
			readFail = 1;
		}
		else if(string_fw.length()==0 || string_cfg.length()==0)
		{
			Toast.makeText(this, "[Himax] Fail format: "+Read_FW_ver, Toast.LENGTH_SHORT).show();
			readFail = 1;
		}
		rw_reg[1]="w:x83\n";
		writeCfg(rw_reg);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPanel = new Panel(this);
		mPanel.setBackgroundColor(Color.WHITE);
		setContentView(mPanel);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		int xdpi;
		int ydpi;
		DisplayMetrics metrics = new DisplayMetrics();
		DisplayMetrics rmetrics = new DisplayMetrics();
		display.getMetrics(metrics);
		display.getRealMetrics(rmetrics);
		xdpi = (int) metrics.xdpi;
		ydpi = (int) metrics.ydpi;
		xr = rmetrics.widthPixels;
		yr = rmetrics.heightPixels;
		float size;
		double xt;
		double yt;
		yt = (double) yr / (double) ydpi;
		xt = (double) xr / (double) xdpi;
		double tsize = (yt * yt) + (xt * xt);
		size = (float) Math.sqrt(tsize);
		String versionname = "";


		//int versioncode=0;
		try {
			versionname = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			//versioncode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}

		manufacturer = Build.MANUFACTURER;
		model = Build.MODEL;
		sdk = Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT;
		dis = yr + "*" + xr;
		dpi = ydpi + "*" + xdpi;
		sizeo = String.format("%.1f", size) + " inch";
		version = versionname;

		read_thread = new HandlerThread("readvendor");
		read_thread.start();
		read_handler=new Handler(read_thread.getLooper());
		read_handler.post(r1);



	}

	private Runnable r1 = new Runnable() {
		@Override
		public void run() {
			read_vendor();
		}
	};

	class Panel extends View {
		Paint paint3 = new Paint();
		Paint paint = new Paint();
		Paint paint2 = new Paint();
		Paint btn_paint = new Paint();
		
		public Panel(Context context) {
    		super(context);
    	}
    	
    	@Override
    	public void onDraw(Canvas canvas) {

//			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			float t=720;
    		float a = xr / t;
            btn_paint.setColor(Color.BLACK);
            btn_paint.setTextSize(30 * a);
			btn_paint.setAntiAlias(true); // 消除锯齿
			btn_paint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿

			paint.setColor(Color.DKGRAY);
			paint.setTextSize(30 * a);
			paint.setAntiAlias(true); // 消除锯齿
			paint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
			paint2.setColor(Color.DKGRAY);
			paint2.setTextSize(50 * a);
			paint2.setAntiAlias(true); // 消除锯齿
			paint2.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
			paint3.setColor(Color.BLUE);
        	paint3.setTextSize(30 * a);
			paint3.setAntiAlias(true); // 消除锯齿
			paint3.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿

			int paddingX = 30;
			int paddingY = 60;

        	canvas.drawText("Device Information", 0+paddingX, 60 * a +30, paint2);
			
       		canvas.drawText("Manufacturer", 0+paddingX, 100 * a+paddingY, paint);
       		canvas.drawText("Model"             , 0+paddingX, 140*a+paddingY, paint);
			canvas.drawText("Android API", 0+paddingX, 180 * a+paddingY, paint);
			canvas.drawText("Resolution", 0+paddingX, 220 * a+paddingY, paint);
			canvas.drawText("DPI", 0+paddingX, 260 * a+paddingY, paint);
			canvas.drawText("Calculated Size", 0+paddingX, 300 * a+paddingY, paint);
			canvas.drawText("APP Version", 0+paddingX, 340 * a+paddingY, paint);
			if(readFail==0)
			{
				canvas.drawText("FW Version", 0+paddingX, 380 * a+paddingY, paint);
				canvas.drawText("CFG Version", 0+paddingX, 420 * a+paddingY, paint);
			}
			else
				canvas.drawText(Read_FW_ver, 0+paddingX, 380 * a+paddingY, paint3);


			canvas.drawText(manufacturer        , 230*a+paddingX, 100*a+paddingY, paint3);
			canvas.drawText(model               , 230*a+paddingX, 140*a+paddingY, paint3);
			canvas.drawText(sdk                 , 230*a+paddingX, 180*a+paddingY, paint3);
			canvas.drawText(dis                 , 230*a+paddingX, 220*a+paddingY, paint3);
			canvas.drawText(dpi                 , 230*a+paddingX, 260*a+paddingY, paint3);
			canvas.drawText(sizeo               , 230*a+paddingX, 300*a+paddingY, paint3);
			canvas.drawText(version             , 230*a+paddingX, 340*a+paddingY, paint3);
			if(readFail==0)
			{
				canvas.drawText(string_fw             , 230*a+paddingX, 380*a+paddingY, paint3);
				canvas.drawText(string_cfg             , 230*a+paddingX, 420*a+paddingY, paint3);
			}
			//canvas.drawRect(10,20,10,20,btn_paint);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
			canvas.drawBitmap(bmp,40,600,null);
            //himax_config.mToast(Phonestate.this,Integer.toString(bmp.getHeight()));
            //himax_config.mToast(Phonestate.this,Integer.toString(bmp.getWidth()));
            mdetail_width = 40+bmp.getHeight();
            mdetail_height = 600+bmp.getWidth();

		}


    }

    @Override
    public boolean onTouchEvent( MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_UP && event.getPointerCount() > 0)
        {
        	int now_x = (int) event.getX();
            int now_y = (int) event.getY();
            //himax_config.mToast(Phonestate.this,"x="+Integer.toString(now_x)+" y="+Integer.toString(now_y));
            //himax_config.mLogPrinter('e',"x="+Integer.toString(now_x)+" y="+Integer.toString(now_y));
            if(now_x > 40 && now_x < mdetail_width &&
                    now_y >600 && now_y < mdetail_height)
            {
                //himax_config.mToast(Phonestate.this,"aaaaaaa");
                AlertDialog.Builder dialog_detail = new AlertDialog.Builder(Phonestate.this);
                dialog_detail.setTitle("Detail...");
                dialog_detail.setMessage(Read_FW_ver);
                dialog_detail.setIcon(R.drawable.ic_launcher);
                dialog_detail.setNegativeButton("OK", null);
                dialog_detail.show();

            }
        }


        return true;
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.phonestate, menu);
		menu.add(0, 0,0,"Detail").setShortcut('s','s');
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case 0:
				AlertDialog.Builder dialog_detail = new AlertDialog.Builder(Phonestate.this);
				dialog_detail.setTitle("Detail...");
				dialog_detail.setMessage(Read_FW_ver);
				dialog_detail.setIcon(R.drawable.ic_launcher);
				dialog_detail.setNegativeButton("OK", null);
				dialog_detail.show();
				break;


		}
		return super.onOptionsItemSelected(item);
	}
}
