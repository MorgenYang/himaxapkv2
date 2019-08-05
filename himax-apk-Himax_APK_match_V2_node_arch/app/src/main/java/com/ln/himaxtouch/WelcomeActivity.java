package com.ln.himaxtouch;

import android.Manifest;
import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.ln.himaxtouch.HomeCatalog.HomeActivity;
import com.ln.himaxtouch.PatternLock.LockFragment;

import java.io.File;
import java.io.IOException;

/**
 * Created by 903622 on 2018/6/5.
 */

public class WelcomeActivity extends Activity {

    private ImageButton mLoginGuestButton;
    private ImageButton mLoginProButton;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ImageView img = (ImageView) findViewById(R.id.img_logo);
        mLoginGuestButton = (ImageButton) findViewById(R.id.login_guest);
        mLoginProButton = (ImageButton) findViewById(R.id.login_pro);
        mContext = this;
        animateLOGO(img);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView version = (TextView) findViewById(R.id.app_version);
            version.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        verifyStoragePermissions(this);
    }

    private void animateLOGO(ImageView img) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1500);
        img.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("isPro", false);
                intent.setClass(mContext, HomeActivity.class);
                mContext.startActivity(intent);
            }
        });
        mLoginProButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
    }

    private void initData() {
        LockFragment lockFragment = new LockFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.act_lock_layout, lockFragment).commit();
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    permissionList,
                    1
            );
        }
    }

}
