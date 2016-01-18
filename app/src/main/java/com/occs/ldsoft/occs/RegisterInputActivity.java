package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import static com.occs.ldsoft.occs.LogInActivity.hideSoftKeyboard;

/**
 * Created by yeliu on 15/7/25.
 */
public class RegisterInputActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RegisterInputFragment();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        hideSoftKeyboard(this);

        return false;
    }
}
