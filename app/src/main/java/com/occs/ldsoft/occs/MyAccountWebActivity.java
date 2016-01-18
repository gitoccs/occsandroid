package com.occs.ldsoft.occs;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.widget.Toast;

/**
 * Created by yeliu on 15/9/14.
 */
public class MyAccountWebActivity extends SingleFragmentActivity {

    private OccsWebMyAccountFragment myFragment;
    @Override
    protected Fragment createFragment() {
        myFragment = new OccsWebMyAccountFragment();
        return myFragment;
    }

    @Override
    public void onBackPressed() {
        if (myFragment.curPage > 1){
            myFragment.curPage--;
            String functionStr = "javascript:setpage" + myFragment.curPage + "()";
            myFragment.webView.loadUrl(functionStr);
        }else {
            super.onBackPressed();
        }
    }
}
