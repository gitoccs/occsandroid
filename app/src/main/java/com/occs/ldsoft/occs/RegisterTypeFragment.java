package com.occs.ldsoft.occs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by yeliu on 15/7/25.
 */
public class RegisterTypeFragment extends Fragment {

    public Button comRegisterBtn;
    public Button softRegisterBtn;
    public Button personRegisterBtn;
    public int typeNumber;

    private static final String TAG = "RegisterTypeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_type_fragment, container, false);
        personRegisterBtn = (Button) v.findViewById(R.id.register_person_btn);
        comRegisterBtn = (Button) v.findViewById(R.id.register_com_btn);
        softRegisterBtn = (Button) v.findViewById(R.id.register_soft_btn);
        comRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                typeNumber = 9;
                updateTypeNumber(typeNumber);
                goNextPage();
            }
        });

        softRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                typeNumber = 91;
                updateTypeNumber(typeNumber);
                goNextPage();
            }
        });

        personRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                typeNumber = 1;
                updateTypeNumber(typeNumber);
                goNextPage();
            }
        });
        return v;
    }

    private void updateTypeNumber(int num){
        Person p = Person.getPerson();
        p.setTypeNumber(num);
        p.setPersonPreference(getActivity().getApplicationContext());
    }

    private void goNextPage() {
        Intent i = new Intent(getActivity(), RegisterInputActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
