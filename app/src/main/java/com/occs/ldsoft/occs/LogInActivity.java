package com.occs.ldsoft.occs;

import android.app.Activity;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;


public class LogInActivity extends SingleFragmentActivity {

    private int returnCount = 0;

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onBackPressed() {
        new CountDownTimer(3000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                returnCount = 0;
            }
        }.start();
        if (returnCount == 0){
            Toast.makeText(this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
            returnCount++;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        hideSoftKeyboard(this);
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
