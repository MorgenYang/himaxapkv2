package com.ln.himaxtouch.HomeCatalog;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.himaxtouch.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 903622 on 2018/6/6.
 */

public class GridViewAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    List<Map<String, Object>> mItemList;
    private ViewHolder mViewHolder;
    private int mDisableItmeStart;
    public GridViewAdapter(Context context, List<Map<String, Object>> itemList, int disableItemStart)
    {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemList = itemList;
        mDisableItmeStart = disableItemStart;
    }

    @Override
    public int getCount()
    {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_view_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.iv = (ImageView) convertView.findViewById(R.id.imgView);
            mViewHolder.tv = (TextView) convertView.findViewById(R.id.txtView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.iv.setImageResource(Integer.valueOf(mItemList.get(position).get("Item icon").toString()));
        mViewHolder.tv.setText(mItemList.get(position).get("Item title").toString());

        if(position > mDisableItmeStart) {
            mViewHolder.iv.setEnabled(false);
            mViewHolder.iv.setColorFilter(Color.parseColor("#BBBBBB"));
            mViewHolder.tv.setEnabled(false);
        } else {
            mViewHolder.iv.setEnabled(true);
            mViewHolder.iv.setColorFilter(null);
            mViewHolder.tv.setEnabled(true);
        }

        return convertView;
    }


    static class ViewHolder
    {
        TextView tv;
        ImageView iv;
    }
}
