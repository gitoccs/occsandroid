package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.occs.ldsoft.occs.SingleFragmentActivity;

/**
 * Created by Yale on 2015/9/5.
 */
public class PersonCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PersonCameraFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }
}
