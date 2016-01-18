package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yeliu on 15/10/8.
 * 关于OCCS的Fragment继承于OccsWebContentFragment
 */
public class AboutFragment extends OccsWebContentFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        ImageView leftHint = (ImageView) v.findViewById(R.id.web_left_handle);
        TextView title = (TextView) v.findViewById(R.id.webview_title_txt);
        //设定title
        title.setText("关于OCCS");
        //设定左边的推拉指引bar
        leftHint.setVisibility(View.INVISIBLE);
        weburl = makeUrl(WebLinkStatic.ABOUTOCCS);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
