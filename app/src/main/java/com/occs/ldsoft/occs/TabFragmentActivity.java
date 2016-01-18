package com.occs.ldsoft.occs;

import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by yeliu on 15/8/4.
 */
public abstract class TabFragmentActivity extends FragmentActivity {
    protected abstract void backBtnAction();
    protected int returnCount;


    @Override
    public void onBackPressed() {
        backBtnAction();
    }

}
