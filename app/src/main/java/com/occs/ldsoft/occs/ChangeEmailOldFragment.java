package com.occs.ldsoft.occs;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
 * Created by yeliu on 15/9/10.
 */
public class ChangeEmailOldFragment extends Fragment {

    private static final String TAG = "ChangeEmailOldFragment";
    private int contentColor;
    private boolean isLoadingWeb;
    private EditText codeNumber;
    private TextView emailTxt;
    private Button yesBtn;
    private Button codeBtn;
    private boolean isValidating;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentColor = getArguments().getInt("color",R.color.btn_gray_normal);
    }

    public static ChangeEmailOldFragment newInstance(int contentColor) {
        Bundle args = new Bundle();
        args.putInt("color", contentColor);

        ChangeEmailOldFragment fragment = new ChangeEmailOldFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.change_email_old_layout, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.change_email_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(contentColor));
        emailTxt = (TextView) v.findViewById(R.id.change_email_old_address);
        emailTxt.setText(Person.getPerson().getEmail());

        codeNumber = (EditText) v.findViewById(R.id.change_email_old_code_edit);
        codeBtn = (Button) v.findViewById(R.id.change_email_old_code_btn);
        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    codeBtn.setBackgroundResource(R.drawable.gray_btn_normal);
                    new FetchEmailCodeTask().execute();
                }
            }
        });

        yesBtn = (Button) v.findViewById(R.id.change_email_old_code_yesBtn);
        yesBtn.setBackgroundResource(Tools.colorToBtnStyle(contentColor));
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    new FetchEmailValidateTask().execute();
                }
            }
        });

        return v;
    }

    private class FetchEmailCodeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + Person.getPerson().getMobile() + "");
            String url = Uri.parse(WebLinkStatic.SENDEMAILCODE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("username", "")
                    .appendQueryParameter("email", Person.getPerson().getEmail()).build().toString();
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
                    codeBtn.setBackgroundResource(R.drawable.demand_btn_normal);
                    break;
                case 1:
                    if (!isValidating){
                        isValidating = true;
                        new CountDownTimer(60000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

                            public void onTick(long millisUntilFinished) {
                                codeBtn.setText(millisUntilFinished / 1000 + "秒后获取");
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                codeBtn.setBackgroundResource(R.drawable.demand_btn_normal);
                                codeBtn.setText("获取验证码");
                                isValidating = false;
                            }
                        }.start();
                    }
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

    private class FetchEmailValidateTask extends AsyncTask<Void, Void, String> {
        private String codeString;

        @Override
        protected void onPreExecute() {
            codeNumber.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.CODEVALIDATE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("phoneOrEmail", Person.getPerson().getEmail())
                    .appendQueryParameter("code", codeString)
                    .appendQueryParameter("userid", Person.getPerson().getName()).build().toString();
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
                case 1:
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
                    ft.replace(R.id.fragmentContainer, ChangeEmailNewFragment.newInstance(contentColor, true));
                    ft.addToBackStack("phonecode");
                    ft.commit();
                    break;
                default:
                    Tools.showToastMid(getActivity(), msg);
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
