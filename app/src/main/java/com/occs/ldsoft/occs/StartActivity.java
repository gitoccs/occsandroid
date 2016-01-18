package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/8/4.
 */
public class StartActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        //开始的入口测试
        return new StartFragment();
    }



}
