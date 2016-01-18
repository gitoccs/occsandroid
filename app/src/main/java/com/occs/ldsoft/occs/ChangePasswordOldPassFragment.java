package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by yeliu on 15/9/10.
 */
public class ChangePasswordOldPassFragment extends Fragment {

    private int contentColor;
    private EditText passEdit;
    private Button yesBtn;
    private boolean isWebloading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentColor = getArguments().getInt("color", R.color.btn_gray_normal);
    }

    public static ChangePasswordOldPassFragment newInstance(int contentColor) {
        Bundle args = new Bundle();
        args.putInt("color", contentColor);

        ChangePasswordOldPassFragment fragment = new ChangePasswordOldPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.change_password_oldpass_layout, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.change_password_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(contentColor));
        TextView nameLabel = (TextView) v.findViewById(R.id.change_password_oldpass_name);
        passEdit = (EditText) v.findViewById(R.id.change_password_oldpass_pass);
        nameLabel.setText(Person.getPerson().getName());
        yesBtn = (Button) v.findViewById(R.id.change_password_oldpass_yesBtn);
        yesBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passEdit.getText().toString().equals(Person.getPerson().getPassword())) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                    ft.replace(R.id.fragmentContainer, ChangePasswordNewPassFragment.newInstance(contentColor));
                    ft.addToBackStack("oldPassword");
                    ft.commit();
                } else {
                    Tools.showToastMid(getActivity(), "密码不正确，请重新输入");
                }
            }
        });
        return v;
    }
}
