package com.ln.himaxtouch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.ln.himaxtouch.HimaxApplication.mNodeAcc;

public class SetupActivity extends Activity implements View.OnClickListener, CheckBox.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    Spinner setup_arch_select;
    private int gSetupArchSlctVal = -1;
    String[] arch_item;
    ArrayAdapter<String> arch_adpater;

    EditText setup_dir_text;

    LinearLayout setup_linear_layout;

    LinearLayout setup_diag_lay;
    Button setup_diag_title;
    EditText setup_diag_text;
    CheckBox setup_diag_chk_full;

    EditText setup_v2_diag_stack_text;
    EditText setup_v2_diag_siir_text;
    EditText setup_v2_diag_sdc_text;
    EditText setup_v2_diag_sbank_text;

    LinearLayout setup_register_lay;
    TextView setup_register_title;
    EditText setup_register_text;
    CheckBox setup_register_chk_full;

    LinearLayout setup_debuglevel_lay;
    TextView setup_debuglevel_title;
    EditText setup_debuglevel_text;
    CheckBox setup_debuglevel_chk_full;

    LinearLayout setup_selftest_lay;
    EditText setup_selftest_text;
    CheckBox setup_selftest_chk_full;

    LinearLayout setup_reset_lay;
    TextView setup_reset_title;
    EditText setup_reset_text;
    CheckBox setup_reset_chk_full;

    LinearLayout setup_flashdump_lay;
    TextView setup_flashdump_title;
    EditText setup_flashdump_text;
    CheckBox setup_flashdump_chk_full;

    LinearLayout setup_debug_lay;
    EditText setup_debug_text;
    CheckBox setup_debug_chk_full;

    LinearLayout setup_vendor_lay;
    EditText setup_vendor_text;
    CheckBox setup_vendor_chk_full;

    LinearLayout setup_sense_lay;
    TextView setup_sense_title;
    EditText setup_sense_text;
    CheckBox setup_sense_chk_full;

    EditText setup_Klog_tag_text;
    EditText setup_save_dir_name_text;
    Button setup_save_button;

    private String setup_driver_arch;
    private String setup_dir;
    private String setup_diag;
    private String setup_v2_diag_stack;
    private String setup_v2_diag_siir;
    private String setup_v2_diag_sdc;
    private String setup_v2_diag_sbank;

    private String setup_register;
    private String setup_debuglevel;
    private String setup_selftest;
    private String setup_reset;
    private String setup_flashdump;
    private String setup_debug;
    private String setup_vendor;
    private String setup_sense;
    private String setup_Klog_tag;
    private String setup_save_dir_name;

    private boolean setup_diag_chk_val;
    private boolean setup_register_chk_val;
    private boolean setup_debuglevel_chk_val;
    private boolean setup_selftest_chk_val;
    private boolean setup_reset_chk_val;
    private boolean setup_flashdump_chk_val;
    private boolean setup_debug_chk_val;
    private boolean setup_vendor_chk_val;
    private boolean setup_sense_chk_val;

    SharedPreferences sh_settings;

    View gDialogV2DiagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sh_settings = this.getSharedPreferences("HIAPK", 0);

        //gDialogV2DiagView = getLayoutInflater().inflate(R.layout.dialog_diagv2_view, null);

        mInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);


        setup_driver_arch = sh_settings.getString("SETUP_DRIVER_ARCH", "v1");
        setup_dir = sh_settings.getString("SETUP_DIR_NODE", "/proc/android_touch/");
        setup_diag = sh_settings.getString("SETUP_DIAG_NODE", "diag");

        setup_v2_diag_stack = sh_settings.getString("SETUP_DIAG_V2_STACK", "stack");
        setup_v2_diag_siir = sh_settings.getString("SETUP_DIAG_V2_SIIR", "iir_s");
        setup_v2_diag_sdc = sh_settings.getString("SETUP_DIAG_V2_SDC", "dc_s");
        setup_v2_diag_sbank = sh_settings.getString("SETUP_DIAG_V2_SBANK", "bank_s");

        setup_register = sh_settings.getString("SETUP_REGISTER_NODE", "register");
        setup_debuglevel = sh_settings.getString("SETUP_DEBUGLEVEL_NODE", "debug_level");
        setup_selftest = sh_settings.getString("SETUP_SELFTEST_NODE", "tp_self_test");
        setup_reset = sh_settings.getString("SETUP_RESET_NODE", "reset");
        setup_flashdump = sh_settings.getString("SETUP_FLASHDUMP_NODE", "flash_dump");
        setup_debug = sh_settings.getString("SETUP_DEBUG_NODE", "debug");
        setup_vendor = sh_settings.getString("SETUP_VENDOR_NODE", "vendor");
        setup_sense = sh_settings.getString("SETUP_SENSE_NODE", "SenseOnOff");
        setup_Klog_tag = sh_settings.getString("SETUP_KLOG_TAG", "HXTP");


        setup_diag_chk_val = sh_settings.getBoolean("SETUP_DIAG_NODE_CHK", false);
        setup_register_chk_val = sh_settings.getBoolean("SETUP_REGISTER_NODE_CHK", false);
        setup_debuglevel_chk_val = sh_settings.getBoolean("SETUP_DEBUGLEVEL_NODE_CHK", false);
        setup_selftest_chk_val = sh_settings.getBoolean("SETUP_SELFTEST_NODE_CHK", false);
        setup_reset_chk_val = sh_settings.getBoolean("SETUP_FLASHDUMP_NODE_CHK", false);
        setup_flashdump_chk_val = sh_settings.getBoolean("SETUP_KLOG_TAG_CHK_CHK", false);
        setup_debug_chk_val = sh_settings.getBoolean("SETUP_DEBUG_NODE_CHK", false);
        setup_vendor_chk_val = sh_settings.getBoolean("SETUP_VENDOR_NODE_CHK", false);
        setup_sense_chk_val = sh_settings.getBoolean("SETUP_SENSE_NODE_CHK", false);


        setup_save_dir_name = sh_settings.getString("SETUP SAVE DIR NAME", "HimaxAPK");

        setup_arch_select = findViewById(R.id.setup_driver_arch);
        arch_item = getResources().getStringArray(R.array.DriverNodeArch);
        arch_adpater = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arch_item);
        setup_arch_select.setAdapter(arch_adpater);
        setup_arch_select.setOnItemSelectedListener(this);

        setup_linear_layout = findViewById(R.id.setup_linear_layout);

        setup_dir_text = (EditText) findViewById(R.id.setup_dir_text);

        setup_diag_lay = findViewById(R.id.setup_diag_lay);
        setup_diag_title = findViewById(R.id.setup_textview1);
        setup_diag_text = (EditText) findViewById(R.id.setup_diag_text);
        setup_diag_chk_full = findViewById(R.id.setup_diag_chk);
        setup_diag_chk_full.setChecked(setup_diag_chk_val);
        setup_diag_chk_full.setOnCheckedChangeListener(this);

        setup_register_lay = findViewById(R.id.setup_register_lay);
        setup_register_title = findViewById(R.id.setup_textview2);
        setup_register_text = (EditText) findViewById(R.id.setup_register_text);
        setup_register_chk_full = findViewById(R.id.setup_reg_chk);
        setup_register_chk_full.setChecked(setup_register_chk_val);
        setup_register_chk_full.setOnCheckedChangeListener(this);

        setup_debuglevel_lay = findViewById(R.id.setup_debuglevel_lay);
        setup_debuglevel_title = findViewById(R.id.setup_textview3);
        setup_debuglevel_text = (EditText) findViewById(R.id.setup_debuglevel_text);
        setup_debuglevel_chk_full = findViewById(R.id.setup_dbglv_chk);
        setup_debuglevel_chk_full.setChecked(setup_debuglevel_chk_val);
        setup_debuglevel_chk_full.setOnCheckedChangeListener(this);

        setup_selftest_lay = findViewById(R.id.setup_selftest_lay);
        setup_selftest_text = (EditText) findViewById(R.id.setup_selftest_text);
        setup_selftest_chk_full = findViewById(R.id.setup_slftest_chk);
        setup_selftest_chk_full.setChecked(setup_selftest_chk_val);
        setup_selftest_chk_full.setOnCheckedChangeListener(this);

        setup_reset_lay = findViewById(R.id.setup_reset_lay);
        setup_reset_title = findViewById(R.id.setup_textview6);
        setup_reset_text = (EditText) findViewById(R.id.setup_reset_text);
        setup_reset_chk_full = findViewById(R.id.setup_reset_chk);
        setup_reset_chk_full.setChecked(setup_reset_chk_val);
        setup_reset_chk_full.setOnCheckedChangeListener(this);

        setup_flashdump_lay = findViewById(R.id.setup_flashdump_lay);
        setup_flashdump_title = findViewById(R.id.setup_textview4);
        setup_flashdump_text = (EditText) findViewById(R.id.setup_flashdump_text);
        setup_flashdump_chk_full = findViewById(R.id.setup_flashdump_chk);
        setup_flashdump_chk_full.setChecked(setup_flashdump_chk_val);
        setup_flashdump_chk_full.setOnCheckedChangeListener(this);

        setup_debug_lay = findViewById(R.id.setup_debug_lay);
        setup_debug_text = (EditText) findViewById(R.id.setup_debug_text);
        setup_debug_chk_full = findViewById(R.id.setup_debug_chk);
        setup_debug_chk_full.setChecked(setup_debug_chk_val);
        setup_debug_chk_full.setOnCheckedChangeListener(this);

        setup_vendor_lay = findViewById(R.id.setup_vendor_lay);
        setup_vendor_text = (EditText) findViewById(R.id.setup_vendor_text);
        setup_vendor_chk_full = findViewById(R.id.setup_vendor_chk);
        setup_vendor_chk_full.setChecked(setup_vendor_chk_val);
        setup_vendor_chk_full.setOnCheckedChangeListener(this);

        setup_sense_lay = findViewById(R.id.setup_sense_lay);
        setup_sense_title = findViewById(R.id.setup_textview10);
        setup_sense_text = (EditText) findViewById(R.id.setup_sense_text);
        setup_sense_chk_full = findViewById(R.id.setup_sns_chk);
        setup_sense_chk_full.setChecked(setup_sense_chk_val);
        setup_sense_chk_full.setOnCheckedChangeListener(this);

        setup_Klog_tag_text = (EditText) findViewById(R.id.kernel_log_tag_text);
        setup_save_dir_name_text = (EditText) findViewById(R.id.setup_save_dir_path);


        setup_dir_text.setText(setup_dir);
        setup_diag_text.setText(setup_diag);
        setup_register_text.setText(setup_register);
        setup_debuglevel_text.setText(setup_debuglevel);
        setup_selftest_text.setText(setup_selftest);
        setup_reset_text.setText(setup_reset);
        setup_flashdump_text.setText(setup_flashdump);
        setup_debug_text.setText(setup_debug);
        setup_vendor_text.setText(setup_vendor);
        setup_sense_text.setText(setup_sense);
        setup_Klog_tag_text.setText(setup_Klog_tag);

        setup_save_dir_name_text.setText(setup_save_dir_name);


        setup_save_button = (Button) findViewById(R.id.setup_save_BTN);
        setup_save_button.setOnClickListener(this);

        if (setup_driver_arch.indexOf("v2") >= 0) {
            Log.d("HXTP", "Now v2");
            gSetupArchSlctVal = 1;
            setup_arch_select.setSelection(1);
        } else {
            gSetupArchSlctVal = 0;
            Log.d("HXTP", "Now v1");
            setup_arch_select.setSelection(0);
        }

    }


    protected void SetGlobalVar() {
        himax_config config = new himax_config(this);
        config.mNodeDir = setup_dir_text.getText().toString();
        config.mSenseNodePath = himax_config.mNodeDir + setup_sense_text.getText().toString();
        config.mSaveDir_str = setup_save_dir_name_text.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if (v == setup_save_button) {
            SharedPreferences.Editor temp_pe = sh_settings.edit();

            switch (gSetupArchSlctVal) {
                case 0:
                    Log.d("HXTP", "Store to v1");
                    temp_pe.putString("SETUP_DRIVER_ARCH", "v1");
                    break;
                case 1:
                    Log.d("HXTP", "Store to v2");
                    temp_pe.putString("SETUP_DRIVER_ARCH", "v2");
                    break;
                default:
                    Log.d("HXTP", "Store to default");
                    temp_pe.putString("SETUP_DRIVER_ARCH", "v1");
                    break;
            }

            temp_pe.putString("SETUP_DIR_NODE", setup_dir_text.getText().toString());

            temp_pe.putString("SETUP_DIAG_NODE", setup_diag_text.getText().toString());
            temp_pe.putBoolean("SETUP_DIAG_NODE_CHK", setup_diag_chk_full.isChecked());

            temp_pe.putString("SETUP_REGISTER_NODE", setup_register_text.getText().toString());
            temp_pe.putBoolean("SETUP_REGISTER_NODE_CHK", setup_register_chk_full.isChecked());

            temp_pe.putString("SETUP_DEBUGLEVEL_NODE", setup_debuglevel_text.getText().toString());
            temp_pe.putBoolean("SETUP_DEBUGLEVEL_NODE_CHK", setup_debuglevel_chk_full.isChecked());

            temp_pe.putString("SETUP_SELFTEST_NODE", setup_selftest_text.getText().toString());
            temp_pe.putBoolean("SETUP_SELFTEST_NODE_CHK", setup_selftest_chk_full.isChecked());

            temp_pe.putString("SETUP_RESET_NODE", setup_reset_text.getText().toString());
            temp_pe.putBoolean("SETUP_RESET_NODE_CHK", setup_reset_chk_full.isChecked());

            temp_pe.putString("SETUP_FLASHDUMP_NODE", setup_flashdump_text.getText().toString());
            temp_pe.putBoolean("SETUP_FLASHDUMP_NODE_CHK", setup_flashdump_chk_full.isChecked());

            temp_pe.putString("SETUP_DEBUG_NODE", setup_debug_text.getText().toString());
            temp_pe.putBoolean("SETUP_DEBUG_NODE_CHK", setup_debug_chk_full.isChecked());

            temp_pe.putString("SETUP_VENDOR_NODE", setup_vendor_text.getText().toString());
            temp_pe.putBoolean("SETUP_VENDOR_NODE_CHK", setup_vendor_chk_full.isChecked());

            temp_pe.putString("SETUP_SENSE_NODE", setup_sense_text.getText().toString());
            temp_pe.putBoolean("SETUP_SENSE_NODE_CHK", setup_sense_chk_full.isChecked());

            temp_pe.putString("SETUP_KLOG_TAG", setup_Klog_tag_text.getText().toString());


            temp_pe.commit();
            SetGlobalVar();
            Toast.makeText(this, "Save Complete.", Toast.LENGTH_SHORT).show();

            if (mNodeAcc != null) {
                mNodeAcc = null;
                mNodeAcc = new NodeDataSource(getApplicationContext());
            }

        }
        if (v == setup_diag_title) {
            ShowWindow2SetDiagCmd();
            Toast.makeText(this, "test textview click", Toast.LENGTH_SHORT).show();
        }
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.setup, menu);
//		return true;
//	}


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == setup_diag_chk_full) {
            if (setup_diag_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_diag_text.setText(String.format("%s%s", setup_dir, setup_diag));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_diag_text.setText(setup_diag);
            }
        } else if (buttonView == setup_debug_chk_full) {
            if (setup_debug_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_debug_text.setText(String.format("%s%s", setup_dir, setup_debug));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_debug_text.setText(setup_debug);
            }
        } else if (buttonView == setup_register_chk_full) {
            if (setup_register_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_register_text.setText(String.format("%s%s", setup_dir, setup_register));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_register_text.setText(setup_register);
            }
        } else if (buttonView == setup_debuglevel_chk_full) {
            if (setup_debuglevel_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_debuglevel_text.setText(String.format("%s%s", setup_dir, setup_debuglevel));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_debuglevel_text.setText(setup_debuglevel);
            }
        } else if (buttonView == setup_flashdump_chk_full) {
            if (setup_flashdump_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_flashdump_text.setText(String.format("%s%s", setup_dir, setup_flashdump));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_flashdump_text.setText(setup_flashdump);
            }
        } else if (buttonView == setup_selftest_chk_full) {
            if (setup_selftest_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_selftest_text.setText(String.format("%s%s", setup_dir, setup_selftest));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_selftest_text.setText(setup_selftest);
            }
        } else if (buttonView == setup_reset_chk_full) {
            if (setup_reset_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_reset_text.setText(String.format("%s%s", setup_dir, setup_reset));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_reset_text.setText(setup_reset);
            }
        } else if (buttonView == setup_vendor_chk_full) {
            if (setup_vendor_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_vendor_text.setText(String.format("%s%s", setup_dir, setup_vendor));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_vendor_text.setText(setup_vendor);
            }
        } else if (buttonView == setup_sense_chk_full) {
            if (setup_sense_chk_full.isChecked()) {
                //Toast.makeText(this, "CHECKED", Toast.LENGTH_SHORT).show();
                setup_sense_text.setText(String.format("%s%s", setup_dir, setup_sense));
            } else {
                //Toast.makeText(this, "un_check", Toast.LENGTH_SHORT).show();
                setup_sense_text.setText(setup_sense);
            }
        }
    }

    private void SelectV1Item() {
        setup_diag_title.setClickable(false);


//					setup_debuglevel_lay.setEnabled(true);//
        setup_debuglevel_lay.setVisibility(View.VISIBLE);

        setup_flashdump_lay.setVisibility(View.VISIBLE);

        setup_reset_lay.setVisibility(View.VISIBLE);

        setup_register_lay.setVisibility(View.VISIBLE);

        setup_sense_lay.setVisibility(View.VISIBLE);


//					setup_linear_layout.addView(setup_debuglevel_lay);
    }

    private void SelectV2Item() {
        setup_diag_title.setClickable(true);
        setup_diag_title.setOnClickListener(this);

//					setup_linear_layout.removeView(setup_debuglevel_lay);
//                    setup_debuglevel_lay.setEnabled(false);
        setup_debuglevel_lay.setVisibility(View.INVISIBLE);

        setup_flashdump_lay.setVisibility(View.INVISIBLE);

        setup_reset_lay.setVisibility(View.INVISIBLE);

        setup_register_lay.setVisibility(View.INVISIBLE);

        setup_sense_lay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == setup_arch_select.getId()) {
            switch (position) {
                case 0:
                    gSetupArchSlctVal = 0;
                    SelectV1Item();
                    break;
                case 1:
                    gSetupArchSlctVal = 1;
                    SelectV2Item();
                    break;
                default:
                    gSetupArchSlctVal = 0;
                    SelectV1Item();
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private AlertDialog.Builder DiagDailogBuilder;
    private LayoutInflater mInflater;
    private View g_diag_v2_Layout;

    private void ShowWindow2SetDiagCmd() {
        setup_v2_diag_stack = sh_settings.getString("SETUP_DIAG_V2_STACK", "stack");
        setup_v2_diag_siir = sh_settings.getString("SETUP_DIAG_V2_SIIR", "iir_s");
        setup_v2_diag_sdc = sh_settings.getString("SETUP_DIAG_V2_SDC", "dc_s");
        setup_v2_diag_sbank = sh_settings.getString("SETUP_DIAG_V2_SBANK", "bank_s");

        Log.d("HXTP", "setup_v2_diag_stack=" + setup_v2_diag_stack);

        AlertDialog.Builder DiagDailogBuilder = new AlertDialog.Builder(SetupActivity.this);
        //DiagDailogBuilder = new AlertDialog.Builder(SetupActivity.this);

        if (g_diag_v2_Layout != null) {
            ViewGroup parent = (ViewGroup) g_diag_v2_Layout.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
        g_diag_v2_Layout = mInflater.inflate(R.layout.dialog_diagv2_view, (ViewGroup) findViewById(R.id.dialog_diag_v2_all_view));

        setup_v2_diag_stack_text = g_diag_v2_Layout.findViewById(R.id.diag_v2_event_text);
        setup_v2_diag_siir_text = g_diag_v2_Layout.findViewById(R.id.diag_v2_siir_text);
        setup_v2_diag_sdc_text = g_diag_v2_Layout.findViewById(R.id.diag_v2_sdc_text);
        setup_v2_diag_sbank_text = g_diag_v2_Layout.findViewById(R.id.diag_v2_sbank_text);

        setup_v2_diag_stack_text.setText(setup_v2_diag_stack);
        setup_v2_diag_siir_text.setText(setup_v2_diag_siir);
        setup_v2_diag_sdc_text.setText(setup_v2_diag_sdc);
        setup_v2_diag_sbank_text.setText(setup_v2_diag_sbank);

        DiagDailogBuilder.setTitle("Set the diag V2's node name");
        DiagDailogBuilder.setIcon(R.drawable.ic_launcher);
        DiagDailogBuilder.setView(g_diag_v2_Layout);
        DiagDailogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SetupActivity.this, "Save the diag v2 setting", Toast.LENGTH_SHORT).show();

                Log.d("HXTP", "Now stack string=" + setup_v2_diag_stack_text.getText().toString());
                SharedPreferences.Editor temp_pe = sh_settings.edit();
                temp_pe.putString("SETUP_DIAG_V2_STACK", setup_v2_diag_stack_text.getText().toString());
                temp_pe.putString("SETUP_DIAG_V2_SIIR", setup_v2_diag_siir_text.getText().toString());
                temp_pe.putString("SETUP_DIAG_V2_SDC", setup_v2_diag_sdc_text.getText().toString());
                temp_pe.putString("SETUP_DIAG_V2_SBANK", setup_v2_diag_sbank_text.getText().toString());

                temp_pe.commit();
            }
        });
        DiagDailogBuilder.setNegativeButton("Cancel", null);

        DiagDailogBuilder.show();
    }
}
