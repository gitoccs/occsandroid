package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/8/12.
 */
public class LogoutActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LogoutFragment();
    }
}
