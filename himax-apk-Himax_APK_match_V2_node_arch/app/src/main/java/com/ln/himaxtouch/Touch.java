package com.ln.himaxtouch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class Touch extends Activity {

	Panel mPanel;
	int[] check = new int[10];
	int[] x = new int[10];   
	int[] y = new int[10];
	int xr;
	int yr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics rmetrics = new DisplayMetrics();
		display.getRealMetrics(rmetrics);
		xr = rmetrics.widthPixels;
		yr = rmetrics.heightPixels;
		mPanel = new Panel(this);
		mPanel.setBackgroundColor(Color.GRAY);
        setContentView(mPanel);
	}
	
	class Panel extends View {
		Paint mPaint = new Paint();
		Paint paintl = new Paint();
		Paint paint2 = new Paint();
		Paint paint3 = new Paint();

    	public Panel(Context context) {
    		super(context);
    	}
    	
    	@Override
    	public void onDraw(Canvas canvas) {
    		float t =720;
    		float b =xr/t;
    		Paint[] paint = new Paint[10];
    		float textsize=40*b;  
    		for(int a=0;a<10;a++){
    			paint[a] = new Paint();
        		paint[a].setTextSize(textsize);
        		paint[a].setAlpha(220);
    		}
    		paint[0].setColor(Color.RED);
    		paint[1].setColor(Color.BLUE);
    		paint[2].setColor(Color.BLACK);
    		paint[3].setColor(Color.WHITE);
    		paint[4].setColor(Color.GREEN);
    		paint[5].setColor(Color.YELLOW);
    		paint[6].setColor(Color.MAGENTA);
    		paint[7].setColor(Color.CYAN);
    		paint[8].setColor(Color.LTGRAY);
    		paint[9].setColor(Color.DKGRAY);
    		paintl.setColor(Color.BLACK);
//    		int xt = xr/16;
//    		int yt = yr/24;
//    		for(int a=0;a<=16;a++){
//    			canvas.drawLine(a*xt,0, a*xt, yr, paintl);
//    		}
//    		for(int a=0;a<=24;a++){
//    			canvas.drawLine(0,a*yt, xr, a*yt, paintl);
//    		}
			// Grid lines
			DrawLineSub(canvas, 20, 20, paintl);
			DrawLineSub(canvas, 100, 100, paint2);
			canvas.drawText("(" + xr + "," + yr + ")", 100, 100, paint2);

			// Border Line
			canvas.drawLine(0, 0, 0, yr, paint3);
			canvas.drawLine(xr - 1, 0, xr - 1, yr, paint3);
			canvas.drawLine(0, yr - 1, xr, yr - 1, paint3);
			canvas.drawLine(0, 0, xr, 0, paint3);
    		
    		float r =80*b;
    		float xa=-(r-(100*b));
    		float xa2 =-(r+(100*b));
    		float ya=textsize+r;
    		float ya2=-(textsize+r-(30*b)); 		
    		
    		for(int a=0;a<10;a++){
    		if(check[a]==1){
    			if(x[a]<150){	
    				xa2=xa;
    			}
    				else if(x[a]>(xr-150)){
    				}			
    				else{
    					xa2=(-r);
    				}
    			if(y[a]>(yr-300)){
    				ya=ya2;
    			}
    			canvas.drawText("("+x[a]+","+y[a]+")", (float)x[a]+xa2, (float)y[a]+ya, paint[a]);
        		canvas.drawCircle(x[a], y[a],r, paint[a]);	
    			}
    		}		
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		// TODO Auto-generated method stub
        	int index;
        	int id;
        	switch(event.getActionMasked())
        	{
        	case MotionEvent.ACTION_UP:
        		index =event.getActionIndex();
        		id = event.getPointerId(index);
        		check[id] = 0;
        		break;
        	case MotionEvent.ACTION_POINTER_UP:
				for(int a=0;a<event.getPointerCount();a++) {
					index = event.getActionIndex();
					id = event.getPointerId(index);
					check[id] = 0;
					//Toast.makeText(this.getContext(), "count: " + Integer.toString(id), Toast.LENGTH_SHORT).show();
				}
        		break;

        	case MotionEvent.ACTION_MOVE:
        		for(int a=0;a<event.getPointerCount();a++){
        			if(check[a]==1){
						x[a] = (int)event.getX(event.findPointerIndex(a));
						y[a] = (int)event.getY(event.findPointerIndex(a));

        			}
        			}
        		break;
        	case MotionEvent.ACTION_POINTER_DOWN:
				for(int a=0;a<event.getPointerCount();a++) {
					index = event.getActionIndex();
					id = event.getPointerId(index);
					x[id] = (int) event.getX(event.findPointerIndex(id));
					y[id] = (int) event.getY(event.findPointerIndex(id));
					check[id] = 1;
				}
        		break;
        	case MotionEvent.ACTION_DOWN:
        		index =event.getActionIndex();
        		id = event.getPointerId(index);
        		x[id] = (int)event.getX(event.findPointerIndex(id));
        		y[id] = (int)event.getY(event.findPointerIndex(id));
        		check[id] = 1;
        		break;


        	}
        	mPanel.invalidate();
    		return true;
    	}   	
    }
	public void DrawLineSub(Canvas canvas, int x_pix_unit, int y_pix_unit, Paint paintNow){
		int x_times = xr/x_pix_unit;
		int y_times = yr/y_pix_unit;

		for(int a=0;a<=x_times;a++){
			canvas.drawLine(a*x_pix_unit,0, a*x_pix_unit, yr, paintNow);
		}
		for(int a=0;a<=y_times;a++){
			canvas.drawLine(0,a*y_pix_unit, xr, a*y_pix_unit, paintNow);
		}

		if ((x_times*x_pix_unit) == xr)
			canvas.drawLine(xr-1, 0, xr-1, yr, paintNow);

		if ((y_times*y_pix_unit) == yr)
			canvas.drawLine(0, yr-1, xr, yr-1, paintNow);
	}
}
