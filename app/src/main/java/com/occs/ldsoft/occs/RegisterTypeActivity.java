package com.occs.ldsoft.occs;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

/**
 * Created by yeliu on 15/7/25.
 */
public class RegisterTypeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RegisterTypeFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
