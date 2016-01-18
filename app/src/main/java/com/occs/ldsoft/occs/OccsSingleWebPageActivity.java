package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/11/20.
 */
public class OccsSingleWebPageActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        String args = getIntent().getExtras().getString("arguments");
        String title = getIntent().getExtras().getString("title");
        String baseurl = getIntent().getExtras().getString("baseurl");
        return OccsSingleWebPageFragment.newInstance(args, title, baseurl);
    }

}
