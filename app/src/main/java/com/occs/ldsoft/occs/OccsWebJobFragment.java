package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yeliu on 15/7/28.
 */
public class OccsWebJobFragment extends OccsWebContentFragment {

    public static final String TAG = "OccsWebJobFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        titleText.setText("岗位认证");
        weburl = makeUrl(WebLinkStatic.JOBLINK);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
