package com.ln.himaxtouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		/*addItem(new DummyItem("1", R.drawable.ic_launcher, "Touch Monitor"));
		//addItem(new DummyItem("2", R.drawable.ic_launcher, "Touch Point"));
		addItem(new DummyItem("2", R.drawable.ic_launcher, "Linearity"));
		addItem(new DummyItem("3", R.drawable.ic_launcher, "Motion Events"));
		addItem(new DummyItem("4", R.drawable.ic_launcher, "Read Motion Event"));
		addItem(new DummyItem("5", R.drawable.ic_launcher, "Device Information"));

		addItem(new DummyItem("6", R.drawable.ic_launcher, "Background White"));
		addItem(new DummyItem("7", R.drawable.ic_launcher, "Background Gray"));
		addItem(new DummyItem("8", R.drawable.ic_launcher, "Background Black"));
		addItem(new DummyItem("9", R.drawable.ic_launcher, "Setup"));
		addItem(new DummyItem("10", R.drawable.ic_launcher, "Upgrde FW"));
		addItem(new DummyItem("11", R.drawable.ic_launcher, "OSC Hopping"));
		addItem(new DummyItem("12", R.drawable.ic_launcher, "IIR Self Test"));
		addItem(new DummyItem("13", R.drawable.ic_launcher, "Self Test"));
		addItem(new DummyItem("14", R.drawable.ic_launcher, "Re-Sense"));
		addItem(new DummyItem("15", R.drawable.ic_launcher, "Register R/W"));
		addItem(new DummyItem("16", R.drawable.ic_launcher, "Multi Register R/W"));*/
		for(int i =0;i<himax_config.list_item.length;i++)
			addItem(new DummyItem(Integer.toString((i+1)), R.drawable.ic_launcher, himax_config.list_item[i]));


	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public int icon;
		public String content;

		public DummyItem(String id, int icon, String content) {
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
