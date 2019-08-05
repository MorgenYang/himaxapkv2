package com.ln.himaxtouch.ObjectiveTest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.himaxtouch.R;

import java.util.ArrayList;


/**
 * Created by 903622 on 2018/3/27.
 */

public class ObjectiveListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    public Context mContext;
    private LayoutInflater mVI;
    private ArrayList<String> mGroupData;
    private ArrayList<String[]> mChildData;
    private ObjectiveTestController mController;

    public ObjectiveListAdapter(Context context, Activity activity, ArrayList<String> groupData,
                                ArrayList<String[]> childData, ObjectiveTestController controller) {
        this.mGroupData = groupData;
        this.mContext = context;
        mVI = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupData = groupData;
        mChildData = childData;
        mController = controller;
    }


    public String getChild(int groupPosition, int childPosition) {
        String[] childs = mChildData.get(groupPosition);
        return childs[childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public int getChildrenCount(int groupPosition) {
        String[] childs = mChildData.get(groupPosition);
        return (childs == null) ? 0 : childs.length;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        String child = getChild(groupPosition, childPosition);
        if (child != null) {
            if (v == null) {
                v = mVI.inflate(R.layout.list_item_child, null);
                holder = new ViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.text.setText(mChildData.get(groupPosition)[childPosition]);
            holder.setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mController.openParameterSettingView(mContext, groupPosition, childPosition);
                }
            });

        }
        return v;
    }
    public String getGroup(int groupPosition) {
        return "group-" + groupPosition;
    }
    public int getGroupCount() {
        return mGroupData.size();
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            v = mVI.inflate(R.layout.list_item_group, null);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }
        else {
            holder = (ViewHolder) v.getTag();
        }

        String arrowLeft = "  ▸";
        String arrowDown = "  ▾";

        holder.text.setText(mGroupData.get(groupPosition));
        if(getChildrenCount(groupPosition) != 0) {
            if(isExpanded) {
                holder.text.setText(mGroupData.get(groupPosition) + arrowDown);
            } else {
                holder.text.setText(mGroupData.get(groupPosition) + arrowLeft);
            }
            holder.setting.setVisibility(View.GONE);
        } else {
            holder.setting.setVisibility(View.VISIBLE);
            holder.setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mController.openParameterSettingView(mContext, groupPosition, 0);
                }
            });
        }

        return v;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public boolean hasStableIds() {
        return true;
    }

    class ViewHolder {
        public TextView text;
        public ImageView imageview;
        public ImageButton setting;
        public ViewHolder(View v) {
            this.text = (TextView)v.findViewById(R.id.textView);
            this.imageview = (ImageView)v.findViewById(R.id.imageView);
            this.setting = (ImageButton)v.findViewById(R.id.item_setting);
        }
    }

}
