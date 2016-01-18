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
public class OccsSingleWebPageFragment extends OccsWebContentFragment {

    public static final String TAG = "OccsSingleWebPageFragment";

    public static OccsSingleWebPageFragment newInstance(String _arguments, String title, String baseurl) {
        Bundle args = new Bundle();
        args.putString("arguments", _arguments);
        args.putString("title", title);
        args.putString("baseurl", baseurl);

        OccsSingleWebPageFragment fragment = new OccsSingleWebPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        titleText.setText(this.getArguments().getString("title"));
        weburl = makeUrl(this.getArguments().getString("baseurl")) + this.getArguments().getString("arguments");
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
