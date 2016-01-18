package com.occs.ldsoft.occs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by yeliu on 15/7/28.
 */
public class OccsWebSendDemandFragment extends OccsWebContentFragment {

    public static final String TAG = "OccsWebSendDemandFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (isFromMainPage){
            titleText.setText("企业用户--发布需求");
        }else{
            titleText.setText("发布需求");
        }
        weburl = makeUrl(WebLinkStatic.SENDDEMANDLINK);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
