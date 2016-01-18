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

import com.occs.ldsoft.occs.OccsDatabaseHelper.NoteCursor;

import java.text.ParseException;

/**
 * Created by yeliu on 15/10/12.
 */
public class SysNotificationFragment extends Fragment {
    private static final String TAG = "SysNotificationFragment";
    private NoteCursor mCursor;
    private BroadcastReceiver receiver;
    private NoteCursorAdapter adapter;
    private ListView listView;
    private TextView textLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sys_notification_layout, container, false);
        listView = (ListView)v.findViewById(R.id.notification_list);
        ImageButton closeBtn = (ImageButton)v.findViewById(R.id.sys_note_backbtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        final PushNotificationManager manager = PushNotificationManager.getInstance();
        mCursor = manager.queryNotes(getActivity().getApplicationContext());
        adapter = new NoteCursorAdapter(getActivity(), mCursor,getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        textLabel = (TextView)v.findViewById(R.id.sys_note_label);
        textLabel.setText("没有通知");
        updateLabel();

        IntentFilter receiverFilter = new IntentFilter("com.occs.sysNoteReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCursor = manager.queryNotes(getActivity().getApplicationContext());
                adapter = new NoteCursorAdapter(getActivity(), mCursor,getActivity().getApplicationContext());
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
                PushNotificationManager manager = PushNotificationManager.getInstance();
                mCursor = manager.queryNoteAt(getActivity().getApplicationContext(), l);
                if(mCursor != null & mCursor.getCount()>0){
                    mCursor.moveToFirst();
                    try {
                        PushNote note = mCursor.getNote();
                        PushRouter.routeNotificationWithNote(getActivity().getApplicationContext(),note);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    protected void dialog(String title, String msg, final long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("清空所有", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                PushNotificationManager manager = PushNotificationManager.getInstance();
                int i = manager.deleteAll(getActivity().getApplicationContext());
                dialog.dismiss();
                mCursor = manager.queryNotes(getActivity().getApplicationContext());
                adapter = new NoteCursorAdapter(getActivity(), mCursor, getActivity().getApplicationContext());
                listView.setAdapter(adapter);
                updateLabel();
            }

        });
        builder.setNegativeButton("删除一条", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PushNotificationManager manager = PushNotificationManager.getInstance();
                long i = manager.deleteNoteAt(getActivity().getApplicationContext(),l);
                dialog.dismiss();
                mCursor = manager.queryNotes(getActivity().getApplicationContext());
                adapter = new NoteCursorAdapter(getActivity(), mCursor, getActivity().getApplicationContext());
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

    @Override
    public void onDestroy() {
        PushNotificationManager manager = PushNotificationManager.getInstance();
        manager.updateIsSeen(getActivity().getApplicationContext());
        mCursor = manager.queryLastNote(getActivity().getApplicationContext());
        Intent i = new Intent("com.occs.sysNoteReceiver");
        getActivity().sendBroadcast(i);
        mCursor.close();

        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private static class NoteCursorAdapter extends CursorAdapter {

        private NoteCursor mNoteCursor;
        private Context sendContext;

        public NoteCursorAdapter(Context context, NoteCursor c, Context _sendContext) {
            super(context, c, 0);
            mNoteCursor = c;
            sendContext = _sendContext;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.sys_notification_cell_layout, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            try {
                final PushNote note = mNoteCursor.getNote();
                RelativeLayout layout = (RelativeLayout)view;
                TextView messageTextView = (TextView)layout.findViewById(R.id.sys_note_message);
                TextView timeTextView = (TextView)layout.findViewById(R.id.sys_note_time);
                ImageView newImageView = (ImageView)layout.findViewById(R.id.sys_note_newimage);
                String cellMessage = context.getString(R.string.cell_message, note.getMessage());
                String cellTime = Tools.longToString(note.getTime(),"yyyy年MM月dd日  HH:mm:ss");
                messageTextView.setText(cellMessage);
                timeTextView.setText(cellTime);
                if (note.getIs_seen() == 0)
                    newImageView.setVisibility(View.VISIBLE);
                else
                    newImageView.setVisibility(View.INVISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
