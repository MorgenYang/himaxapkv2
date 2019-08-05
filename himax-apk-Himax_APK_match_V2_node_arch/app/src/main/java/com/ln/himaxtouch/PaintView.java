package com.ln.himaxtouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PaintView extends View {

    float[][] fixed = {{100,100, 0},{700,1600, 1},{700,100, 1},{100,1600, 1},{100,100,2},{300,560,0},{600,700,1},{800,400,2}};
    float[][] fixed_parameter;
    final int WIDTH = 30;


    public Map<Integer, ArrayList<pf>> mPoints;
    public static class pf {
        float x;
        float y;
    }

    private Paint mPaint = new Paint();
    private Paint mPPP = new Paint();

    public PaintView(Context context) {
        super(context);
        mPoints = new HashMap<Integer, ArrayList<pf>>();
        mPaint.setColor(Color.RED);

        mPPP.setStrokeWidth(WIDTH*2);
        mPPP.setColor(Color.CYAN);

        fixed_parameter = new float[fixed.length-1][3];
        for(int i=1; i<fixed.length; i++) {
            if(fixed[i][2] == 1 || fixed[i][2] == 2) {
                if((fixed[i-1][0] - fixed[i][0]) == 0) {
                    fixed_parameter[i-1][0] = 1;
                    fixed_parameter[i-1][1] = -(fixed[i-1][0]);
                    fixed_parameter[i-1][2] = 0;
                } else {
                    fixed_parameter[i-1][0] = -(fixed[i-1][1] - fixed[i][1])/(fixed[i-1][0] - fixed[i][0]);
                    fixed_parameter[i-1][1] = -(fixed[i-1][1]*(fixed[i-1][0]-fixed[i][0]) - fixed[i-1][0]*(fixed[i-1][1]-fixed[i][1]))/(fixed[i-1][0] - fixed[i][0]);
                    fixed_parameter[i-1][2] = 1;
                }

            }

        }
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Log.d("Steve", ""+event);
        return super.onHoverEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean start = false;
        for(int i=0;i<fixed.length;i++) {
            if(fixed[i][2] == 0) {
                start = true;
            } else if(fixed[i][2] == 2){
                start = false;
            }
            if(start) {
                canvas.drawLine(fixed[i][0], fixed[i][1], fixed[i + 1][0], fixed[i + 1][1], mPPP);
            }
        }
//        canvas.drawLine(fixed[3][0], fixed[3][1], fixed[0][0], fixed[0][1], mPPP);

        Set<Integer> keys = mPoints.keySet();

        for(Integer key : keys) {
            ArrayList<pf> points = mPoints.get(key);

            switch (key) {
                case 0:
                    mPaint.setColor(Color.RED);
                    break;
                case 1:
                    mPaint.setColor(Color.BLUE);
                    break;
                case 2:
                    mPaint.setColor(Color.YELLOW);
                    break;
                case 3:
                    mPaint.setColor(Color.GREEN);
                    break;
                case 4:
                    mPaint.setColor(Color.WHITE);
                    break;
            }

            for(int j=0; j<points.size()-1; j++) {
                double dd = getdistance(points.get(j), WIDTH);
//                double dd = Math.abs(a*points.get(j).x + b*points.get(j).y + c)/Math.sqrt(a*a + b*b);
//                if(points.get(j).x < fixed[0][0]-10 || points.get(j).x > fixed[1][0]+10 || points.get(j).y < fixed[0][1]-10 || points.get(j).y > fixed[1][1]+10) {
//                    dd = 20;
//                }
//                double dd1 = Math.abs(a*points.get(j).x + b*points.get(j).y + c)/Math.sqrt(a*a + b*b);
//                double dd12 = Math.abs(a*points.get(j).x + b*points.get(j).y + c)/Math.sqrt(a*a + b*b);
//                double dd123 = Math.abs(a*points.get(j).x + b*points.get(j).y + c)/Math.sqrt(a*a + b*b);
                int color = mPaint.getColor();
                if(dd > WIDTH) {
                    mPaint.setColor(Color.YELLOW);
                    canvas.drawCircle(points.get(j).x, points.get(j).y, 3, mPaint);
                } else {
                    mPaint.setColor(Color.RED);
                    canvas.drawCircle(points.get(j).x, points.get(j).y, 3, mPaint);
                }
                canvas.drawLine(points.get(j).x,points.get(j).y,points.get(j+1).x,points.get(j+1).y,mPaint);
            }
        }


    }

    private double getdistance(pf point, int tolrence) {
        double dis = 0;
        float[] record;
        for(int i=0; i<fixed_parameter.length; i++)
        {
            double dist = Math.abs(fixed_parameter[i][0]*point.x +  fixed_parameter[i][2]*point.y + fixed_parameter[i][1])/Math.sqrt(fixed_parameter[i][0]*fixed_parameter[i][0] +  fixed_parameter[i][2]* fixed_parameter[i][2]);
            if(fixed[i][0] > fixed[i+1][0]) {
                if(point.x < fixed[i+1][0]-tolrence || point.x > fixed[i][0]+tolrence) {
                    dist += 1000;
                }
            } else {
                if(point.x < fixed[i][0]-tolrence || point.x > fixed[i+1][0]+tolrence) {
                    dist += 1000;
                }
            }
            if(fixed[i][1] > fixed[i+1][1]) {
                if(point.y < fixed[i+1][1]-tolrence || point.y > fixed[i][1]+tolrence) {
                    dist += 1000;
                }
            } else {
                if(point.y < fixed[i][1]-tolrence || point.y > fixed[i+1][1]+tolrence) {
                    dist += 1000;
                }
            }

            if(i==0) {
                dis = dist;
            } else {
                if(dist < dis) {
                    dis = dist;
                    record = fixed[i];
                }
            }


        }

        return dis;
    }

}
