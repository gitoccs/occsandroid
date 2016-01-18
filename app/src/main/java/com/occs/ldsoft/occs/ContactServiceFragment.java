package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yeliu on 15/10/8.
 */
public class ContactServiceFragment extends OccsWebContentFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        ImageView leftHint = (ImageView) v.findViewById(R.id.web_left_handle);
        TextView title = (TextView) v.findViewById(R.id.webview_title_txt);
        title.setText("联系客服");
        leftHint.setVisibility(View.INVISIBLE);
        weburl = makeUrl(WebLinkStatic.CONTACTSERVICE);
        webView.loadUrl(weburl);
        Log.i(TAG, weburl);
        return v;
    }
}
