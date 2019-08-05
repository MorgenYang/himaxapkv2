package com.ln.himaxtouch.TouchTest;

import android.os.Environment;

import com.ln.himaxtouch.himax_config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by 610285 on 2017/3/20.
 */

public class RWlog {

    public static void saveResult(String content, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String save_path = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + himax_config.mTouchTest_out_dir_str
                    + File.separator
                    + name
                    + ".txt";
            File file = new File(save_path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            PrintStream out = null;
            try {
                out = new PrintStream(new FileOutputStream(file, true));
                out.println(content);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    public static void write(String content, String title, String time) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String save_path = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + himax_config.mTouchTest_tmp_dir_str
                    + File.separator
                    + title
                    + time
                    + ".txt";
            File file = new File(save_path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            PrintStream out = null;
            try {
                out = new PrintStream(new FileOutputStream(file, true));
                out.println(content);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    public static void copyFile(String title, String time, String newName) {
        try {
            String save_path = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + himax_config.mTouchTest_tmp_dir_str
                    + File.separator
                    + title
                    + time
                    + ".txt";
            File oldFile = new File(save_path);
            String oldAbsolutePath = oldFile.getAbsolutePath();
            String oldRoot = oldAbsolutePath.substring(0, oldAbsolutePath.length() - 29);
            String newPath = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + himax_config.mTouchTest_out_dir_str
                    + File.separator
                    + newName
                    + ".txt";
            int s = 0;
            int r;
            if (oldFile.exists()) {
                InputStream inputStream = new FileInputStream(oldAbsolutePath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((r = inputStream.read(buffer)) != -1) {
                    s += r;
                    System.out.println(s);
                    fs.write(buffer, 0, r);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delete(String name) {
        File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HimaxAPK/" + name);
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        file.delete();
        return true;
    }

//    public static String read(String name, String title) {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            File file = new File(Environment.getExternalStorageDirectory()
//                    .toString()
//                    + File.separator
//                    + "sdcard"
//                    + File.separator
//                    + "tmp"
//                    + File.separator
//                    + title
//                    + name
//                    + ".txt");
//            if (!file.getParentFile().exists()) {
//                file.getParentFile().mkdirs();
//            }
//            Scanner scan = null;
//            StringBuilder sb = new StringBuilder();
//            try {
//                scan = new Scanner(new FileInputStream(file));
//                while (scan.hasNext()) {
//                    sb.append(scan.next() + "\n");
//                }
//                return sb.toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (scan != null) {
//                    scan.close();
//                }
//            }
//        }
//        return null;
//    }
}
