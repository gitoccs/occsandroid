package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/10/8.
 */
public class HelperActivity extends SingleFragmentActivity {

    private HelperFragment myFragment;
    @Override
    protected Fragment createFragment() {
        myFragment = new HelperFragment();
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
