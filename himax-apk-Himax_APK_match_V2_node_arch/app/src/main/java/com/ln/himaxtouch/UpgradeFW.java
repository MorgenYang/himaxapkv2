package com.ln.himaxtouch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.CustomView.RangeSeekBar;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Einim on 2016/5/22.
 */
public class UpgradeFW extends Activity implements View.OnClickListener {
    public final static String DUMP_PATH = "/sdcard/HX_Flash_Dump.bin";
    static Button start;
    static Button start_abs_btn;
    Button reload;
    Button check_dir;
    static Button btn_dump;
    EditText FW_Dir;
    TextView permission_text;
    EditText ed_dump_size;
    static Button compare_btn;
    static TextView des;
    static TextView upgrade_result;
    int check_p = 0;
    static String bin_path;
    File bin_dir;
    String bin_files[];
    File bin_files_file[];
    RadioGroup bin_file_list;
    private static String Select_bin;
    public static String proc_dir_node;
    public static String proc_debug_node;
    SharedPreferences sh_settings;

    NodeDataSource mDataSource;
    public static Context mContext;

    private static boolean isExistDumped = false;

    private static final int MSG_UPDATE_FW_FINISH = 0;
    private static final int mUpdateFail = 2;
    private static final int MSG_DUMP_FLASH_FINISH = 1;
    private static final int MSG_COMPARE_FINISH = 2;
    private static Handler mMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_FW_FINISH:
                    start_abs_btn.setEnabled(true);
                    start.setEnabled(true);
                    start.setText("Upgrad\n ");
                    upgrade_result.setText((String) msg.obj);
                    break;
                case MSG_DUMP_FLASH_FINISH: {
                    boolean result = (boolean) msg.obj;
                    if (result) {
                        btn_dump.setText("dump success & again");
                        Toast.makeText(mContext, "dump to /sdcard/FW_Flsh_Dump.bin", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_dump.setText("dump fail & again");
                        Toast.makeText(mContext, "dump failed", Toast.LENGTH_SHORT).show();
                    }
                    btn_dump.setEnabled(true);
                    compare_btn.setEnabled(isExistDumped);
                    StringBuilder sb = new StringBuilder();
                    if (isExistDumped) {
                        sb.append("EXIST: ");
                    } else {
                        sb.append("NOT EXIST: ");
                    }
                    sb.append(DUMP_PATH);
                    des.setText(sb.toString());
                }
                break;
                case MSG_COMPARE_FINISH: {
                    compare_btn.setEnabled(isExistDumped);
                    boolean result = (boolean) msg.obj;
                    if (result) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("PASS")
                                .setMessage("There is no difference between two FWs.")
                                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(mContext)
                                .setTitle("FAIL")
                                .setMessage("Found some differences between two FWs.")
                                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    private static final int mStart2Update = 1;
    private static final int MSG_DUMP_FLASH = 2;
    private static final int MSG_COMPARE_DUMP = 3;

    class WorkHandler extends Handler {
        public WorkHandler(Looper l) {
            super(l);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case mStart2Update:
                    mUpdateFW((boolean) msg.obj);
                    break;
                case MSG_DUMP_FLASH: {
                    isExistDumped = mDataSource.dumpFWCommandToSDCARD(msg.arg1);
                    Message m = Message.obtain();
                    m.obj = isExistDumped;
                    m.what = MSG_DUMP_FLASH_FINISH;
                    mMain.sendMessage(m);
                }
                break;
                case MSG_COMPARE_DUMP: {
                    FileComparator fc = new FileComparator();
                    String selected = (String) msg.obj;
                    int result = fc.compareBlockContent(new File(selected), new File(DUMP_PATH), msg.arg1, msg.arg2);
                    if (result == 0) {
                        // no diff
                        Message m = Message.obtain();
                        m.obj = true;
                        m.what = MSG_COMPARE_FINISH;
                        mMain.sendMessage(m);
                    } else {
                        // with diff
                        Message m = Message.obtain();
                        m.obj = false;
                        m.what = MSG_COMPARE_FINISH;
                        mMain.sendMessage(m);
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private static HandlerThread mHandlerThread;
    private static WorkHandler mWorking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandlerThread = new HandlerThread("Working for update FW");
        mHandlerThread.start();
        mWorking = new WorkHandler(mHandlerThread.getLooper());

        mDataSource = new NodeDataSource(this);
        mContext = this;

        setContentView(R.layout.activity_fwupgrade);
        sh_settings = this.getSharedPreferences("HIAPK", 0);
        proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        proc_debug_node = proc_dir_node + sh_settings.getString("SETUP_DEBUG_NODE", "debug");


        permission_text = (TextView) findViewById(R.id.deug_permission);
        start = (Button) findViewById(R.id.start_btn);
        start_abs_btn = (Button) findViewById(R.id.start_abs_btn);
        check_dir = (Button) findViewById(R.id.check_dir_btn);
        btn_dump = (Button) findViewById(R.id.btn_dump);
        // reload = (Button) findViewById(R.id.reloadP);
        bin_file_list = (RadioGroup) findViewById(R.id.bin_file_list);
        upgrade_result = (TextView) findViewById(R.id.upgrade_result);
        ed_dump_size = (EditText) findViewById(R.id.ed_dump_size);
        ed_dump_size.setText("64");
        compare_btn = (Button) findViewById(R.id.compare_btn);
        des = (TextView) findViewById(R.id.des);
        File f = new File(DUMP_PATH);
        isExistDumped = f.exists();
        StringBuilder sb = new StringBuilder();
        if (isExistDumped) {
            sb.append("EXIST: ");
        } else {
            sb.append("NOT EXIST: ");
        }
        sb.append(DUMP_PATH);
        des.setText(sb.toString());

        check_p = check_permission();
        if (check_p == 0) {
            permission_text.setTextColor(Color.parseColor("#FF5566"));
            permission_text.setText("Checked permission fail");
        } else {
            permission_text.setTextColor(Color.parseColor("#009955"));
            permission_text.setText("Checked permission success");
        }

        start.setOnClickListener(this);
        start_abs_btn.setOnClickListener(this);
        check_dir.setOnClickListener(this);
        btn_dump.setOnClickListener(this);
        compare_btn.setOnClickListener(this);
        // reload.setOnClickListener(this);
        // Toast.makeText(getApplicationContext(), Result, Toast.LENGTH_SHORT).show();

        FW_Dir = (EditText) findViewById(R.id.FW_Dir);

        bin_path = FW_Dir.getText().toString();
        //bin_path= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/DCIM";
        bin_dir = new File(bin_path);
        bin_files_file = bin_dir.listFiles();
        //Toast.makeText(getApplicationContext(), bin_path+"\n isDIR"+bin_dir.isDirectory(), Toast.LENGTH_SHORT).show();

        mParseBinFile();
        bin_file_list.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        RadioButton tempButton = (RadioButton) findViewById(checkedId);
                        // Toast.makeText(getApplicationContext(), tempButton.getText().toString(), Toast.LENGTH_SHORT).show();
                        Select_bin = tempButton.getText().toString();
                        start_abs_btn.setEnabled(true);
                        start.setEnabled(true);
                        File f = new File(DUMP_PATH);
                        isExistDumped = f.exists();
                        compare_btn.setEnabled(isExistDumped);
                    }

                });

       /* for(int i=0;i<bin_files.length;i++)
        {
           Toast.makeText(getApplicationContext(), "aaa", Toast.LENGTH_SHORT).show();
        }*/

    }

    static {
        System.loadLibrary("HimaxAPK");
    }
    //


    //Native function
    public static native String writeCfg(String[] stringArray);

    public static native String readCfg(String[] stringArray);

    private static String retry_readcfg(int retry, String[] command) {
        String result = "";

        while (retry > 0) {
            result = readCfg(command);
            if (result == null || result == "" || result.length() == 0) {
                retry--;
                if (retry == 0)
                    result = "";
                continue;
            } else
                break;
        }
        return result;
    }

    void waiting(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log.d("[himax]", "wainting: fail");
        }
    }

    String write_command(String[] command) {
        String write_in = writeCfg(command);
        return write_in;

    }

    void mParseBinFile() {
        int count_bin_file = 0;
        bin_file_list.removeAllViews();
        if (bin_files_file == null) {
            start.setEnabled(false);
            start_abs_btn.setEnabled(false);
            compare_btn.setEnabled(false);
            upgrade_result.setText("Files List");
            return;
        }
        for (int i = 0; i < bin_files_file.length; i++) {
            RadioButton radio = new RadioButton(this);

            if (bin_files_file[i].toString().matches("[\\w\\W\\s\\S.]*\\.bin")) {

                int pathp = bin_files_file[i].toString().lastIndexOf(bin_path);
                //Toast.makeText(this, Integer.toString(pathp)+bin_path, Toast.LENGTH_SHORT).show();
                Log.d("HXTP", "[" + Integer.toString(i) + "]file=" + bin_files_file[i].toString());
                String get_file_name = bin_files_file[i].toString().substring(pathp + bin_path.length() + 1);
                radio.setText(get_file_name);
                bin_file_list.addView(radio);
                count_bin_file++;

            } else
                continue;
        }
        if (count_bin_file == 0) {
            bin_file_list.removeAllViews();
            start.setEnabled(false);
            start_abs_btn.setEnabled(false);
            compare_btn.setEnabled(false);
            upgrade_result.setText("Files List");
        } else {
            start.setEnabled(false);
            start_abs_btn.setEnabled(false);
            compare_btn.setEnabled(false);
            upgrade_result.setText("Files List");

        }
        Toast.makeText(getApplicationContext(), "There are " + Integer.toString(count_bin_file) + " bin files!!", Toast.LENGTH_SHORT).show();

    }

    int check_permission() {
        // int read_result=0;
        String write_command[] = {proc_debug_node, "d"};
        String result[] = {proc_debug_node};
        String writer = write_command(write_command);
        // Toast.makeText(getApplicationContext(), writer, Toast.LENGTH_SHORT).show();

        String Result = retry_readcfg(3, result);
        if (Result == null || Result.length() == 0)
            return 0;
        int write_r = writer.matches("[\\w\\W\\s\\S.]*fail[\\w\\W\\s\\S.]*") ? 1 : 0;
        // Toast.makeText(getApplicationContext(), Result+Integer.toString(write_r), Toast.LENGTH_SHORT).show();
        if (Result.length() > 0 && write_r == 0)
            return 1;
        return 0;
    }

    private static void mUpdateFW(boolean isAbsPath) {
        String abs_bin_file_name = (isAbsPath) ? (bin_path + "/" + Select_bin) : (Select_bin);
        String command = "t " + abs_bin_file_name + "\n";
        String runUpdate[] = {proc_debug_node, command};
        String resultUpdate[] = {proc_debug_node};
        String Result = "\n";
        String line = null;

        writeCfg(runUpdate);
        Result = retry_readcfg(3, resultUpdate);
        Message main_msg = Message.obtain();

        main_msg.obj = Result;
        main_msg.what = MSG_UPDATE_FW_FINISH;
        mMain.sendMessage(main_msg);
    }

    @Override
    public void onClick(final View v) {
        if (v == start || v == start_abs_btn) {
            sh_settings = this.getSharedPreferences("HIAPK", 0);
            proc_dir_node = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
            proc_debug_node = proc_dir_node + sh_settings.getString("SETUP_DEBUG_NODE", "debug");

            if (Select_bin == null) {
                Toast.makeText(getApplicationContext(), "There is no file had been selected\n Plz Select one file", Toast.LENGTH_SHORT).show();
                return;
            }

            final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
            popDialog.setTitle("Confirm to upgrade");
            popDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Message msg = Message.obtain();
                    msg.what = mStart2Update;
                    msg.obj = (v == start_abs_btn);
                    mWorking.sendMessage(msg);
                    start.setEnabled(false);
                    start_abs_btn.setEnabled(false);
                    start.setText("Upgrading...");
                }
            });
            popDialog.setNegativeButton("No", null);
            popDialog.show();
        }
        if (v == check_dir) {
            bin_path = FW_Dir.getText().toString();
            if (bin_path.length() == 0) {
                return;
            }
            if (bin_path.charAt(bin_path.length() - 1) == '/') {
                Toast.makeText(getBaseContext(), "the last character can not be '/'", Toast.LENGTH_SHORT).show();
                return;
            }
            bin_dir = new File(bin_path);
            bin_files_file = bin_dir.listFiles();
            if (bin_dir.listFiles() != null)
                Toast.makeText(getApplicationContext(), "There are " + Integer.toString(bin_files_file.length) + " files in " + bin_path, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "NULL, Plz check Permission in Android M or check the Path of Directory!", Toast.LENGTH_SHORT).show();
            mParseBinFile();
        }
        if (v == btn_dump) {

            int size = 0;
            try {
                size = Integer.valueOf(ed_dump_size.getText().toString());
            } catch (Exception e) {
                size = 0;
            }

            if (size == 0) {
                return;
            }

            btn_dump.setEnabled(false);
            btn_dump.setText("dumping");
            Message m = Message.obtain();
            m.what = MSG_DUMP_FLASH;
            m.arg1 = size;
            mWorking.sendMessage(m);

        }
        if (v == compare_btn) {
            if (!isExistDumped) {
                return;
            }
            File f = new File(bin_path + "/" + Select_bin);
            ShowDialog(f.length());
        }
       /* if(v==reload)
        {
            if(check_permission()==1) {
                Toast.makeText(getApplicationContext(), "Permission reload success", Toast.LENGTH_SHORT).show();
                permission_text.setTextColor(Color.GREEN);
                permission_text.setText("Permission Success");
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Permission reload fail", Toast.LENGTH_SHORT).show();
                permission_text.setTextColor(Color.RED);
                permission_text.setText("Permission Fail");
            }
        }*/
    }

    public void ShowDialog(long fileLength) {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.dialog_compare_fw, (ViewGroup) findViewById(R.id.dialog));
        popDialog.setTitle("setting compare range.");
        popDialog.setIcon(R.drawable.ic_launcher);
        popDialog.setView(l);

        final EditText tv_start = (EditText) l.findViewById(R.id.index_start);
        final EditText tv_end = (EditText) l.findViewById(R.id.index_end);
        final RangeSeekBar seek = (RangeSeekBar) l.findViewById(R.id.dialog_seek);
        final CheckBox check_all = (CheckBox) l.findViewById(R.id.select_all);

        tv_start.setText(0 + "");
        tv_end.setText(fileLength + "");
        seek.setRangeValues(0, fileLength);
        seek.setNotifyWhileDragging(true);
        seek.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                tv_start.setText(String.valueOf(minValue));
                tv_end.setText(String.valueOf(maxValue));
            }
        });

        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    seek.setSelectedMaxValue(seek.getAbsoluteMaxValue());
                    seek.setSelectedMinValue(seek.getAbsoluteMinValue());
                    tv_start.setText(seek.getAbsoluteMinValue().toString());
                    tv_end.setText(seek.getAbsoluteMaxValue().toString());
                    seek.setEnabled(false);
                    tv_start.setEnabled(false);
                    tv_end.setEnabled(false);
                } else {
                    seek.setEnabled(true);
                    tv_start.setEnabled(true);
                    tv_end.setEnabled(true);
                }
            }
        });

        tv_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int value = seek.getAbsoluteMinValue().intValue();
                try {
                    value = Integer.valueOf(charSequence.toString());
                } catch (Exception e) {

                }
                if (value > seek.getSelectedMaxValue().intValue()) {
                    value = seek.getSelectedMaxValue().intValue();
                }
                seek.setSelectedMinValue(value);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tv_end.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int value = seek.getAbsoluteMaxValue().intValue();
                try {
                    value = Integer.valueOf(charSequence.toString());
                } catch (Exception e) {

                }
                if (value < seek.getSelectedMinValue().intValue()) {
                    value = seek.getSelectedMinValue().intValue();
                }
                seek.setSelectedMaxValue(value);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message m = Message.obtain();
                m.what = MSG_COMPARE_DUMP;
                m.arg1 = seek.getSelectedMinValue().intValue();
                m.arg2 = seek.getSelectedMaxValue().intValue();
                m.obj = bin_path + "/" + Select_bin;
                mWorking.sendMessage(m);
                compare_btn.setEnabled(false);
            }
        });
        popDialog.setNegativeButton("Cancel", null);

        popDialog.show();
    }

    private static class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {

            // one or both null
            if (file1 == file2) {
                return 0;
            } else if (file1 == null && file2 != null) {
                return -1;
            } else if (file1 != null && file2 == null) {
                return 1;
            } else if (!file1.exists() || !file2.exists()) {
                return -2;
            }

            if (file1.isDirectory() || file2.isDirectory()) {
                throw new IllegalArgumentException("Unable to compare directory content");
            }

            // not same size
            if (file1.length() < file2.length()) {
                return -1;
            } else if (file1.length() > file2.length()) {
                return 1;
            }

            try {
                int x = compareContent(file1, file2);
                return x;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }


        public int compareBlockContent(File file1, File file2, int start, int end) {

            if (start < 0 || start > file1.length() || end > file1.length() || end < 0 || end < start) {
                return -2;
            }

            final int BUFFER_SIZE = bufferSize(file1.length());

            int index = start;
            int index_end = BUFFER_SIZE;

            // check content
            try (BufferedInputStream is1 = new BufferedInputStream(new FileInputStream(file1), BUFFER_SIZE); BufferedInputStream is2 = new BufferedInputStream(new FileInputStream(file2), BUFFER_SIZE);) {

                byte[] b1 = new byte[BUFFER_SIZE];
                byte[] b2 = new byte[BUFFER_SIZE];

                int read1 = -1;
                int read2 = -1;
                int read = -1;

                do {
                    if ((index + index_end) > end) {
                        index_end = end - index;
                    }

                    if (start != 0) {
                        is1.skip(start);
                        is2.skip(start);
                    }

                    read1 = is1.read(b1, 0, index_end);
                    read2 = is2.read(b2, 0, index_end);

                    index = index + index_end;

                    if (read1 < read2) {
                        return -1;
                    } else if (read1 > read2) {
                        return 1;
                    } else {
                        // read1 is equals to read2
                        read = read1;
                    }

                    if (read >= 0) {
                        if (read != BUFFER_SIZE) {
                            // clear the buffer not filled from the read
                            Arrays.fill(b1, read, BUFFER_SIZE, (byte) 0);
                            Arrays.fill(b2, read, BUFFER_SIZE, (byte) 0);
                        }
                        // compare the content of the two buffers
                        if (!Arrays.equals(b1, b2)) {
                            return new String(b1).compareTo(new String(b2));
                        }
                    }
                } while (read >= 0 && index_end == BUFFER_SIZE);

                // no difference found
                return 0;
            } catch (Exception e) {
                return -1;
            }
        }

        private int bufferSize(long fileLength) {
            int multiple = (int) (fileLength / 1024);
            if (multiple <= 1) {
                return 1024;
            } else if (multiple <= 8) {
                return 1024 * 2;
            } else if (multiple <= 16) {
                return 1024 * 4;
            } else if (multiple <= 32) {
                return 1024 * 8;
            } else if (multiple <= 64) {
                return 1024 * 16;
            } else {
                return 1024 * 64;
            }
        }

        private int compareContent(File file1, File file2) throws IOException {

            long x = file1.length();
            final int BUFFER_SIZE = bufferSize(file1.length());

            // check content
            try (BufferedInputStream is1 = new BufferedInputStream(new FileInputStream(file1), BUFFER_SIZE); BufferedInputStream is2 = new BufferedInputStream(new FileInputStream(file2), BUFFER_SIZE);) {

                byte[] b1 = new byte[BUFFER_SIZE];
                byte[] b2 = new byte[BUFFER_SIZE];

                int read1 = -1;
                int read2 = -1;
                int read = -1;

                do {
                    read1 = is1.read(b1);
                    read2 = is2.read(b2);

                    if (read1 < read2) {
                        return -1;
                    } else if (read1 > read2) {
                        return 1;
                    } else {
                        // read1 is equals to read2
                        read = read1;
                    }

                    if (read >= 0) {
                        if (read != BUFFER_SIZE) {
                            // clear the buffer not filled from the read
                            Arrays.fill(b1, read, BUFFER_SIZE, (byte) 0);
                            Arrays.fill(b2, read, BUFFER_SIZE, (byte) 0);
                        }
                        // compare the content of the two buffers
                        if (!Arrays.equals(b1, b2)) {
                            return new String(b1).compareTo(new String(b2));
                        }
                    }
                } while (read >= 0);

                // no difference found
                return 0;
            }
        }
    }
}
