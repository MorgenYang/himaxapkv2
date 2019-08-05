package com.ln.himaxtouch.TouchTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ln.himaxtouch.R;
import com.ln.himaxtouch.himax_config;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestResultActivity extends Activity {
    public String log;
    Button acc;
    Button dia;
    Button hor;
    Button ver;
    Button sen;
    Button seg;
    Button mtwo;
    Button meg;
    Button rate;
    Button name;
    Button out;
    Button result;
    TextView textView;

    int check = 0;
    int width;
    int height;
    boolean checked;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics rmetrics = new DisplayMetrics();
        display.getRealMetrics(rmetrics);
        SharedPreferences getData = getSharedPreferences("data", MODE_PRIVATE);
        checked = getData.getBoolean("vir_key", true);
        if (!checked) {
            width = rmetrics.widthPixels;
            height = rmetrics.heightPixels;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        acc = (Button) findViewById(R.id.acc);
        dia = (Button) findViewById(R.id.dia);
        hor = (Button) findViewById(R.id.hor);
        ver = (Button) findViewById(R.id.ver);
        sen = (Button) findViewById(R.id.sen);
        seg = (Button) findViewById(R.id.senedge);
        mtwo = (Button) findViewById(R.id.two);
        meg = (Button) findViewById(R.id.medge);
        rate = (Button) findViewById(R.id.rate);
        name = (Button) findViewById(R.id.name);
        out = (Button) findViewById(R.id.out);
        result = (Button) findViewById(R.id.result);
        textView = (TextView) findViewById(R.id.log);

        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        if (data.getString("accuracy_click_num_text", "").equals("")) {
            acc.setVisibility(View.INVISIBLE);
            dia.setVisibility(View.INVISIBLE);
            hor.setVisibility(View.INVISIBLE);
            ver.setVisibility(View.INVISIBLE);
            sen.setVisibility(View.INVISIBLE);
            seg.setVisibility(View.INVISIBLE);
            mtwo.setVisibility(View.INVISIBLE);
            meg.setVisibility(View.INVISIBLE);
            rate.setVisibility(View.INVISIBLE);
            name.setVisibility(View.INVISIBLE);
            out.setVisibility(View.INVISIBLE);
            result.setVisibility(View.INVISIBLE);
            textView.setText("Please go back and enter settings,\n\nthen test first!");
            return;
        }
        final int accuracy_click_num = Integer.parseInt(data.getString("accuracy_click_num_text", ""));
        final double test_bar_radius = Double.parseDouble(data.getString("test_bar_radius_text", ""));
        final double lcd_width = Double.parseDouble(data.getString("lcd_width_text", ""));
        final double accuracy_center_threshold = Double.parseDouble(data.getString("accuracy_center_threshold_text", ""));
        final double accuracy_edge_threshold = Double.parseDouble(data.getString("accuracy_edge_threshold_text", ""));
        final double lineation_center_threshold = Double.parseDouble(data.getString("lineation_center_threshold_text", ""));
        final double lineation_edge_threshold = Double.parseDouble(data.getString("lineation_edge_threshold_text", ""));
        final double a = width / lcd_width;
        //ver x
        final int firstVer = (int) (a * test_bar_radius);
        final int secondVer = (int) ((width - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        final int thirdVer = width / 2;
        final int fourthVer = (int) (3 * (width - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        final int fifthVer = (int) (width - a * test_bar_radius);
        //hor y
        final int firstHor = (int) (a * test_bar_radius);
        final int secondHor = (int) ((height - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        final int thirdHor = height / 2;
        final int fourthHor = (int) (3 * (height - 2 * test_bar_radius * a) / 4 + a * test_bar_radius);
        final int fifthHor = (int) (height - a * test_bar_radius);

        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 0;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String accTime = data.getString("acc", "");
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!accTime.isEmpty()) {
                    String accData = read(accTime, "acc_");
                    if (accData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df1 = new DecimalFormat("0.0");
                    String[] points;
                    points = accData.split("\n");
                    String[][] point;
                    point = new String[points.length][2];

                    if (accuracy_click_num == 1) {
                        int[] x;
                        int[] y;
                        boolean flag = true;
                        x = new int[points.length];
                        y = new int[points.length];
                        double[] distance;
                        distance = new double[13];
                        for (int i = 0; i < 13; i++) {
                            distance[i] = 0;
                        }

                        for (int i = 0; i < points.length; i++) {
                            point[i] = points[i].split(",");
                            x[i] = Integer.parseInt(point[i][0]);
                            y[i] = Integer.parseInt(point[i][1]);
                        }

                        int[] countPoint;
                        countPoint = new int[13];
                        for (int i = 0; i < 13; i++) {
                            countPoint[i] = 0;
                        }

                        for (int i = 0; i < points.length; i++) {
                            if (x[i] >= 0 && x[i] <= firstVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[0]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[1]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[2]++;
                            }
                            if (x[i] >= secondVer - test_bar_radius * a && x[i] <= secondVer + test_bar_radius * a) {
                                if (y[i] >= secondHor - test_bar_radius * a && y[i] <= secondHor + test_bar_radius * a)
                                    countPoint[3]++;
                                if (y[i] >= fourthHor - test_bar_radius * a && y[i] <= fourthHor + test_bar_radius * a)
                                    countPoint[4]++;
                            }
                            if (x[i] >= thirdVer - test_bar_radius * a && x[i] <= thirdVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[5]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[6]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[7]++;
                            }
                            if (x[i] >= fourthVer - test_bar_radius * a && x[i] <= fourthVer + test_bar_radius * a) {
                                if (y[i] >= secondHor - test_bar_radius * a && y[i] <= secondHor + test_bar_radius * a)
                                    countPoint[8]++;
                                if (y[i] >= fourthHor - test_bar_radius * a && y[i] <= fourthHor + test_bar_radius * a)
                                    countPoint[9]++;
                            }
                            if (x[i] >= fifthVer - test_bar_radius * a && x[i] <= fifthVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[10]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[11]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[12]++;
                            }
                        }

                        //move point to right resolution
                        int[] pointX;
                        int[] pointY;
                        pointX = new int[13];
                        pointY = new int[13];
                        int j = 0;
                        for (int i = 0; i < pointX.length; i++) {
                            while (countPoint[i] == 0) {
                                pointX[i] = 0;
                                pointY[i] = 0;
                                i++;
                                if (i == 13) {
                                    break;
                                }
                            }
                            if (i == 13) {
                                break;
                            }
                            pointX[i] = x[j];
                            pointY[i] = y[j];
                            j++;
                        }

                        if (countPoint[0] == 1)
                            distance[0] = Math.sqrt((pointX[0] - firstVer) * (pointX[0] - firstVer) + (pointY[0] - firstHor) * (pointY[0] - firstHor));
                        if (countPoint[1] == 1)
                            distance[1] = Math.sqrt((pointX[1] - firstVer) * (pointX[1] - firstVer) + (pointY[1] - thirdHor) * (pointY[1] - thirdHor));
                        if (countPoint[2] == 1)
                            distance[2] = Math.sqrt((pointX[2] - firstVer) * (pointX[2] - firstVer) + (pointY[2] - fifthHor) * (pointY[2] - fifthHor));
                        if (countPoint[3] == 1)
                            distance[3] = Math.sqrt((pointX[3] - secondVer) * (pointX[3] - secondVer) + (pointY[3] - secondHor) * (pointY[3] - secondHor));
                        if (countPoint[4] == 1)
                            distance[4] = Math.sqrt((pointX[4] - secondVer) * (pointX[4] - secondVer) + (pointY[4] - fourthHor) * (pointY[4] - fourthHor));
                        if (countPoint[5] == 1)
                            distance[5] = Math.sqrt((pointX[5] - thirdVer) * (pointX[5] - thirdVer) + (pointY[5] - firstHor) * (pointY[5] - firstHor));
                        if (countPoint[6] == 1)
                            distance[6] = Math.sqrt((pointX[6] - thirdVer) * (pointX[6] - thirdVer) + (pointY[6] - thirdHor) * (pointY[6] - thirdHor));
                        if (countPoint[7] == 1)
                            distance[7] = Math.sqrt((pointX[7] - thirdVer) * (pointX[7] - thirdVer) + (pointY[7] - fifthHor) * (pointY[7] - fifthHor));
                        if (countPoint[8] == 1)
                            distance[8] = Math.sqrt((pointX[8] - fourthVer) * (pointX[8] - fourthVer) + (pointY[8] - secondHor) * (pointY[8] - secondHor));
                        if (countPoint[9] == 1)
                            distance[9] = Math.sqrt((pointX[9] - fourthVer) * (pointX[9] - fourthVer) + (pointY[9] - fourthHor) * (pointY[9] - fourthHor));
                        if (countPoint[10] == 1)
                            distance[10] = Math.sqrt((pointX[10] - fifthVer) * (pointX[10] - fifthVer) + (pointY[10] - firstHor) * (pointY[10] - firstHor));
                        if (countPoint[11] == 1)
                            distance[11] = Math.sqrt((pointX[11] - fifthVer) * (pointX[11] - fifthVer) + (pointY[11] - thirdHor) * (pointY[11] - thirdHor));
                        if (countPoint[12] == 1)
                            distance[12] = Math.sqrt((pointX[12] - fifthVer) * (pointX[12] - fifthVer) + (pointY[12] - fifthHor) * (pointY[12] - fifthHor));

                        for (int i = 0; i < 13; i++) {
                            if (i < 3 || i == 5 || i == 7 || (i >= 10 && i <= 12)) {
                                if (countPoint[i] == 0) {
                                    textView.append("[ edge ] ------ miss\n");
                                    flag = false;
                                } else {
                                    if (distance[i] > accuracy_edge_threshold * a) {
                                        textView.append("[ edge ](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)--fail\n");
                                        flag = false;
                                    } else
                                        textView.append("[ edge ](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)\n");
                                }
                            } else if ((i >= 3 && i < 5) || i == 6 || (i > 7 && i < 10)) {
                                if (countPoint[i] == 0) {
                                    textView.append("[center] ------ miss\n");
                                    flag = false;
                                } else {
                                    if (distance[i] > accuracy_center_threshold * a) {
                                        textView.append("[center](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)--fail\n");
                                        flag = false;
                                    } else
                                        textView.append("[center](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)\n");
                                }
                            }
                        }
                        if (!flag) {
                            result.setBackgroundColor(Color.RED);
                            result.setText("ACC\nfail");
                        } else {
                            result.setBackgroundColor(Color.GREEN);
                            result.setText("ACC\npass");
                        }
                    }
                    if (accuracy_click_num == 3) {
                        int[] x;
                        int[] y;
                        boolean flag = true;
                        x = new int[points.length];
                        y = new int[points.length];
                        double[] distance;
                        distance = new double[39];
                        for (int i = 0; i < 39; i++) {
                            distance[i] = 0;
                        }

                        for (int i = 0; i < points.length; i++) {
                            point[i] = points[i].split(",");
                            x[i] = Integer.parseInt(point[i][0]);
                            y[i] = Integer.parseInt(point[i][1]);
                        }

                        int[] countPoint;
                        countPoint = new int[13];
                        for (int i = 0; i < 13; i++) {
                            countPoint[i] = 0;
                        }

                        for (int i = 0; i < points.length; i++) {
                            if (x[i] >= 0 && x[i] <= firstVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[0]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[1]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[2]++;
                            }
                            if (x[i] >= secondVer - test_bar_radius * a && x[i] <= secondVer + test_bar_radius * a) {
                                if (y[i] >= secondHor - test_bar_radius * a && y[i] <= secondHor + test_bar_radius * a)
                                    countPoint[3]++;
                                if (y[i] >= fourthHor - test_bar_radius * a && y[i] <= fourthHor + test_bar_radius * a)
                                    countPoint[4]++;
                            }
                            if (x[i] >= thirdVer - test_bar_radius * a && x[i] <= thirdVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[5]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[6]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[7]++;
                            }
                            if (x[i] >= fourthVer - test_bar_radius * a && x[i] <= fourthVer + test_bar_radius * a) {
                                if (y[i] >= secondHor - test_bar_radius * a && y[i] <= secondHor + test_bar_radius * a)
                                    countPoint[8]++;
                                if (y[i] >= fourthHor - test_bar_radius * a && y[i] <= fourthHor + test_bar_radius * a)
                                    countPoint[9]++;
                            }
                            if (x[i] >= fifthVer - test_bar_radius * a && x[i] <= fifthVer + test_bar_radius * a) {
                                if (y[i] >= 0 && y[i] <= firstHor + test_bar_radius * a)
                                    countPoint[10]++;
                                if (y[i] >= thirdHor - test_bar_radius * a && y[i] <= thirdHor + test_bar_radius * a)
                                    countPoint[11]++;
                                if (y[i] >= height - 2 * a * test_bar_radius && y[i] <= height)
                                    countPoint[12]++;
                            }
                        }

                        //move point to right resolution
                        int[] sum = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        for (int i = 0; i < 13; i++) {
                            if (i == 0)
                                sum[i] = countPoint[i];
                            if (i > 0)
                                sum[i] = sum[i - 1] + countPoint[i];
                        }
                        int[] pointX;
                        int[] pointY;
                        pointX = new int[39];
                        pointY = new int[39];
                        for (int i = 0; i < 13; i++) {
                            if (i == 0) {
                                if (countPoint[0] == 0) {
                                    pointX[0] = 0;
                                    pointY[0] = 0;
                                    pointX[1] = 0;
                                    pointY[1] = 0;
                                    pointX[2] = 0;
                                    pointY[2] = 0;
                                } else if (countPoint[0] == 1) {
                                    pointX[0] = 0;
                                    pointY[0] = 0;
                                    pointX[1] = 0;
                                    pointY[1] = 0;
                                    pointX[2] = x[0];
                                    pointY[2] = y[0];
                                } else if (countPoint[0] == 2) {
                                    pointX[0] = 0;
                                    pointY[0] = 0;
                                    pointX[1] = x[0];
                                    pointY[1] = y[0];
                                    pointX[2] = x[1];
                                    pointY[2] = y[1];
                                } else if (countPoint[0] == 3) {
                                    pointX[0] = x[0];
                                    pointY[0] = y[0];
                                    pointX[1] = x[1];
                                    pointY[1] = y[1];
                                    pointX[2] = x[2];
                                    pointY[2] = y[2];
                                }
                            }
                            if (i > 0) {
                                if (countPoint[i] == 0) {
                                    pointX[3 * i] = 0;
                                    pointY[3 * i] = 0;
                                    pointX[3 * i + 1] = 0;
                                    pointY[3 * i + 1] = 0;
                                    pointX[3 * i + 2] = 0;
                                    pointY[3 * i + 2] = 0;
                                } else if (countPoint[i] == 1) {
                                    pointX[3 * i] = 0;
                                    pointY[3 * i] = 0;
                                    pointX[3 * i + 1] = 0;
                                    pointY[3 * i + 1] = 0;
                                    pointX[3 * i + 2] = x[sum[i - 1]];
                                    pointY[3 * i + 2] = y[sum[i - 1]];
                                } else if (countPoint[i] == 2) {
                                    pointX[3 * i] = 0;
                                    pointY[3 * i] = 0;
                                    pointX[3 * i + 1] = x[sum[i - 1]];
                                    pointY[3 * i + 1] = y[sum[i - 1]];
                                    pointX[3 * i + 2] = x[sum[i - 1] + 1];
                                    pointY[3 * i + 2] = y[sum[i - 1] + 1];
                                } else if (countPoint[i] == 3) {
                                    pointX[3 * i] = x[sum[i - 1]];
                                    pointY[3 * i] = y[sum[i - 1]];
                                    pointX[3 * i + 1] = x[sum[i - 1] + 1];
                                    pointY[3 * i + 1] = y[sum[i - 1] + 1];
                                    pointX[3 * i + 2] = x[sum[i - 1] + 2];
                                    pointY[3 * i + 2] = y[sum[i - 1] + 2];
                                }
                            }
                        }

                        //get distance data
                        if (countPoint[0] == 1) {
                            distance[2] = Math.sqrt((pointX[2] - firstVer) * (pointX[2] - firstVer) + (pointY[2] - firstHor) * (pointY[2] - firstHor));
                        } else if (countPoint[0] == 2) {
                            distance[1] = Math.sqrt((pointX[1] - firstVer) * (pointX[1] - firstVer) + (pointY[1] - firstHor) * (pointY[1] - firstHor));
                            distance[2] = Math.sqrt((pointX[2] - firstVer) * (pointX[2] - firstVer) + (pointY[2] - firstHor) * (pointY[2] - firstHor));
                        } else if (countPoint[0] == 3) {
                            distance[0] = Math.sqrt((pointX[0] - firstVer) * (pointX[0] - firstVer) + (pointY[0] - firstHor) * (pointY[0] - firstHor));
                            distance[1] = Math.sqrt((pointX[1] - firstVer) * (pointX[1] - firstVer) + (pointY[1] - firstHor) * (pointY[1] - firstHor));
                            distance[2] = Math.sqrt((pointX[2] - firstVer) * (pointX[2] - firstVer) + (pointY[2] - firstHor) * (pointY[2] - firstHor));
                        }

                        if (countPoint[1] == 1) {
                            distance[5] = Math.sqrt((pointX[5] - firstVer) * (pointX[5] - firstVer) + (pointY[5] - thirdHor) * (pointY[5] - thirdHor));
                        } else if (countPoint[1] == 2) {
                            distance[4] = Math.sqrt((pointX[4] - firstVer) * (pointX[4] - firstVer) + (pointY[4] - thirdHor) * (pointY[4] - thirdHor));
                            distance[5] = Math.sqrt((pointX[5] - firstVer) * (pointX[5] - firstVer) + (pointY[5] - thirdHor) * (pointY[5] - thirdHor));
                        } else if (countPoint[1] == 3) {
                            distance[3] = Math.sqrt((pointX[3] - firstVer) * (pointX[3] - firstVer) + (pointY[3] - thirdHor) * (pointY[3] - thirdHor));
                            distance[4] = Math.sqrt((pointX[4] - firstVer) * (pointX[4] - firstVer) + (pointY[4] - thirdHor) * (pointY[4] - thirdHor));
                            distance[5] = Math.sqrt((pointX[5] - firstVer) * (pointX[5] - firstVer) + (pointY[5] - thirdHor) * (pointY[5] - thirdHor));
                        }

                        if (countPoint[2] == 1) {
                            distance[8] = Math.sqrt((pointX[8] - firstVer) * (pointX[8] - firstVer) + (pointY[8] - fifthHor) * (pointY[8] - fifthHor));
                        } else if (countPoint[2] == 2) {
                            distance[7] = Math.sqrt((pointX[7] - firstVer) * (pointX[7] - firstVer) + (pointY[7] - fifthHor) * (pointY[7] - fifthHor));
                            distance[8] = Math.sqrt((pointX[8] - firstVer) * (pointX[8] - firstVer) + (pointY[8] - fifthHor) * (pointY[8] - fifthHor));
                        } else if (countPoint[2] == 3) {
                            distance[6] = Math.sqrt((pointX[6] - firstVer) * (pointX[6] - firstVer) + (pointY[6] - fifthHor) * (pointY[6] - fifthHor));
                            distance[7] = Math.sqrt((pointX[7] - firstVer) * (pointX[7] - firstVer) + (pointY[7] - fifthHor) * (pointY[7] - fifthHor));
                            distance[8] = Math.sqrt((pointX[8] - firstVer) * (pointX[8] - firstVer) + (pointY[8] - fifthHor) * (pointY[8] - fifthHor));
                        }

                        if (countPoint[3] == 1) {
                            distance[11] = Math.sqrt((pointX[11] - secondVer) * (pointX[11] - secondVer) + (pointY[11] - secondHor) * (pointY[11] - secondHor));
                        } else if (countPoint[3] == 2) {
                            distance[10] = Math.sqrt((pointX[10] - secondVer) * (pointX[10] - secondVer) + (pointY[10] - secondHor) * (pointY[10] - secondHor));
                            distance[11] = Math.sqrt((pointX[11] - secondVer) * (pointX[11] - secondVer) + (pointY[11] - secondHor) * (pointY[11] - secondHor));
                        } else if (countPoint[3] == 3) {
                            distance[9] = Math.sqrt((pointX[9] - secondVer) * (pointX[9] - secondVer) + (pointY[9] - secondHor) * (pointY[9] - secondHor));
                            distance[10] = Math.sqrt((pointX[10] - secondVer) * (pointX[10] - secondVer) + (pointY[10] - secondHor) * (pointY[10] - secondHor));
                            distance[11] = Math.sqrt((pointX[11] - secondVer) * (pointX[11] - secondVer) + (pointY[11] - secondHor) * (pointY[11] - secondHor));
                        }

                        if (countPoint[4] == 1) {
                            distance[14] = Math.sqrt((pointX[14] - secondVer) * (pointX[14] - secondVer) + (pointY[14] - fourthHor) * (pointY[14] - fourthHor));
                        } else if (countPoint[4] == 2) {
                            distance[13] = Math.sqrt((pointX[13] - secondVer) * (pointX[13] - secondVer) + (pointY[13] - fourthHor) * (pointY[13] - fourthHor));
                            distance[14] = Math.sqrt((pointX[14] - secondVer) * (pointX[14] - secondVer) + (pointY[14] - fourthHor) * (pointY[14] - fourthHor));
                        } else if (countPoint[4] == 3) {
                            distance[12] = Math.sqrt((pointX[12] - secondVer) * (pointX[12] - secondVer) + (pointY[12] - fourthHor) * (pointY[12] - fourthHor));
                            distance[13] = Math.sqrt((pointX[13] - secondVer) * (pointX[13] - secondVer) + (pointY[13] - fourthHor) * (pointY[13] - fourthHor));
                            distance[14] = Math.sqrt((pointX[14] - secondVer) * (pointX[14] - secondVer) + (pointY[14] - fourthHor) * (pointY[14] - fourthHor));
                        }

                        if (countPoint[5] == 1) {
                            distance[17] = Math.sqrt((pointX[17] - thirdVer) * (pointX[17] - thirdVer) + (pointY[17] - firstHor) * (pointY[17] - firstHor));
                        } else if (countPoint[5] == 2) {
                            distance[16] = Math.sqrt((pointX[16] - thirdVer) * (pointX[16] - thirdVer) + (pointY[16] - firstHor) * (pointY[16] - firstHor));
                            distance[17] = Math.sqrt((pointX[17] - thirdVer) * (pointX[17] - thirdVer) + (pointY[17] - firstHor) * (pointY[17] - firstHor));
                        } else if (countPoint[5] == 3) {
                            distance[15] = Math.sqrt((pointX[15] - thirdVer) * (pointX[15] - thirdVer) + (pointY[15] - firstHor) * (pointY[15] - firstHor));
                            distance[16] = Math.sqrt((pointX[16] - thirdVer) * (pointX[16] - thirdVer) + (pointY[16] - firstHor) * (pointY[16] - firstHor));
                            distance[17] = Math.sqrt((pointX[17] - thirdVer) * (pointX[17] - thirdVer) + (pointY[17] - firstHor) * (pointY[17] - firstHor));
                        }

                        if (countPoint[6] == 1) {
                            distance[20] = Math.sqrt((pointX[20] - thirdVer) * (pointX[20] - thirdVer) + (pointY[20] - thirdHor) * (pointY[20] - thirdHor));
                        } else if (countPoint[6] == 2) {
                            distance[19] = Math.sqrt((pointX[19] - thirdVer) * (pointX[19] - thirdVer) + (pointY[19] - thirdHor) * (pointY[19] - thirdHor));
                            distance[20] = Math.sqrt((pointX[20] - thirdVer) * (pointX[20] - thirdVer) + (pointY[20] - thirdHor) * (pointY[20] - thirdHor));
                        } else if (countPoint[6] == 3) {
                            distance[18] = Math.sqrt((pointX[18] - thirdVer) * (pointX[18] - thirdVer) + (pointY[18] - thirdHor) * (pointY[18] - thirdHor));
                            distance[19] = Math.sqrt((pointX[19] - thirdVer) * (pointX[19] - thirdVer) + (pointY[19] - thirdHor) * (pointY[19] - thirdHor));
                            distance[20] = Math.sqrt((pointX[20] - thirdVer) * (pointX[20] - thirdVer) + (pointY[20] - thirdHor) * (pointY[20] - thirdHor));
                        }

                        if (countPoint[7] == 1) {
                            distance[23] = Math.sqrt((pointX[23] - thirdVer) * (pointX[23] - thirdVer) + (pointY[23] - fifthHor) * (pointY[23] - fifthHor));
                        } else if (countPoint[7] == 2) {
                            distance[21] = 0;
                            distance[22] = Math.sqrt((pointX[22] - thirdVer) * (pointX[22] - thirdVer) + (pointY[22] - fifthHor) * (pointY[22] - fifthHor));
                            distance[23] = Math.sqrt((pointX[23] - thirdVer) * (pointX[23] - thirdVer) + (pointY[23] - fifthHor) * (pointY[23] - fifthHor));
                        } else if (countPoint[7] == 3) {
                            distance[21] = Math.sqrt((pointX[21] - thirdVer) * (pointX[21] - thirdVer) + (pointY[21] - fifthHor) * (pointY[21] - fifthHor));
                            distance[22] = Math.sqrt((pointX[22] - thirdVer) * (pointX[22] - thirdVer) + (pointY[22] - fifthHor) * (pointY[22] - fifthHor));
                            distance[23] = Math.sqrt((pointX[23] - thirdVer) * (pointX[23] - thirdVer) + (pointY[23] - fifthHor) * (pointY[23] - fifthHor));
                        }

                        if (countPoint[8] == 1) {
                            distance[26] = Math.sqrt((pointX[26] - fourthVer) * (pointX[26] - fourthVer) + (pointY[26] - secondHor) * (pointY[26] - secondHor));
                        } else if (countPoint[8] == 2) {
                            distance[25] = Math.sqrt((pointX[25] - fourthVer) * (pointX[25] - fourthVer) + (pointY[25] - secondHor) * (pointY[25] - secondHor));
                            distance[26] = Math.sqrt((pointX[26] - fourthVer) * (pointX[26] - fourthVer) + (pointY[26] - secondHor) * (pointY[26] - secondHor));
                        } else if (countPoint[8] == 3) {
                            distance[24] = Math.sqrt((pointX[24] - fourthVer) * (pointX[24] - fourthVer) + (pointY[24] - secondHor) * (pointY[24] - secondHor));
                            distance[25] = Math.sqrt((pointX[25] - fourthVer) * (pointX[25] - fourthVer) + (pointY[25] - secondHor) * (pointY[25] - secondHor));
                            distance[26] = Math.sqrt((pointX[26] - fourthVer) * (pointX[26] - fourthVer) + (pointY[26] - secondHor) * (pointY[26] - secondHor));
                        }

                        if (countPoint[9] == 1) {
                            distance[29] = Math.sqrt((pointX[9] - fourthVer) * (pointX[9] - fourthVer) + (pointY[9] - fourthHor) * (pointY[9] - fourthHor));
                        } else if (countPoint[9] == 2) {
                            distance[28] = Math.sqrt((pointX[28] - fourthVer) * (pointX[28] - fourthVer) + (pointY[28] - fourthHor) * (pointY[28] - fourthHor));
                            distance[29] = Math.sqrt((pointX[29] - fourthVer) * (pointX[29] - fourthVer) + (pointY[29] - fourthHor) * (pointY[29] - fourthHor));
                        } else if (countPoint[9] == 3) {
                            distance[27] = Math.sqrt((pointX[27] - fourthVer) * (pointX[27] - fourthVer) + (pointY[27] - fourthHor) * (pointY[27] - fourthHor));
                            distance[28] = Math.sqrt((pointX[28] - fourthVer) * (pointX[28] - fourthVer) + (pointY[28] - fourthHor) * (pointY[28] - fourthHor));
                            distance[29] = Math.sqrt((pointX[29] - fourthVer) * (pointX[29] - fourthVer) + (pointY[29] - fourthHor) * (pointY[29] - fourthHor));
                        }

                        if (countPoint[10] == 1) {
                            distance[32] = Math.sqrt((pointX[32] - fifthVer) * (pointX[32] - fifthVer) + (pointY[32] - firstHor) * (pointY[32] - firstHor));
                        } else if (countPoint[10] == 2) {
                            distance[31] = Math.sqrt((pointX[31] - fifthVer) * (pointX[31] - fifthVer) + (pointY[31] - firstHor) * (pointY[31] - firstHor));
                            distance[32] = Math.sqrt((pointX[32] - fifthVer) * (pointX[32] - fifthVer) + (pointY[32] - firstHor) * (pointY[32] - firstHor));
                        } else if (countPoint[10] == 3) {
                            distance[30] = Math.sqrt((pointX[30] - fifthVer) * (pointX[30] - fifthVer) + (pointY[30] - firstHor) * (pointY[30] - firstHor));
                            distance[31] = Math.sqrt((pointX[31] - fifthVer) * (pointX[31] - fifthVer) + (pointY[31] - firstHor) * (pointY[31] - firstHor));
                            distance[32] = Math.sqrt((pointX[32] - fifthVer) * (pointX[32] - fifthVer) + (pointY[32] - firstHor) * (pointY[32] - firstHor));
                        }

                        if (countPoint[11] == 1) {
                            distance[35] = Math.sqrt((pointX[35] - fifthVer) * (pointX[35] - fifthVer) + (pointY[35] - thirdHor) * (pointY[35] - thirdHor));
                        } else if (countPoint[11] == 2) {
                            distance[34] = Math.sqrt((pointX[34] - fifthVer) * (pointX[34] - fifthVer) + (pointY[34] - thirdHor) * (pointY[34] - thirdHor));
                            distance[35] = Math.sqrt((pointX[35] - fifthVer) * (pointX[35] - fifthVer) + (pointY[35] - thirdHor) * (pointY[35] - thirdHor));
                        } else if (countPoint[11] == 3) {
                            distance[33] = Math.sqrt((pointX[33] - fifthVer) * (pointX[33] - fifthVer) + (pointY[33] - thirdHor) * (pointY[33] - thirdHor));
                            distance[34] = Math.sqrt((pointX[34] - fifthVer) * (pointX[34] - fifthVer) + (pointY[34] - thirdHor) * (pointY[34] - thirdHor));
                            distance[35] = Math.sqrt((pointX[35] - fifthVer) * (pointX[35] - fifthVer) + (pointY[35] - thirdHor) * (pointY[35] - thirdHor));
                        }

                        if (countPoint[12] == 1) {
                            distance[38] = Math.sqrt((pointX[38] - fifthVer) * (pointX[38] - fifthVer) + (pointY[38] - fifthHor) * (pointY[38] - fifthHor));
                        } else if (countPoint[12] == 2) {
                            distance[37] = Math.sqrt((pointX[37] - fifthVer) * (pointX[37] - fifthVer) + (pointY[37] - fifthHor) * (pointY[37] - fifthHor));
                            distance[38] = Math.sqrt((pointX[38] - fifthVer) * (pointX[38] - fifthVer) + (pointY[38] - fifthHor) * (pointY[38] - fifthHor));
                        } else if (countPoint[12] == 3) {
                            distance[36] = Math.sqrt((pointX[36] - fifthVer) * (pointX[36] - fifthVer) + (pointY[36] - fifthHor) * (pointY[36] - fifthHor));
                            distance[37] = Math.sqrt((pointX[37] - fifthVer) * (pointX[37] - fifthVer) + (pointY[37] - fifthHor) * (pointY[37] - fifthHor));
                            distance[38] = Math.sqrt((pointX[38] - fifthVer) * (pointX[38] - fifthVer) + (pointY[38] - fifthHor) * (pointY[38] - fifthHor));
                        }

                        //show in page
                        for (int i = 0; i < 39; i++) {
                            if (i < 9 || (i >= 15 && i < 18) || (i >= 21 && i < 24) || (i >= 30 && i <= 38)) {
                                if (distance[i] == 0) {
                                    textView.append("[ edge ] ------ miss\n");
                                    flag = false;
                                } else {
                                    if (distance[i] > accuracy_edge_threshold * a) {
                                        textView.append("[ edge ](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)-fail\n");
                                        flag = false;
                                    } else
                                        textView.append("[ edge ](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)\n");
                                }
                            } else if ((i >= 9 && i < 15) || (i >= 18 && i < 21) || (i >= 24 && i < 30)) {
                                if (distance[i] == 0) {
                                    textView.append("[center] ------ miss\n");
                                    flag = false;
                                } else {
                                    if (distance[i] > accuracy_center_threshold * a) {
                                        textView.append("[center](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)-fail\n");
                                        flag = false;
                                    } else
                                        textView.append("[center](" + pointX[i] + "," + pointY[i] + ")dis:" + df1.format(distance[i]) + "(pix)/" + df.format(distance[i] / a) + "(mm)\n");
                                }
                            }
                        }
                        if (!flag) {
                            result.setBackgroundColor(Color.RED);
                            result.setText("ACC\nfail");
                        } else {
                            result.setBackgroundColor(Color.GREEN);
                            result.setText("ACC\npass");
                        }
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 1;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String diaTime = data.getString("dia", "");
                String diaCount = data.getString("diaCount", "");
                List<String> list = new ArrayList<String>();
                textView.setText("Total Line Num : " + diaCount + "\n");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!diaTime.isEmpty()) {
                    boolean flag = true;
                    String diaData = read(diaTime, "dia_");
                    if (diaData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] points;
                    points = diaData.split("\n");
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df1 = new DecimalFormat("0.0");
                    String[][] point;
                    point = new String[points.length][2];
                    int j = 0;

                    for (int i = 0; i < points.length; i++) {
                        point[i] = points[i].split(",");
                        if (point[i][0].equals("65535")) {
                            list.add("65535");
                            j++;
                            continue;
                        }
                        if (i == 0) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                            j++;

                        } else if (point[i - 1][0].equals("65535") && (Integer.parseInt(point[i][1]) + 100) >= Integer.parseInt(point[i - 2][1])) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                            j++;
                        } else if (i > 0 && (Integer.parseInt(point[i][1]) + 100) >= Integer.parseInt(point[i - 1][1])) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                            j++;
                        } else break;
                    }
                    for (int i = j; i < points.length; i++) {
                        point[i] = points[i].split(",");
                        if (point[i][0].equals("65535")) {
                            list.add("65535");
//                            j++;
                            continue;
                        }
                        if (i == j) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - width * height + Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                        } else if (point[i - 1][0].equals("65535") && (Integer.parseInt(point[i][1]) + 100) >= Integer.parseInt(point[i - 2][1])) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - width * height + Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                        } else if (i > j && (Integer.parseInt(point[i][1]) + 100) >= Integer.parseInt(point[i - 1][1])) {
                            list.add(df1.format(Math.abs((height * Integer.parseInt(point[i][0]) - width * height + Integer.parseInt(point[i][1]) * width)
                                    / Math.sqrt(width * width + height * height))) + "");
                        }
                    }
                    himax_config.mLogPrinter('e', "list size=" + Integer.toString(list.size()));
                    String distanceDia = "";
                    for (int i = 0; i < list.size(); i++) {
                        himax_config.mLogPrinter('e', "now index=" + Integer.toString(i) + "points.length=" + Integer.toString(points.length));
                        if (point[i][0].equals("65535")) {
                            if (i < points.length - 1 && Integer.parseInt(point[i - 1][1]) < Integer.parseInt(point[i + 1][1])) {
                                //edge
                                if (Double.parseDouble(point[i - 1][0]) <= test_bar_radius * a
                                        || Double.parseDouble(point[i - 1][1]) <= test_bar_radius * a * height / width
                                        || width - Double.parseDouble(point[i - 1][0]) <= test_bar_radius * a
                                        || height - Double.parseDouble(point[i - 1][1]) <= test_bar_radius * a * height / width) {
                                    textView.append("[ edge ] ---------- miss\n");
                                } else
                                    textView.append("[center] ---------- miss\n");
                                flag = false;
                            }
                            continue;
                        }
                        if (i == 0) {
                            textView.append("Line one ");
                            if (Integer.parseInt(point[j - 2][1]) - Integer.parseInt(point[0][1]) < height - (int) (test_bar_radius * a * 2))
                                textView.append("is too short, fail!");
                            textView.append("\n------------------\n");
                        }
                        if (i == j) {
                            textView.append("\nLine two ");
                            if (Integer.parseInt(point[points.length - 1][1]) - Integer.parseInt(point[j][1]) < height - (int) (test_bar_radius * a * 2))
                                textView.append("is too short, fail!");
                            textView.append("\n------------------\n");
                        }
                        //edge
                        if (Double.parseDouble(point[i][0]) <= test_bar_radius * a
                                || Double.parseDouble(point[i][1]) <= test_bar_radius * a * height / width
                                || width - Double.parseDouble(point[i][0]) <= test_bar_radius * a
                                || height - Double.parseDouble(point[i][1]) <= test_bar_radius * a * height / width) {
                            if (Double.parseDouble(list.get(i) + "") / a > lineation_edge_threshold) {
                                distanceDia = "[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)-fail" + "\n";
                                textView.append(distanceDia);
                                flag = false;
                            } else {
                                distanceDia = "[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n";
                                textView.append(distanceDia);
                            }
                        } else {
                            if (Double.parseDouble(list.get(i) + "") / a > lineation_center_threshold) {
                                distanceDia = "[center](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)-fail\n";
                                textView.append(distanceDia);
                                flag = false;
                            } else {
                                distanceDia = "[center](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n";
                                textView.append(distanceDia);
                            }
                        }
                    }
                    if (flag) {
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("DIA\npass");
                    } else {
                        result.setBackgroundColor(Color.RED);
                        result.setText("DIA\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        hor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 2;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String horTime = data.getString("hor", "");
                String horCount = data.getString("horCount", "");
                List<String> list = new ArrayList<String>();
                textView.setText("Total Line Num : " + horCount);
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!horTime.isEmpty()) {
                    boolean flag = true;
                    String horData = read(horTime, "hor_");
                    if (horData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] points;
                    points = horData.split("\n");
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df1 = new DecimalFormat("0.0");
                    String[][] point;
                    point = new String[points.length][2];

                    //judge 5 lines and mark every line last point number
                    int line1PointNum = 0;
                    int line2PointNum = 0;
                    int line3PointNum = 0;
                    int line4PointNum = 0;
                    for (int i = 0; i < points.length; i++) {
                        point[i] = points[i].split(",");
                        if (point[i][0].equals("65535")) {
                            list.add("65535");
                            continue;
                        }
                        if (Integer.parseInt(point[i][1]) < firstHor + (secondHor - firstHor) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][1]) - firstHor)) + "");
                            line1PointNum++;
                        }
                        if (Integer.parseInt(point[i][1]) > firstHor + (secondHor - firstHor) / 2 && Integer.parseInt(point[i][1]) < secondHor + (thirdHor - secondHor) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][1]) - secondHor)) + "");
                            line2PointNum++;
                        }
                        if (Integer.parseInt(point[i][1]) > secondHor + (thirdHor - secondHor) / 2 && Integer.parseInt(point[i][1]) < thirdHor + (fourthHor - thirdHor) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][1]) - thirdHor)) + "");
                            line3PointNum++;
                        }
                        if (Integer.parseInt(point[i][1]) > thirdHor + (fourthHor - thirdHor) / 2 && Integer.parseInt(point[i][1]) < fourthHor + (fifthHor - fourthHor) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][1]) - fourthHor)) + "");
                            line4PointNum++;
                        }
                        if (Integer.parseInt(point[i][1]) > fourthHor + (fifthHor - fourthHor) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][1]) - fifthHor)) + "");
                        }
                    }

                    for (int i = 0; i < points.length; i++) {
                        if (point[i][0].equals("65535")) {
                            if (Integer.parseInt(point[i + 1][1]) < firstHor + (secondHor - firstHor) / 2) {
                                line1PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][1]) > firstHor + (secondHor - firstHor) / 2 && Integer.parseInt(point[i + 1][1]) < secondHor + (thirdHor - secondHor) / 2) {
                                line2PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][1]) > secondHor + (thirdHor - secondHor) / 2 && Integer.parseInt(point[i + 1][1]) < thirdHor + (fourthHor - thirdHor) / 2) {
                                line3PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][1]) > thirdHor + (fourthHor - thirdHor) / 2 && Integer.parseInt(point[i + 1][1]) < fourthHor + (fifthHor - fourthHor) / 2) {
                                line4PointNum++;
                            }
                        }
                    }

                    int two = line1PointNum + line2PointNum;
                    int three = line1PointNum + line2PointNum + line3PointNum;
                    int four = line1PointNum + line2PointNum + line3PointNum + line4PointNum;
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            textView.append("\nLine one\n");
                            textView.append("--------------------\n");
                        }
                        if (i == line1PointNum) {
                            textView.append("\nLine two \n");
                            textView.append("--------------------\n");
                        }
                        if (i == two) {
                            textView.append("\nLine three \n");
                            textView.append("--------------------\n");
                        }
                        if (i == three) {
                            textView.append("\nLine four \n");
                            textView.append("--------------------\n");
                        }
                        if (i == four) {
                            textView.append("\nLine five \n");
                            textView.append("--------------------\n");
                        }

                        //first line
                        if (i <= line1PointNum - 1 || (i <= points.length && i > four)) {
                            if (point[i][0].equals("65535")) {
                                textView.append("---------- miss\n");
                                flag = false;
                                continue;
                            }
                            if (Double.parseDouble(list.get(i) + "") / a > lineation_edge_threshold) {
                                textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                flag = false;
                            } else
                                textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                        }
                        if ((i <= two - 1 && i > line1PointNum)
                                || (i <= three - 1 && i > two)
                                || (i <= four - 1 && i > three)) {
                            if (point[i][0].equals("65535")) {
                                textView.append("---------- miss\n");
                                flag = false;
                                continue;
                            }
                            if (Integer.parseInt(point[i][0]) < test_bar_radius * a
                                    || (Integer.parseInt(point[i][0]) < width && Integer.parseInt(point[i][0]) > width - test_bar_radius * a)) {
                                if (Double.parseDouble(list.get(i) + "") / a > lineation_edge_threshold) {
                                    textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                    flag = false;
                                } else
                                    textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                            } else {
                                if (Double.parseDouble(list.get(i) + "") / a > lineation_center_threshold) {
                                    textView.append("[center](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                    flag = false;
                                } else
                                    textView.append("[center](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                            }
                        }
                    }
                    if (flag) {
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("HOR\npass");
                    } else {
                        result.setBackgroundColor(Color.RED);
                        result.setText("HOR\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 3;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String verTime = data.getString("ver", "");
                String verCount = data.getString("verCount", "");
                List<String> list = new ArrayList<String>();
                textView.setText("Total Line Num : " + verCount + "\n");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!verTime.isEmpty()) {
                    boolean flag = true;
                    String verData = read(verTime, "ver_");
                    if (verData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] points;
                    points = verData.split("\n");
                    DecimalFormat df = new DecimalFormat("0.00");
                    DecimalFormat df1 = new DecimalFormat("0.0");
                    String[][] point;
                    point = new String[points.length][2];

                    //judge 5 lines and mark every line last point number
                    int line1PointNum = 0;
                    int line2PointNum = 0;
                    int line3PointNum = 0;
                    int line4PointNum = 0;
                    for (int i = 0; i < points.length; i++) {
                        point[i] = points[i].split(",");
                        if (point[i][0].equals("65535")) {
                            list.add("65535");
                            continue;
                        }
                        if (Integer.parseInt(point[i][0]) < firstVer + (secondVer - firstVer) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][0]) - firstVer)) + "");
                            line1PointNum++;
                        }
                        if (Integer.parseInt(point[i][0]) > firstVer + (secondVer - firstVer) / 2 && Integer.parseInt(point[i][0]) < secondVer + (thirdVer - secondVer) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][0]) - secondVer)) + "");
                            line2PointNum++;
                        }
                        if (Integer.parseInt(point[i][0]) > secondVer + (thirdVer - secondVer) / 2 && Integer.parseInt(point[i][0]) < thirdVer + (fourthVer - thirdVer) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][0]) - thirdVer)) + "");
                            line3PointNum++;
                        }
                        if (Integer.parseInt(point[i][0]) > thirdVer + (fourthVer - thirdVer) / 2 && Integer.parseInt(point[i][0]) < fourthVer + (fifthVer - fourthVer) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][0]) - fourthVer)) + "");
                            line4PointNum++;
                        }
                        if (Integer.parseInt(point[i][0]) > fourthVer + (fifthVer - fourthVer) / 2) {
                            list.add(df1.format(Math.abs(Integer.parseInt(point[i][0]) - fifthVer)) + "");
                        }
                    }

                    for (int i = 0; i < points.length; i++) {
                        if (point[i][0].equals("65535")) {
                            if (Integer.parseInt(point[i + 1][0]) < firstVer + (secondVer - firstVer) / 2) {
                                line1PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][0]) > firstVer + (secondVer - firstVer) / 2 && Integer.parseInt(point[i + 1][0]) < secondVer + (thirdVer - secondVer) / 2) {
                                line2PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][0]) > secondVer + (thirdVer - secondVer) / 2 && Integer.parseInt(point[i + 1][0]) < thirdVer + (fourthVer - thirdVer) / 2) {
                                line3PointNum++;
                            }
                            if (Integer.parseInt(point[i + 1][0]) > thirdVer + (fourthVer - thirdVer) / 2 && Integer.parseInt(point[i + 1][0]) < fourthVer + (fifthVer - fourthVer) / 2) {
                                line4PointNum++;
                            }
                        }
                    }

                    int two = line1PointNum + line2PointNum;
                    int three = line1PointNum + line2PointNum + line3PointNum;
                    int four = line1PointNum + line2PointNum + line3PointNum + line4PointNum;
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            textView.append("\nLine one\n");
                            textView.append("--------------------\n");
                        }
                        if (i == line1PointNum) {
                            textView.append("\nLine two \n");
                            textView.append("--------------------\n");
                        }
                        if (i == two) {
                            textView.append("\nLine three \n");
                            textView.append("--------------------\n");
                        }
                        if (i == three) {
                            textView.append("\nLine four \n");
                            textView.append("--------------------\n");
                        }
                        if (i == four) {
                            textView.append("\nLine five \n");
                            textView.append("--------------------\n");
                        }

                        //first line
                        if (i <= line1PointNum - 1 || (i <= points.length && i > four)) {
                            if (point[i][0].equals("65535")) {
                                textView.append("---------- miss\n");
                                flag = false;
                                continue;
                            }
                            if (Double.parseDouble(list.get(i) + "") / a > lineation_edge_threshold) {
                                textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                flag = false;
                            } else
                                textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                        }
                        if ((i <= two - 1 && i > line1PointNum)
                                || (i <= three - 1 && i > two)
                                || (i <= four - 1 && i > three)) {
                            if (point[i][0].equals("65535")) {
                                textView.append("---------- miss\n");
                                flag = false;
                                continue;
                            }
                            if (Integer.parseInt(point[i][1]) < test_bar_radius * a
                                    || (Integer.parseInt(point[i][1]) < height && Integer.parseInt(point[i][1]) > height - test_bar_radius * a)) {
                                if (Double.parseDouble(list.get(i) + "") / a > lineation_edge_threshold) {
                                    textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                    flag = false;
                                } else
                                    textView.append("[ edge ](" + points[i] + ")dis:" + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                            } else {
                                if (Double.parseDouble(list.get(i) + "") / a > lineation_center_threshold) {
                                    textView.append("[center](" + points[i] + ")dis: " + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)--fail\n");
                                    flag = false;
                                } else
                                    textView.append("[center](" + points[i] + ")dis: " + list.get(i) + "(pix)/" + df.format(Double.parseDouble(list.get(i)) / a) + "(mm)\n");
                            }
                        }
                    }
                    if (flag) {
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("VER\npass");
                    } else {
                        result.setBackgroundColor(Color.RED);
                        result.setText("VER\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        sen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 4;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String senTime = data.getString("sen", "");
                String senCount = data.getString("senCount", "");
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!senTime.isEmpty()) {
                    String senData = read(senTime, "sen_");
                    if (senData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(senCount) == 1) {
                        textView.setText(senData);
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("SEN\npass");
                    } else {
                        textView.setText("\nbroken line,please test again!\n\n");
                        textView.append(senData);
                        result.setBackgroundColor(Color.RED);
                        result.setText("SEN\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        seg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 5;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String segTime = data.getString("seg", "");
                String segCount = data.getString("segCount", "");
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!segTime.isEmpty()) {
                    String segData = read(segTime, "seg_");
                    if (segData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Integer.parseInt(segCount) == 1) {
                        textView.setText(segData);
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("SEG\npass");
                    } else {
                        textView.setText("\nbroken line,please test again!\n\n");
                        textView.append(segData);
                        result.setBackgroundColor(Color.RED);
                        result.setText("SEG\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });


        mtwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 6;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String mtwoTime = data.getString("mtwo", "");
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!mtwoTime.isEmpty()) {
                    String mtwoData = read(mtwoTime, "two_");
                    if (mtwoData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    String[] dis;
                    String mtwo = "";
                    double max = 0;
                    double min = 0;
                    double avg;
                    double sum = 0;
                    boolean flag = false;
                    dis = mtwoData.split("\n");

                    //get max min avg value
                    for (int i = 0; i < dis.length; i++) {
                        sum = sum + Double.parseDouble(dis[i]);
                        if (i == 0) {
                            max = Double.parseDouble(dis[i]);
                            min = Double.parseDouble(dis[i]);
                        }
                        if (i > 0 && max <= Double.parseDouble(dis[i])) {
                            max = Double.parseDouble(dis[i]);
                        }
                        if (i > 0 && min >= Double.parseDouble(dis[i])) {
                            min = Double.parseDouble(dis[i]);
                        }
                    }
                    avg = sum / dis.length;
                    textView.setText("max: " + max + "(pix) or " + df.format(max / a) + "(mm)\n" + "min: " + min + "(pix) or " + df.format(min / a) + "(mm)\navg: " + df.format(avg) + "(pix) or " + df.format(avg / a) + "(mm)\n-------------\n");

                    //show distance data and judge
                    for (int i = 0; i < dis.length; i++) {
                        if (i == dis.length - 1) {
                            if (Double.parseDouble(dis[i]) < 50) {
                                flag = true;
                            }
                            mtwo = " dis: " + dis[i] + " (pix)  /  " + df.format(Double.parseDouble(dis[i]) / a) + " (mm)";
                            textView.append(mtwo);
                        } else {
                            if (Double.parseDouble(dis[i]) < 50) {
                                flag = true;
                            }
                            mtwo = " dis: " + dis[i] + " (pix)  /  " + df.format(Double.parseDouble(dis[i]) / a) + " (mm)" + "\n";
                            textView.append(mtwo);
                        }
                    }

                    if (flag) {
                        result.setBackgroundColor(Color.GREEN);
                        result.setText("TWO\npass");
                    } else {
                        result.setBackgroundColor(Color.RED);
                        result.setText("TWO\nfail");
                    }
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        meg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 7;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String megTime = data.getString("meg", "");
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!megTime.isEmpty()) {
                    String megData = read(megTime, "meg_");
                    if (megData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    textView.setText(megData);
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 8;
                SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
                String rateTime = data.getString("rate", "");
                String rate = "";
                textView.setText("");
                result.setBackgroundColor(Color.rgb(170, 170, 170));
                result.setText("NaN");
                if (!rateTime.isEmpty()) {
                    String rateData = read(rateTime, "rat_");
                    if (rateData == null) {
                        Toast.makeText(TestResultActivity.this, "data removed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] rateArr = rateData.split("\n");
                    for (int i = 0; i < rateArr.length - 1; i++) {
                        if (i == rateArr.length - 2) {
                            rate = rate + 1000 / (Integer.parseInt(rateArr[i + 1]) - Integer.parseInt(rateArr[i]));
                        }
                        rate = rate + 1000 / (Integer.parseInt(rateArr[i + 1]) - Integer.parseInt(rateArr[i])) + "\n";
                    }
                    textView.setText(rate);
                } else
                    Toast.makeText(TestResultActivity.this, "No Data,Please test first!", Toast.LENGTH_SHORT).show();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            SharedPreferences data = getSharedPreferences("time", Context.MODE_PRIVATE);
            String accTime = data.getString("acc", "");
            String diaTime = data.getString("dia", "");
            String horTime = data.getString("hor", "");
            String verTime = data.getString("ver", "");
            String senTime = data.getString("sen", "");
            String segTime = data.getString("seg", "");
            String mtwoTime = data.getString("mtwo", "");
            String megTime = data.getString("meg", "");
            String rateTime = data.getString("rate", "");

            @Override
            public void onClick(View v) {
                final EditText editor = new EditText(TestResultActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(TestResultActivity.this);
                dialog.setTitle("Please enter name");
                dialog.setIcon(android.R.drawable.ic_dialog_info);
                dialog.setView(editor);
                dialog.setPositiveButton("Define", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reName = editor.getText().toString();
                        String[] time = {accTime, diaTime, horTime, verTime, senTime, segTime, mtwoTime, megTime, rateTime};
                        String[] testName = {"acc", "dia", "hor", "ver", "sen", "seg", "two", "meg", "rat"};
                        for (int i = 0; i < 9; i++) {
                            if (check == i) {
                                RWlog.copyFile(testName[i] + "_", time[i], reName);
                                Toast.makeText(TestResultActivity.this, testName[i] + " named success", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialog.setNegativeButton("Cancel", null);
                dialog.show();
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editor = new EditText(TestResultActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(TestResultActivity.this);
                dialog.setTitle("Save result");
                dialog.setIcon(R.drawable.ic_launcher);
                dialog.setView(editor);
                String[] testName = {"acc", "dia", "hor", "ver", "sen", "seg", "mtwo", "meg", "rate"};
                for (int i = 0; i < 9; i++) {
                    if (check == i)
                        dialog.setMessage("Enter " + testName[i] + " name and save");
                }
                dialog.setPositiveButton("Define", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reName = editor.getText().toString();
                        for (int i = 0; i < 9; i++) {
                            if (check == i) {
                                String testResult = textView.getText().toString();
                                RWlog.saveResult(testResult, reName);
                            }
                        }
                        Toast.makeText(TestResultActivity.this, "save success", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("Cancel", null);
                dialog.show();
            }
        });
    }

    private String read(String name, String title) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String save_path = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + himax_config.mTouchTest_tmp_dir_str
                    + File.separator
                    + title
                    + name
                    + ".txt";
            File file = new File(save_path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            Scanner scan = null;
            StringBuilder sb = new StringBuilder();
            try {
                scan = new Scanner(new FileInputStream(file));
                while (scan.hasNext()) {
                    sb.append(scan.next() + "\n");
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scan != null) {
                    scan.close();
                }
            }
        } else {
            Toast.makeText(this, "Sdcard no exist!\nSave failed!", Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
