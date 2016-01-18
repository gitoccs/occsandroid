package com.occs.ldsoft.occs;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yeliu on 15/7/28.
 */
public class OccsWebMyAccountFragment extends OccsWebContentFragment {

    public static final String TAG = "OccsWebMyAccountFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        ImageView leftHint = (ImageView) v.findViewById(R.id.web_left_handle);
        TextView title = (TextView) v.findViewById(R.id.webview_title_txt);
        title.setText("我的账户");
        leftHint.setVisibility(View.INVISIBLE);
        weburl = makeUrl(WebLinkStatic.MYACCOUNT);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
