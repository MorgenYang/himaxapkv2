package com.ln.himaxtouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by 902995 on 2018/1/12.
 */

public class BackgroundDefGraph extends Activity {
    AlertDialog.Builder mGraphDialogBuilder;
    static AlertDialog.Builder mGrapListhDialogBuilder;
    static RelativeLayout mContentView;
    LayoutInflater mInflater;
    View mLayout;
    LayoutInflater mListInflater;
    View mListLayout;
    LayoutInflater mRootInflater;
    View mRootLayout;

    static LinearLayout mDialogBackGraphList;
    private static int mAlreadyPrepareGaph = 0;

    static private String mGrpahPath;
    static private Bitmap mBitmap[];

    private static ProgressBar mProgress;

    private static GraphAdapter itemAdapter;
    private static GridView mGridview;

    static LinearLayout.LayoutParams mGridView_para = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    static private String mAllFileStr[];

    static private himax_config mHXC;
    private static int mNowBackgroundGraph = 0;

    static private final int mFlagWorkStart = 1;
    static private final int mFlagWorkEnd = 2;

    private static class WorkHandler extends Handler {
        WorkHandler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case mFlagWorkStart:
                    mPrepareBitmap();

                    break;
                case mFlagWorkEnd:
                    Log.d("HXTP", "All short cut prepare OK");
                    break;
            }
        }

    }

    static private WorkHandler mWorkingThread;
    private HandlerThread mWorkingHandlerThread;


    static private final int mFlagMainStartUI = 1;
    static private final int mFlagMainProcessing = 2;
    static private final int mFlagMainRemoveGridView = 3;
    static private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case mFlagMainStartUI:
                    itemAdapter.notifyDataSetChanged();

                    break;
                case mFlagMainProcessing:
                    //mProgress.setVisibility(ProgressBar.VISIBLE);
                    break;

                case mFlagMainRemoveGridView:
                    Log.d("HXTP", "remove mGridview");
                    //mContentView.removeView(mGridview);
                    //Dialog dialog = new Dialog(mDialogBackGraphList.getContext());
                    mDialogBackGraphList.removeView(mGridview);
                    mDialogBackGraphList.removeView(mDialogBackGraphList);
                    mContentView.removeView(mDialogBackGraphList);
                    //dialog.cancel();
                    mSetBackground(mNowBackgroundGraph);

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHXC = new himax_config(this);

        mGrpahPath = "/sdcard/DCIM/CAMERA/";


        setContentView(R.layout.activity_background_def_graph);

        mWorkingHandlerThread = new HandlerThread("HX_Working_for_parse_graph");
        mWorkingHandlerThread.start();

        mWorkingThread = new WorkHandler(mWorkingHandlerThread.getLooper());

        mRootInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mRootLayout = mRootInflater.inflate(R.layout.activity_background_def_graph, (ViewGroup) findViewById(R.id.activity_background_def_graph));

        mContentView = (RelativeLayout) this.findViewById(R.id.activity_background_def_graph);


        mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mLayout = mInflater.inflate(R.layout.dialog_back_graph_select, (ViewGroup) findViewById(R.id.dialog_back_graph_select));

        mListInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mListLayout = mInflater.inflate(R.layout.dialog_back_graph_list, (ViewGroup) findViewById(R.id.dialog_back_graph_list_main));

        mGrapListhDialogBuilder = new AlertDialog.Builder(BackgroundDefGraph.this);
        mGraphDialogBuilder = new AlertDialog.Builder(BackgroundDefGraph.this);

        mDialogBackGraphList = (LinearLayout) mListLayout.findViewById(R.id.dialog_back_graph_list_main);


        mSetupGridView();

        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setNegativeButton("OK", null);
        dialog_builder.setCancelable(false);
        AlertDialog dialog = dialog_builder.create();
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setTitle("Notice!");
        dialog.setMessage(Html.fromHtml("<br>Please Press <font color=\"red\">Volume Up Key</font> to set your Background Graph" +
                "<br>Please Press <font color=\"red\">Volume Down Key</font> to set your Graph Path"));
        dialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            /*
            if(mGridview == null)
            {
                TextView tv = mDialogBackGraphList.findViewById(R.id.dialog_back_graph_list_title);
                tv.setText("aaaxxxaa");
                Log.d("HXTP","mDialogBackGraphList is null");
            }
            else
                mDialogBackGraphList.addView(mGridview,mGridView_para);*/
            mGrapListhDialogBuilder.setTitle("Choose your graph to set background");
            mGrapListhDialogBuilder.setAdapter(itemAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSetBackground(which);
                }
            });
            //mGrapListhDialogBuilder.setView(mDialogBackGraphList);
            mGrapListhDialogBuilder.setNegativeButton("Cancel", null);


            mGrapListhDialogBuilder.show();


            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {


            final EditText path_text = (EditText) mLayout.findViewById(R.id.dbgs_file_path);
            mGrpahPath = path_text.getText().toString();

            mGraphDialogBuilder.setTitle("Edit your graph path");
            mGraphDialogBuilder.setView(R.layout.dialog_back_graph_select);
            mGraphDialogBuilder.setIcon(R.drawable.ic_launcher);
            mGraphDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mGrpahPath = path_text.getText().toString();
                    Toast.makeText(BackgroundDefGraph.this, "Now path=" + mGrpahPath, Toast.LENGTH_SHORT).show();


                    mSetupGridView();

                    Message main_msg = Message.obtain();
                    main_msg.what = mFlagMainProcessing;
                    mMainHandler.sendMessage(main_msg);


                }
            });
            mGraphDialogBuilder.setNegativeButton("Cancel", null);

            mGraphDialogBuilder.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static void mSetBackground(int id) {
        /*
        try{
            Bitmap.compress(Bitmap.CompressFormat.JPEG,80,openFileOutput(filename,MODE_PRIVATE));
        }
        catch(Exception e)
        {
            Log.e("HXTP",e.toString());
        }
        Uri*/
        //Bitmap bitmap = BitmapFactory.decodeFile(filename);
        Log.d("HXTP", "SetBackground id=" + Integer.toString(id));
        mNowBackgroundGraph = id;
        Drawable drawable = Drawable.createFromPath(mGrpahPath + mAllFileStr[id]);
        mContentView.setBackground(drawable);
        /*
        mContentView.setOnCapturedPointerListener(new View.OnCapturedPointerListener(){
            @Override
            public boolean onCapturedPointer(View view,MotionEvent event){
                mTouchOperation(event);
                return false;
            }
        });
        */

    }

    static private void mPrepareBitmap() {
        Log.d("HXTP", "Now prepare bitmap");


        for (int i = 0; i < mAllFileStr.length; i++) {
            Message msg = Message.obtain();
            mBitmap[i] = mHXC.mScaleImage(BitmapFactory.decodeFile(mGrpahPath + mAllFileStr[i]), 100, 100);
            msg.what = mFlagMainStartUI;
            mMainHandler.sendMessage(msg);
        }
        mAlreadyPrepareGaph = 1;

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //mGridview.invalidate();
                mNowBackgroundGraph = (int) id;

                Message main_msg = Message.obtain();
                main_msg.what = mFlagMainRemoveGridView;
                mMainHandler.sendMessage(main_msg);


            }
        });

        Message msg = Message.obtain();
        msg.what = mFlagWorkEnd;
        mWorkingThread.sendMessage(msg);
