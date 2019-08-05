package com.ln.himaxtouch;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class BackgroundColor extends Activity{

    SeekBar mSeekBarRed ;
    TextView mTextRed;
    SeekBar mSeekBarBlue ;
    TextView mTextBlue;
    SeekBar mSeekBarGreen ;
    TextView mTextGreen;

    LinearLayout mPreViewLayer;

    AlertDialog.Builder mColorDialogBuilder;

    static RelativeLayout mContentView;
    LayoutInflater mInflater;
    View mLayout;
    LayoutInflater mRootInflater;
    View mRootLayout;


    private int mRedBarVal = 0;
    private int mBlueBarVal = 0;
    private int mGreenBarVal = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

        mRootInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mRootLayout = mRootInflater.inflate(R.layout.activity_background_color,(ViewGroup)findViewById(R.id.activity_background_color));

        setContentView(R.layout.activity_background_color);
        Log.d("HXTP","onCreate");
        mContentView = (RelativeLayout)findViewById(R.id.activity_background_color);

        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setNegativeButton("OK",null);
        dialog_builder.setCancelable(false);
        AlertDialog dialog = dialog_builder.create();
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setTitle("Please Press Volume Up Key to set your Background Color");
        dialog.show();





    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            mColorDialogBuilder = new AlertDialog.Builder(BackgroundColor.this);
            mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
            mLayout = mInflater.inflate(R.layout.dialog_color_select,(ViewGroup)findViewById(R.id.dialog_all_view));
            mColorDialogBuilder.setTitle("setting your favor color");
            mColorDialogBuilder.setIcon(R.drawable.ic_launcher);
            mColorDialogBuilder.setView(mLayout);
            mColorDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mContentView.setBackgroundColor(Color.argb(0xff,mRedBarVal,mGreenBarVal,mBlueBarVal));
                }
            });
            mColorDialogBuilder.setNegativeButton("Cancel", null);

            mColorDialogBuilder.show();

            mPreViewLayer = (LinearLayout) mLayout.findViewById(R.id.dialog_preview_layer);
            mPreViewLayer.setBackgroundColor(Color.argb(0xff,mRedBarVal,mGreenBarVal,mBlueBarVal));

            mTextRed = (TextView)mLayout.findViewById(R.id.dialog_red_text);
            mTextBlue = (TextView)mLayout.findViewById(R.id.dialog_blue_text);
            mTextGreen = (TextView)mLayout.findViewById(R.id.dialog_green_text);

            mSeekBarRed = (SeekBar)mLayout.findViewById(R.id.dialog_red_seek);
            mSeekBarBlue = (SeekBar)mLayout.findViewById(R.id.dialog_blue_seek);
            mSeekBarGreen = (SeekBar)mLayout.findViewById(R.id.dialog_green_seek);

            mTextRed.setText("Red="+Integer.toString(mRedBarVal));
            mTextBlue.setText("Blue="+Integer.toString(mBlueBarVal));
            mTextGreen.setText("Green="+Integer.toString(mGreenBarVal));

            mSeekBarRed.setProgress(mRedBarVal);
            mSeekBarBlue.setProgress(mBlueBarVal);
            mSeekBarGreen.setProgress(mGreenBarVal);

            mSeekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTextRed.setText("Red="+Integer.toString(progress));
                    Log.d("HXTP","Now Red progress="+Integer.toString(progress));
                    mRedBarVal = progress;
                    mPreViewLayer.setBackgroundColor(Color.argb(0xff,mRedBarVal,mGreenBarVal,mBlueBarVal));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mSeekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTextBlue.setText("Blue="+Integer.toString(progress));
                    Log.d("HXTP","Now Blue progress="+Integer.toString(progress));
                    mBlueBarVal = progress;
                    mPreViewLayer.setBackgroundColor(Color.argb(0xff,mRedBarVal,mGreenBarVal,mBlueBarVal));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mSeekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTextGreen.setText("Green="+Integer.toString(progress));
                    Log.d("HXTP","Now Green progress="+Integer.toString(progress));
                    mGreenBarVal = progress;
                    mPreViewLayer.setBackgroundColor(Color.argb(0xff,mRedBarVal,mGreenBarVal,mBlueBarVal));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            return true;

        }
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {



            return true;
        }
        return super.onKeyDown(keyCode,event);
    }






}
