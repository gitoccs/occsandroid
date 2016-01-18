package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;

/**
 * Created by yeliu on 15/10/8.
 */
public class ContactServiceActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ContactServiceFragment();
    }
}