//        msg.what = mFlagMainStartUI;
//        mMainHandler.sendMessage(msg);

    }

    private void mSetupGridView() {


        String uri = mGrpahPath;


        File path_dir = new File(uri);
        if (!path_dir.isDirectory()) {
            Log.e("HXTPE", "There is no this directory");
            return;
        } else {
            mAllFileStr = path_dir.list();
        }

        mBitmap = new Bitmap[mAllFileStr.length];

        for (int i = 0; i < mAllFileStr.length; i++) {
            mBitmap[i] = Bitmap.createBitmap(100, 100, Bitmap.Config.ALPHA_8);

        }

        itemAdapter = new GraphAdapter(R.layout.dialog_back_graph_adapter, mAllFileStr, mBitmap);

        mGridview = new GridView(mDialogBackGraphList.getContext());
        mGridview.setNumColumns(3);
        mGridview.setGravity(Gravity.CENTER_HORIZONTAL);
        mGridview.setAdapter(itemAdapter);

        Message msg = Message.obtain();
        msg.what = mFlagWorkStart;
        mWorkingThread.sendMessage(msg);

        //mContentView.addView(mGridview,mGridView_para);

    }

    public class GraphAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] Ids;
        private final int rowResourceId;
        private final Bitmap[] bitmap;

        public GraphAdapter(int textViewResourceId, String[] objects, Bitmap[] bitmap) {
            super(BackgroundDefGraph.this, textViewResourceId, objects);
            this.context = BackgroundDefGraph.this;
            this.Ids = objects;
            this.rowResourceId = textViewResourceId;
            this.bitmap = bitmap;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("HXTP", "adapter processing!" + Integer.toString(position));
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(rowResourceId, parent, false);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            TextView textView = (TextView) rowView.findViewById(R.id.textView);


            //Drawable drawable = Drawable.createFromPath(uri_path+Ids[position]);

            textView.setText(Ids[position]);
            imageView.setImageBitmap(bitmap[position]);

            return rowView;
        }
    }

    private static float mYPress = 0;
    private static float mXRelease = 0;
    private static float mYRelease = 0;
    private static float mXPress = 0;

    private static void mTouchOperation(MotionEvent event) {


        switch (event.getAction()) {

            case MotionEvent.ACTION_BUTTON_PRESS:
                Log.d("HXTP", "Test ACTION_BUTTON_PRESS");
                break;
            case MotionEvent.ACTION_BUTTON_RELEASE:
                Log.d("HXTP", "Test ACTION_BUTTON_RELEASE");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mXRelease = event.getX();
                mYRelease = event.getY();
                Log.d("HXTP", "Test Up x=" + Float.toString(mXRelease) + " y=" + Float.toString(mYRelease));
                if (mAlreadyPrepareGaph == 1) {
                    if ((mXRelease - mXPress) > mHXC.mx_res / 2) {
                        if (mNowBackgroundGraph != 0) {
                            mSetBackground(--mNowBackgroundGraph);
                            Log.d("HXTP", "l->r mNowBackgroundGraph=" + Integer.toString(mNowBackgroundGraph));
                            Log.d("HXTP", "Test delta=" + Float.toString(mXRelease - mXPress) + " mx_res/2=" + Integer.toString(mHXC.mx_res / 2));
                        }
                    } else if ((int) (mXPress - mXRelease) > (mHXC.mx_res / 2)) {
                        if (mNowBackgroundGraph < mAllFileStr.length) {
                            mSetBackground(++mNowBackgroundGraph);
                            Log.d("HXTP", "r->l mNowBackgroundGraph=" + Integer.toString(mNowBackgroundGraph));
                            Log.d("HXTP", "Test delta=" + Float.toString(mXPress - mXRelease) + " mx_res/2=" + Integer.toString(mHXC.mx_res / 2));
                        }
                    } else if (Math.abs(mYRelease - mYPress) > (mHXC.my_res * 2 / 3)) {
                        //mContentView.addView(mGridview);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                mXPress = event.getX();
                mYPress = event.getY();
                Log.d("HXTP", "Test Down x=" + Float.toString(mXPress) + " y=" + Float.toString(mYPress));
                break;


        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method
        // mTouchOperation(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onPause() {

        super.onPause();
        //mAlreadyPrepareGaph = 0;
        //mGridview = null;

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //mAlreadyPrepareGaph = 0;
        //mGridview = null;

    }

}


