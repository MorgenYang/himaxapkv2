package com.ln.himaxtouch.HomeCatalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.himaxtouch.R;

/**
 * Created by 903622 on 2018/6/19.
 */

public class HimaxListAdapter extends ArrayAdapter<String> {

    private String[] mItems;
    private GridViewAdapter.ViewHolder mViewHolder;
    private LayoutInflater mInflater;
    private Context mContext;

    public HimaxListAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
        mItems = objects;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_list_item, null);
            mViewHolder = new GridViewAdapter.ViewHolder();
            mViewHolder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            mViewHolder.tv = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (GridViewAdapter.ViewHolder) convertView.getTag();
        }

        mViewHolder.tv.setText(mItems[position]);
        return convertView;
    }
}
