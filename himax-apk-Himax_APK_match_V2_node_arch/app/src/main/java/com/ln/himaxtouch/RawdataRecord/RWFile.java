package com.ln.himaxtouch.RawdataRecord;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by eiNim on 2017/10/26.
 */

public class RWFile {
    public void write_line(String file_path, String data) {
        try {
            FileWriter fw = new FileWriter(file_path, false);
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
            bw.write(data);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            Log.e("HXTP", e.toString());
        }
    }

    public void write_doc(String file_path, String data[]) {
        for (int i = 0; i < data.length; i++)
            this.write_line(file_path, data[i]);
    }

    public String read_doc(String filename) {
        String result = null;
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard, filename);

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                result += (line + "\n");
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }
        return text.toString();
    }
}
