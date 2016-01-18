package com.occs.ldsoft.occs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by yeliu on 15/7/25.
 */
public class LoginFragment extends Fragment {

    Button loginBtn;
    Button registerBtn;
    Button demandBtn;
    ProgressBar progressBar;
    private EditText nameEditTxt;
    private EditText passEditTxt;
    private String username;
    private String password;
    private static final String TAG = "LoginFragment";
    private boolean isLoadingWeb = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DrawLoginBackground back = new DrawLoginBackground(this.getActivity());
        View v = inflater.inflate(R.layout.login_layout, container, false);
        View layout = v.findViewById(R.id.login_fragment_layout);

        loginBtn = (Button) v.findViewById(R.id.login_btn);
        registerBtn = (Button) v.findViewById(R.id.register_btn);
        demandBtn = (Button) v.findViewById(R.id.demand_btn);

        nameEditTxt = (EditText)v.findViewById(R.id.name_input);
        passEditTxt = (EditText)v.findViewById(R.id.pass_input);
        progressBar = (ProgressBar)v.findViewById(R.id.web_progressBarLogin);
        progressBar.setVisibility(View.INVISIBLE);
        TextView findPassTxtView = (TextView)v.findViewById(R.id.find_pass_txt);
        ((RelativeLayout) layout).addView(back, 0);

        Person p = Person.getPersonLogin(getActivity().getApplicationContext());
        nameEditTxt.setText(p.getName());
        passEditTxt.setText(p.getPassword());

//***********************************   button clicked   **************************************

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Yale Debug", "login btn clicked");
                username = String.valueOf(nameEditTxt.getText());
                password = String.valueOf(passEditTxt.getText());
                if(!WebFunctionHelper.FetchLoginTask.isLoadingWeb && Tools.isNetworkConnected(getActivity())){
                    loginBtn.setBackgroundResource(R.drawable.gray_btn_normal);
                    new FetchLoginGo(nameEditTxt.getText().toString(),
                            passEditTxt.getText().toString(), progressBar).execute();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Yale Debug", "register btn clicked");
                Intent i = new Intent(getActivity(), RegisterTypeActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        demandBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Yale Debug", "demand btn clicked");
                Intent i = new Intent(getActivity(), SendDemandActivity.class);
                i.putExtra("isFromMainPage", true);
                startActivity(i);
            }
        });

        findPassTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Yale Debug", "find pass btn clicked");
                Intent i = new Intent(getActivity(), FindPassNameActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

//***********************************   text input changed   ***********************************

        nameEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    private class FetchLoginGo extends WebFunctionHelper.FetchLoginTask {

        public FetchLoginGo(String nameStr, String passStr, ProgressBar progressBar) {
            super(nameStr, passStr, progressBar);
        }

        @Override
        public void successFunction(String msg) {
            WebFunctionHelper.FetchLoginTask.fetchLoginSuccess(getActivity());
        }

        @Override
        public void failFunction(String msg) {
            Tools.showToastMid(getActivity(), msg);
            loginBtn.setBackgroundResource(R.drawable.login_btn_normal);
        }
    }
}
