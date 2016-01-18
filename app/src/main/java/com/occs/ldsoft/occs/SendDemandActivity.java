package com.occs.ldsoft.occs;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.widget.Toast;

/**
 * Created by yeliu on 15/7/31.
 */
public class SendDemandActivity extends SingleFragmentActivity{

    private OccsWebSendDemandFragment fragment;
    private int returnCount;

    @Override
    protected Fragment createFragment() {
        fragment = new OccsWebSendDemandFragment();
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (fragment.curPage > 1){
            fragment.curPage--;
            String functionStr = "javascript:setpage" + fragment.curPage + "()";
            fragment.webView.loadUrl(functionStr);
        }else {
            finish();
        }
    }
}
