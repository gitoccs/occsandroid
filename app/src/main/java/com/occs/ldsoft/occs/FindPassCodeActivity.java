package com.occs.ldsoft.occs;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import static com.occs.ldsoft.occs.LogInActivity.hideSoftKeyboard;

/**
 * Created by yeliu on 15/7/31.
 */
public class FindPassCodeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FindPassCodeFragment();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard(this);
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
