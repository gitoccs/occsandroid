package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/11/27.
 */
public class GongdanSingleWebActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        String args = getIntent().getStringExtra("urlArguments");
        return OccsSingleWebPageFragment.newInstance(args, "适合的工单", WebLinkStatic.SINGLESUITABLEGONGDAN);
    }
}
