package com.occs.ldsoft.occs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by yeliu on 15/7/28.
 */
public class OccsWebGongdanFragment extends OccsWebContentFragment {

    public static final String TAG = "OccsWebGongdanFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        Intent i = getActivity().getIntent();
        if(i.getBooleanExtra("hideLeftPan", false)){
            ImageView leftHint = (ImageView) v.findViewById(R.id.web_left_handle);
            leftHint.setVisibility(View.INVISIBLE);
        }
        weburl = makeUrl(WebLinkStatic.GONGDANLINK);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        titleText.setText("适合我的工单");
        return v;
    }
}
