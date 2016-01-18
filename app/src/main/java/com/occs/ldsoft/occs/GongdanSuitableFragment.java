package com.occs.ldsoft.occs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import com.occs.ldsoft.occs.TabViewContainerActivity.FetchProjectDetails;

/**
 * Created by yeliu on 15/11/28.
 */
public class GongdanSuitableFragment extends Fragment {
    private static final String TAG = "GongdanSuitableFragment";
    private ListView listView;
    private OccsDatabaseHelper.GongdanCursor mCursor;
    private GongdanCursorAdapter adapter;
    private TextView textLabel;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sys_notification_layout, container, false);
        listView = (ListView)v.findViewById(R.id.notification_list);
        ImageButton closeBtn = (ImageButton)v.findViewById(R.id.sys_note_backbtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        final GongdanSuitableManager manager = GongdanSuitableManager.getInstance();
        mCursor = manager.queryGongdans(getActivity().getApplicationContext());
        adapter = new GongdanCursorAdapter(getActivity(), mCursor, getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        textLabel = (TextView)v.findViewById(R.id.sys_note_label);
        textLabel.setText("没有工单");
        updateLabel();

        IntentFilter receiverFilter = new IntentFilter("com.occs.sysNoteReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCursor = manager.queryGongdans(getActivity().getApplicationContext());
                adapter = new GongdanCursorAdapter(getActivity(), mCursor,getActivity().getApplicationContext());
                listView.setAdapter(adapter);
                updateLabel();
            }
        };
        getActivity().registerReceiver(receiver, receiverFilter);
        return v;
    }

    private void updateLabel(){
        if (adapter.getCount() == 0){
            textLabel.setVisibility(View.VISIBLE);
        }else{
            textLabel.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "listview long click");
                dialog("删除信息", "请确认操作，本操作不可撤销", l);
                Log.d(TAG, "long time item pressed " + i + "    " + l);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GongdanSuitableManager manager = GongdanSuitableManager.getInstance();
                mCursor = manager.queryGongdanAt(getActivity().getApplicationContext(), l);
                if (mCursor != null & mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    try {
                        Gongdan gongdan = mCursor.getGongdan();
                        if (Tools.isNetworkConnected(getActivity().getApplicationContext()) && !FetchProjectDetails.isLoadingWeb){
                            new FetchProjectDetails(gongdan.getWorkid(),false, getActivity()).execute();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        GongdanSuitableManager manager = GongdanSuitableManager.getInstance();
        manager.updateIsSeen(getActivity().getApplicationContext());
        Intent i = new Intent("com.occs.sysNoteReceiver");
        getActivity().sendBroadcast(i);
        mCursor.close();

        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    protected void dialog(String title, String msg, final long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("清空所有", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                GongdanSuitableManager manager = GongdanSuitableManager.getInstance();
                int i = manager.deleteAll(getActivity().getApplicationContext());
                dialog.dismiss();
                mCursor = manager.queryGongdans(getActivity().getApplicationContext());
                adapter = new GongdanCursorAdapter(getActivity(), mCursor, getActivity().getApplicationContext());
                listView.setAdapter(adapter);
                updateLabel();
            }

        });
        builder.setNegativeButton("删除一条", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GongdanSuitableManager manager = GongdanSuitableManager.getInstance();
                long i = manager.deleteGongdanAt(getActivity().getApplicationContext(), l);
                dialog.dismiss();
                mCursor = manager.queryGongdans(getActivity().getApplicationContext());
                adapter = new GongdanCursorAdapter(getActivity(), mCursor, getActivity().getApplicationContext());
                listView.setAdapter(adapter);
                updateLabel();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static class GongdanCursorAdapter extends CursorAdapter {

        private OccsDatabaseHelper.GongdanCursor mGongdanCursor;
        private Context sendContext;

        public GongdanCursorAdapter(Context context, OccsDatabaseHelper.GongdanCursor c, Context _sendContext) {
            super(context, c, 0);
            mGongdanCursor = c;
            sendContext = _sendContext;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.gongdan_suitable_cell_layout, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            try {
                final Gongdan gongdan = mGongdanCursor.getGongdan();
                RelativeLayout layout = (RelativeLayout)view;
                TextView titleView = (TextView)layout.findViewById(R.id.gongdan_title);
                TextView projectView = (TextView)layout.findViewById(R.id.gongdan_project);
                TextView costView = (TextView)layout.findViewById(R.id.gongdan_cost);
                TextView typeView = (TextView)layout.findViewById(R.id.gongdan_type);
                TextView periodView = (TextView)layout.findViewById(R.id.gongdan_period);
                TextView deadlineView = (TextView)layout.findViewById(R.id.gongdan_deadline);
                TextView stutusView = (TextView)layout.findViewById(R.id.gongdan_status);
                ImageView newImageView = (ImageView)layout.findViewById(R.id.sys_note_newimage);
                titleView.setText(gongdan.getTitle());
                projectView.setText(gongdan.getProject());
                costView.setText(gongdan.getCost());
                deadlineView.setText(Tools.longToString(gongdan.getDeadline(),"yyyy年MM月dd日 HH:mm:ss"));
                periodView.setText(gongdan.getPeriod() + "天");
                typeView.setText(gongdan.getType());
                stutusView.setText(gongdan.getStatus());
                if (gongdan.getIs_seen() == 0)
                    newImageView.setVisibility(View.VISIBLE);
                else
                    newImageView.setVisibility(View.INVISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
