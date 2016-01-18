package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/9/8.
 */
public class PhotoCroperActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new PhotoCroperFragment();
    }
}
