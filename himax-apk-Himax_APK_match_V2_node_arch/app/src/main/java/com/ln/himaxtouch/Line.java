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

public class Line extends Activity {

	Panel mPanel;
	int count=1;
	float[] x = new float[5000];
	float[] y = new float[5000];
	float[] m = new float[5000];
	float a = 0;
	float b = 0;
	float t1;
	float t9;
	float t10;
	float t2;
	float[] t3 = new float[5000];
	float t8;
	double t7;
	int check =0;
	int check2=0;
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
		mPanel.setBackgroundColor(Color.BLACK);
        setContentView(mPanel);
	}
	
	class Panel extends View {
		Paint mPaint = new Paint();
		Paint paint = new Paint();
		Paint paintl = new Paint();
		
		public Panel(Context context) {
    		super(context);
    	}
    	
    	@Override
    	public void onDraw(Canvas canvas) {
    		float t = 720;
    		float c = xr/t;
    		paint.setColor(Color.WHITE);
    		paint.setTextSize(40*c);
    		mPaint.setColor(Color.RED);
    		paintl.setColor(Color.GRAY);
    		
    		int xt = xr/16;
    		int yt = yr/24;
    		for(int a=0;a<=16;a++){
    			canvas.drawLine(a*xt,0, a*xt, yr, paintl);
    		}
    		for(int a=0;a<=24;a++){
    			canvas.drawLine(0,a*yt, xr, a*yt, paintl);
    		}

    		if(check==1&&count>1&&check2==0){
    			canvas.drawLine(0,b, xr, t1, paint);
				canvas.drawText("RMS error:"+(int)t8+" pixel", 20*c, 50*c, paint);
				canvas.drawText("Max error:"+(int)t7+" pixel", 20*c, 90*c, paint);
    		}
    		else if(check==1&&count>1&&check2==1){
    			canvas.drawLine(t9,0, t10, yr, paint);
				canvas.drawText("RMS error:"+(int)t8+" pixel", 20*c, 50*c, paint);
				canvas.drawText("Max error:"+(int)t7+" pixel", 20*c, 90*c, paint);
    		}
    		if(count>1){
    			for(int a=1;a<count;a++){
    				canvas.drawLine(x[a-1],y[a-1], x[a], y[a], mPaint);
    			}
    		}
    		if(count==1){
    			canvas.drawText("Draw a line", 100*c, 100*c, paint);
    		}
    	}
    	
    	@Override
    	public boolean onTouchEvent(MotionEvent event) {
    		// TODO Auto-generated method stub
        	switch(event.getAction())
        	{
        	case MotionEvent.ACTION_UP:
        		float tmp1;
        		tmp1 = x[0]*y[0];
        		for(int c=1;c<count;c++){
        			tmp1=tmp1+(x[c]*y[c]);
        		}
        		float tmp2;
        		float tmp3;
        		float tmp4;
        		tmp2 =x[0];
        		tmp3 =y[0];
        		for(int d=1;d<count;d++){
        			tmp2=tmp2+x[d];
        			tmp3=tmp3+y[d];
        		}
        		tmp4=tmp2*tmp3;
        		float tmp5;
        		tmp5=tmp2*tmp2;
        		float tmp6;
        		tmp6=x[0]*x[0];
        		for(int e=1;e<count;e++){
        			tmp6=tmp6+(x[e]*x[e]);
        		}
        		a=(tmp4-(count*tmp1))/(tmp5-(count*tmp6));
        		
        		float tmp7;
        		tmp7=tmp6*tmp3;
        		float tmp8;
        		tmp8=tmp2*tmp1;
        		b=(tmp8-tmp7)/(tmp5-(count*tmp6));        		
        		t1 = a*xr+b;
        		if(a>10||a<-10){
        			t9 = (0-b)/a;
        			t10 = (yr-b)/a;
        			check2 =1;
        		}
        		else{
        			check2=0;
        		}
        		float t4;
        		double t5;
        		for(int g=0;g<count;g++){
        		t3[g] = Math.abs(y[g]-(a*x[g])-b);
        		t4 = (a*a)+1;
        		t5 = (double)t4;
        		t5 = Math.sqrt(t5);
        		t4 = (float)t5;
        		t3[g] = t3[g]/t4;
        		}
        		for(int k=0;k<(count-1);k++){
        			if(t3[k]<t3[k+1]){
        				t8=t3[k+1];
        			}
        			else{
        				t8=t3[k];
        			}
        		}
        		float t6=0;
        		for(int h=0;h<count;h++){
        			t6 = t6+((y[h]-(a*x[h])-b)*(y[h]-(a*x[h])-b));
        		}
        		t6 = t6/(count-2);
        		t7 =(double)t6;
        		t7 = Math.sqrt(t7);
        		check=1;
        		break;    		
        	case MotionEvent.ACTION_MOVE:     		
        		if(count<5000){
        			x[count] = event.getX();
        			y[count] = event.getY();
        			count = count + 1;
        		}
        		break;
        	case MotionEvent.ACTION_DOWN:
        		check =0;
        		for(int c=0;c<count;c++){
        			x[c]=0;
        			y[c]=0;
        			m[c]=0;
        			t3[c]=0;
        		}
        		count = 1;        		
        		x[0] = event.getX();
        		y[0] = event.getY(); 
        		break;
        	}
        	mPanel.invalidate();
    		return true;
    	}   	
    }
}
