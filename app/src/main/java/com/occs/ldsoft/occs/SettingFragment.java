package com.occs.ldsoft.occs;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ToggleButton;

/**
 * Created by yeliu on 15/11/16.
 */
public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    ToggleButton toggleButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ImageButton closeBtn = (ImageButton)v.findViewById(R.id.app_setting_closebtn);
//        toggleButton = (ToggleButton)v.findViewById(R.id.slinece_mode_togglebutton);
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().finish();
//            }
//        });
//
//        toggleButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                toggleChecked(toggleButton.isChecked());
//            }
//        });
//
//        checkToggleBtn();
        return v;
    }

    public void toggleChecked(Boolean isChecked){
        if (isChecked){
            Tools.setRingerMode(getActivity().getApplicationContext(),AudioManager.RINGER_MODE_SILENT);
            Log.d(TAG,"AudioManager.RINGER_MODE_SILENT");
        }else{
            Tools.setRingerMode(getActivity().getApplicationContext(),AudioManager.RINGER_MODE_NORMAL);
            Log.d(TAG, "AudioManager.MODE_NORMAL");
        }
    }

    public void checkToggleBtn(){
        toggleButton.setChecked(Tools.isPhoneSilent(getActivity().getApplicationContext()));
    }
}
