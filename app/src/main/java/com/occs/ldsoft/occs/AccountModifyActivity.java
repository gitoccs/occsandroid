package com.occs.ldsoft.occs;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import static com.occs.ldsoft.occs.LogInActivity.hideSoftKeyboard;

/**
 * Created by yeliu on 15/9/8.
 * 账户管理的Activity
 */
public class AccountModifyActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AccountModifyFragment();
    }
//点击背景隐藏键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard(this);
        return false;
    }
}
