package com.ln.himaxtouch;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RegisterListDummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<RegisterListDummyItem> ITEMS = new ArrayList<RegisterListDummyItem>();

    static {
        for (int i = 0; i < 128; i++) {
            addItem(new RegisterListDummyItem(String.valueOf(i), "00"));
        }
    }

    private static void addItem(RegisterListDummyItem item) {
        ITEMS.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RegisterListDummyItem {
        public String id;
        public String content;

        public RegisterListDummyItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
