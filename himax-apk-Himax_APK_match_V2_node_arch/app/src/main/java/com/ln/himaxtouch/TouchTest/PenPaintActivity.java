package com.ln.himaxtouch.TouchTest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ln.himaxtouch.CustomView.PenView;
import com.ln.himaxtouch.R;

public class PenPaintActivity extends Activity {

    private PenView mView;
    private Button mClearButton;
    private Button mHoverButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_view);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.lll);
        mView = new PenView(this);

//        mClearButton = (Button) findViewById(R.id.clear_button);

        mClearButton = new Button(this);
        mClearButton.setText("Clear");
        mClearButton.setId(R.id.acc);

        mHoverButton = new Button(this);
        mHoverButton.setText("Turn Off Hover");
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.acc);
        mHoverButton.setLayoutParams(params);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.mPenPoints.clear();
                mView.invalidate();
            }
        });

        mHoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.isShowHover = !mView.isShowHover;
                if(mView.isShowHover) {
                    mHoverButton.setText("Turn Off Hover");
                } else {
                    mHoverButton.setText("Turn On Hover");
                }
            }
        });

        layout.addView(mView);
        layout.addView(mClearButton);
        layout.addView(mHoverButton);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mView.onMyTouchEvent(event);

        return super.onTouchEvent(event);


    }
}
