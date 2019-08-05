package com.ln.himaxtouch;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class TestActivity extends Activity {

    public PaintView mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_view);


        LinearLayout layout = (LinearLayout) findViewById(R.id.lll);
        mView = new PaintView(this);
        layout.addView(mView);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Steve", "keyDown" + event);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("Steve", "keyUp" + event);
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("Steve", "" + event);
        int action = event.getAction();
//        int id = event.getPointerId(event.getActionIndex());
        int evAction = event.getAction() & MotionEvent.ACTION_MASK;

        int indexP = event.getPointerCount();
        for (int i = 0; i < indexP; i++) {
            Log.d("Steve", event.getToolType(i) + " ");
        }
//
//
//        switch (evAction) {
//            case MotionEvent.ACTION_DOWN:
//                mView.mPoints.clear();
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_POINTER_DOWN:
//            case MotionEvent.ACTION_POINTER_UP: {
//                for(int i=0;i<event.getPointerCount();i++) {
//                    int id = event.getPointerId(i);
//                    ArrayList<PaintView.pf> list = mView.mPoints.get(id);
//                    if(list == null)
//                        list = new ArrayList<PaintView.pf>();
//                    for(int j=0;j<event.getHistorySize();j++) {
//                        PaintView.pf p = new PaintView.pf();
//                        p.x = event.getHistoricalX(i, j);
//                        p.y = event.getHistoricalY(i, j);
//                        list.add(p);
//                    }
//                    mView.mPoints.put(id, list);
//                }
//            }
//            default:
//                break;
//
//        }
//        mView.invalidate();
        return super.onTouchEvent(event);
    }
}
