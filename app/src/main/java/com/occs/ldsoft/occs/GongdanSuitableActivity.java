package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/11/28.
 */
public class GongdanSuitableActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GongdanSuitableFragment();
    }
}
