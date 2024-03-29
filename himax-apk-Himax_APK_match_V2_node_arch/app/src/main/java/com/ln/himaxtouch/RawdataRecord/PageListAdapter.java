package com.ln.himaxtouch.RawdataRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.himaxtouch.DummyContent;
import com.ln.himaxtouch.R;

/**
 * Created by 902995 on 2018/5/2.
 */

public class PageListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] Ids;
    private final int rowResourceId;

    public PageListAdapter(Context context, int textViewResourceId, String[] objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.Ids = objects;
        this.rowResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(rowResourceId, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);

//		textView.setText(DummyContent.ITEMS.get(position).content);
        textView.setText(this.Ids[position]);
        imageView.setImageResource(DummyContent.ITEMS.get(position).icon);

        return rowView;
    }
}