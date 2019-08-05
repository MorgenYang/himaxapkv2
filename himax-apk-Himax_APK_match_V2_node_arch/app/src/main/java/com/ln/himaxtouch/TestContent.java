package com.ln.himaxtouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 610285 on 2017/2/27.
 */


public class TestContent {
    public static List<TestItem> TESTITEMS = new ArrayList<TestItem>();

    public static Map<String, TestItem> TESTITEM_MAP = new HashMap<String, TestItem>();

    static {
        //touch test
        addTestItem(new TestItem("1", R.drawable.ic_launcher, "Accuracy"));
        addTestItem(new TestItem("2", R.drawable.ic_launcher, "Diagonal"));
        addTestItem(new TestItem("3", R.drawable.ic_launcher, "Horizontal"));
        addTestItem(new TestItem("4", R.drawable.ic_launcher, "Vertical"));
        addTestItem(new TestItem("5", R.drawable.ic_launcher, "Sensitivity"));
        addTestItem(new TestItem("6", R.drawable.ic_launcher, "Sensitivity Edge"));
        addTestItem(new TestItem("7", R.drawable.ic_launcher, "Manual Two point"));
        addTestItem(new TestItem("8", R.drawable.ic_launcher, "Manual Edge"));
        addTestItem(new TestItem("9", R.drawable.ic_launcher, "Manual Event Rate"));
    }

    private static void addTestItem(TestItem item) {
        TESTITEMS.add(item);
        TESTITEM_MAP.put(item.id, item);
    }

    public static class TestItem {
        public String id;
        public int icon;
        public String content;

        public TestItem(String id, int icon, String content) {
            this.id = id;
            this.icon = icon;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
