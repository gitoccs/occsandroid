package com.occs.ldsoft.occs;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by yeliu on 15/9/11.
 */
public class ChangePasswordNewPassFragment extends Fragment{

    private static final String TAG = "ChangePasswordNewPassFragment";
    private int contentColor;
    private EditText newPass1;
    private EditText newPass2;
    private Button yesBtn;
    private boolean isLoadingWeb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentColor = getArguments().getInt("color", R.color.btn_gray_normal);
    }

    public static ChangePasswordNewPassFragment newInstance(int contentColor) {
        Bundle args = new Bundle();
        args.putInt("color", contentColor);

        ChangePasswordNewPassFragment fragment = new ChangePasswordNewPassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.change_password_newpass_layout, container, false);
        newPass1 = (EditText) v.findViewById(R.id.change_password_newpass1);
        newPass2 = (EditText) v.findViewById(R.id.change_password_newpass2);
        yesBtn = (Button) v.findViewById(R.id.change_password_newpass_yesbtn);
        yesBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"pass1: " + newPass1 + "   pass2: " + newPass2);
                if (newPass1.getText().toString().equals("")){
                    Tools.showToastMid(getActivity(),"密码不能为空");
                    return;
                }
                if (newPass1.getText().toString().equals(newPass2.getText().toString())) {
                    if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())){
                        new FetchChangPassword().execute();
                    }
                } else {
                    Tools.showToastMid(getActivity(),"两次输入的密码不匹配");
                }
            }
        });
        return v;
    }

    private class FetchChangPassword extends AsyncTask<Void, Void, String> {

        private String passString;

        @Override
        protected void onPreExecute() {
            passString = newPass1.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.CHANGEPASSWORD).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("password", passString)
                    .appendQueryParameter("username", Person.getPerson().getName())
                    .build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            Log.i(TAG, xmlString);
            isLoadingWeb = false;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(xmlString));
                parseLogInfo(parser);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void parseLogInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType = parser.next();
            String status = null;
            String msg = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("status")){
                        status = readText(parser);
                        Log.d(TAG,status);
                    };

                    if (parser.getName().equals("msg")){
                        msg = readText(parser);
                        Log.d(TAG,msg);
                    };
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo){
                case 0:
                case 2:
                case 3:
                    Tools.showToastMid(getActivity(),msg);
                    break;
                case 1:
                    Tools.showToastMid(getActivity(),msg);
                    Person p;
                    if (Person.getPerson() != null){
                        p = Person.getPerson();
                    }else{
                        p = Person.getInstance();
                    }
                    p.setName(Person.getPerson().getName());
                    p.setPassword(newPass1.getText().toString());
                    p.setPersonPreference(getActivity().getApplicationContext());

                    getActivity().finish();
                    break;
            }
        }

        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }
    }
}
