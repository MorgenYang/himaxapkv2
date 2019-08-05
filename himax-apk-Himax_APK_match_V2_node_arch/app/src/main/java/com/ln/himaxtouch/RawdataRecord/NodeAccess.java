package com.ln.himaxtouch.RawdataRecord;

import java.util.Objects;

/**
 * Created by eiNim on 2017/10/24.
 */

public class NodeAccess {
    static {
        System.loadLibrary("node_access");
    }

    public native String ReadNode(String[] stringArray);

    public native String WriteNode(String[] stringArray);

    String retur_str() {
        String aaa[] = {"/proc/android_touch/vendor", "v"};
        return ReadNode(aaa);
    }

    public String WriteCMD(String cmd[]) {
        String temp = null;
        String result = null;
        String t_str[] = new String[3];
        int retry = 3;
        for (int i = 0; i < 3; i++) {
            do {
                temp = WriteNode(cmd);
                if (temp.indexOf("fail") < 0 && temp != null && !temp.isEmpty())
                    break;

            } while (retry-- >= 0);

            t_str[i] = temp;
        }

        if (Objects.equals(t_str[0], t_str[1]))
            result = t_str[0];
        else if (Objects.equals(t_str[1], t_str[2]))
            result = t_str[1];
        else
            result = t_str[2];
        return result;
    }

    public String ReadCMD(String cmd[]) {
        String temp = null;
        String result = null;
        String t_str[] = new String[3];
        int retry = 3;
        for (int i = 0; i < 3; i++) {
            do {
                temp = ReadNode(cmd);
                if (temp.indexOf("fail") < 0 && temp != null && !temp.isEmpty())
                    break;
            } while (retry-- >= 0);
            t_str[i] = temp;
        }

        if (Objects.equals(t_str[0], t_str[1]))
            result = t_str[0];
        else if (Objects.equals(t_str[1], t_str[2]))
            result = t_str[1];
        else
            result = t_str[2];
        return result;
    }
}
