package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by yeliu on 15/9/8.
 */
public class AccountModifyFragment extends Fragment {

    private Button nameBtn;
    private Button phoneBtn;
    private Button emailBtn;
    private int contentColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentColor = getActivity().getIntent().getIntExtra("color",R.color.btn_gray_normal);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_modify_fragment, container, false);
        nameBtn = (Button) v.findViewById(R.id.account_modify_namebtn);
        phoneBtn = (Button) v.findViewById(R.id.account_modify_phonebtn);
        emailBtn = (Button) v.findViewById(R.id.account_modify_emailbtn);

        nameBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));
        phoneBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));
        emailBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                ft.replace(R.id.fragmentContainer, ChangePasswordOldPassFragment.newInstance(contentColor));
                ft.addToBackStack("modify");
                ft.commit();
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                ft.replace(R.id.fragmentContainer, ChangePhoneCodeFragment.newInstance(contentColor));
                ft.addToBackStack("modify");
                ft.commit();
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                if (!Person.getPerson().getEmail().equals(""))
                    ft.replace(R.id.fragmentContainer, ChangeEmailOldFragment.newInstance(contentColor));
                else
                    ft.replace(R.id.fragmentContainer, ChangeEmailNewFragment.newInstance(contentColor,false));
                ft.addToBackStack("modify");
                ft.commit();
            }
        });

        return v;
    }
}
