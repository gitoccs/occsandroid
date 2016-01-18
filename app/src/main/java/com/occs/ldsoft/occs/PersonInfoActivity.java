package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by yeliu on 15/8/30.
 */
public class PersonInfoActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PersonInfoFragment();
    }
}
