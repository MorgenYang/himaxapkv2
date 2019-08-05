package com.ln.himaxtouch.ObjectiveTest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ln.himaxtouch.HimaxApplication;
import com.ln.himaxtouch.R;

import java.util.ArrayList;

import static com.ln.himaxtouch.HimaxApplication.mICData;
import static com.ln.himaxtouch.HimaxApplication.mNodeAcc;
import static com.ln.himaxtouch.ObjectiveTest.ObjectiveListId.OBJECTIVE_TEST_PATTERN_TRACK;


/**
 * Created by 903622 on 2018/3/27.
 */

public class ObjectiveTestActivity extends Activity implements PopupWindow.OnDismissListener {

    private static final boolean DEBUG = false;
    private static final String TAG = "[HXTP]ObjTestActivity";
    private HimaxApplication mApplication;
    private ObjectiveTestController mController;

    private ExpandableListView mExpandableListView;
    private ExpandableListView.OnGroupClickListener mGroupClickListener;
    private ExpandableListView.OnChildClickListener mChildClickListener;
    private ObjectiveListAdapter mObjectiveListAdapter;
    private ArrayList<String> mGroupData = new ArrayList<String>();
    private ArrayList<String[]> mChildData = new ArrayList<String[]>();
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_objectivetest);

        initItemData();

        if (mApplication == null) {
            mApplication = (HimaxApplication) getApplication();
        }
        if (mController == null) {
            mController = mApplication.getObjectiveTestController();
        }
        mContext = this;
        createListViewComponent();
        mExpandableListView = (ExpandableListView)findViewById(R.id.objective_expandable_list_view);
        mExpandableListView.setGroupIndicator(null);
        mController.onCreateMainListView(mExpandableListView, mObjectiveListAdapter, mGroupClickListener, mChildClickListener);

        if (mICData.val_icid == 0) {
            Log.d(TAG,"Now is " + TAG);
            mICData.readICIDByNode();
            mICData.matchICIDStr2Int();
            mICData.reInitByDiffIC(Long.valueOf(mICData.val_icid));
        }


    }

    private void initItemData() {
        mGroupData.clear();
        mChildData.clear();

        /**
            * Group Area
            */
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_SNR, getResources().getString(R.string.objective_snr_measurement));
        mGroupData.add(OBJECTIVE_TEST_PATTERN_TRACK, getResources().getString(R.string.objective_pattern_track));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST, getResources().getString(R.string.objective_jitter_test));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD, getResources().getString(R.string.objective_record_max_point_count));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD, getResources().getString(R.string.objective_ghost_point_record));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_PALM_TEST, getResources().getString(R.string.objective_palm_test));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION, getResources().getString(R.string.objective_board_protection));
        mGroupData.add(ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD, getResources().getString(R.string.objective_extend_board));

        /**
             * Child Area
             */
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_SNR, null);
        String[] pattern_child = {getResources().getString(R.string.objective_pattern_1), getResources().getString(R.string.objective_pattern_2),
                getResources().getString(R.string.objective_pattern_3), getResources().getString(R.string.objective_pattern_4),
                getResources().getString(R.string.objective_pattern_5), getResources().getString(R.string.objective_pattern_6)};
        mChildData.add(OBJECTIVE_TEST_PATTERN_TRACK, pattern_child);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_JITTER_TEST, null);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_MAX_POINT_RECORD, null);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_GHOST_POINT_RECORD, null);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_PALM_TEST, null);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_BORAD_PROTECTION, null);
        mChildData.add(ObjectiveListId.OBJECTIVE_TEST_EXTEND_BORARD, null);
    }

    private void createListViewComponent() {
        mObjectiveListAdapter = new ObjectiveListAdapter(this, this, mGroupData, mChildData, mController);
        mGroupClickListener = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if(i != OBJECTIVE_TEST_PATTERN_TRACK) {
                    mController.onItemClick(mContext, i, -1);
                }
                return false;
            }
        };
        mChildClickListener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                mController.onItemClick(mContext, i, i1);
                return false;
            }
        };
    }

    @Override
    public void onDismiss() {
        Toast.makeText(this,"Gone！",Toast.LENGTH_SHORT).show();

    }
}
