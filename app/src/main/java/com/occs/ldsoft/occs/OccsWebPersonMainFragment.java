package com.occs.ldsoft.occs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import com.occs.ldsoft.occs.OccsDatabaseHelper.NoteCursor;
import com.occs.ldsoft.occs.OccsDatabaseHelper.GongdanCursor;

/**
 * Created by yeliu on 15/9/21.
 */
public class OccsWebPersonMainFragment extends OccsWebContentFragment {
    public static final String TAG = "OccsWebPersonMainFragment";
    private BroadcastReceiver receiver;
    private String finalUrl = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter receiverFilter = new IntentFilter("com.occs.sysNoteReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (webView != null && finalUrl != null){
                    String url = setupFinalUrl();
                    webView.loadUrl(url);
                }
            }
        };
        getActivity().registerReceiver(receiver, receiverFilter);
    }

    private String setupFinalUrl(){
        String gd_title = "";
        String gd_date = "";
        int gd_unseen = 0;
        long gd_count = 0;

        String note_msg = "";
        String note_time = "";
        int note_unseen = 0;
        long note_count = 0;

        GongdanSuitableManager gdManger = GongdanSuitableManager.getInstance();
        GongdanCursor gongdanLast = gdManger.queryLastGongdan(getActivity().getApplicationContext());
        if (gongdanLast.getCount() > 0){
            gongdanLast.moveToFirst();
            Gongdan gongdan = null;
            try {
                gongdan = gongdanLast.getGongdan();
                gd_title = gongdan.getTitle();
                gd_date = Tools.longToString(gongdan.getTime(), "yyyy年MM月dd日 HH:mm");
                gd_unseen = gdManger.getNoSeenCount(getActivity().getApplicationContext());
                gd_count = gdManger.getAllcount(getActivity().getApplicationContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        PushNotificationManager noteManager = PushNotificationManager.getInstance();
        NoteCursor noteLast = noteManager.queryLastNote(getActivity().getApplicationContext());
        if (noteLast.getCount() > 0){
            noteLast.moveToFirst();
            PushNote note = null;
            try {
                note = noteLast.getNote();
                note_msg = note.getMessage();
                note_time = Tools.longToString(note.getTime(), "yyyy年MM月dd日 HH:mm");
                note_unseen = noteManager.getNoSeenCount(getActivity().getApplicationContext());
                note_count = noteManager.getAllcount(getActivity().getApplicationContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        try {
            String url = makeFinalUrl(note_msg, note_time, note_unseen, note_count,
                    gd_title, gd_date, gd_unseen, gd_count);
            Log.d(TAG, url);
            return url;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        TextView title = (TextView) v.findViewById(R.id.webview_title_txt);
        title.setText("我的首页");
        finalUrl = makeUrl(WebLinkStatic.PERSONMAIN);

        Intent i = new Intent("com.occs.sysNoteReceiver");
        getActivity().sendBroadcast(i);
        return v;
    }

    private String makeFinalUrl(String note_msg, String note_date, int note_unseen, long note_count,
                                String gd_title, String gd_date, int gd_unseen, long gd_count) throws ParseException {
        String url = Uri.parse(finalUrl).buildUpon()
                .appendQueryParameter("note_msg", note_msg)
                .appendQueryParameter("note_date",note_date)
                .appendQueryParameter("note_unseen", String.valueOf(note_unseen))
                .appendQueryParameter("note_count", String.valueOf(note_count))
                .appendQueryParameter("gd_title", gd_title)
                .appendQueryParameter("gd_date",gd_date)
                .appendQueryParameter("gd_unseen", String.valueOf(gd_unseen))
                .appendQueryParameter("gd_count", String.valueOf(gd_count))
                .build().toString();
        return url;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }
}
