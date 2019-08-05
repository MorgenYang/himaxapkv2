package com.ln.himaxtouch.PatternLock;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ln.himaxtouch.HomeCatalog.HomeActivity;
import com.ln.himaxtouch.R;

/**
 * Created by 903622 on 2018/6/6.
 */

public class LockFragment extends Fragment implements LockView.onPatternChangeListener{
    private TextView mLockHint;
    private LockView mLockView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock,null);
        initViews(view);
        return view;
    }

    @Override
    public void onPatternChange(String passwordstr) {
        if(passwordstr!=null){
            String settingpass = "5689";
            if(passwordstr.equals(settingpass)){
                Intent intent = new Intent();
                intent.putExtra("isPro", true);
                intent.setClass(getActivity(), HomeActivity.class);
                getActivity().startActivity(intent);
            }else {
                mLockHint.setText(R.string.pattern_error);
            }
        } else {
            mLockHint.setText(R.string.draw_pattern);
        }
    }

    @Override
    public void onPatternStart(boolean isClick) {
        if(isClick){
            mLockHint.setText(R.string.draw_pattern);
        }
    }

    private void initViews (View view ){
        mLockHint = (TextView) view.findViewById(R.id.act_main_lockhint);
        mLockView = (LockView) view.findViewById(R.id.act_main_lockview);
        mLockView.setPatternChangeListener(this);
    }
}