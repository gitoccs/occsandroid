package com.occs.ldsoft.occs;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by yeliu on 15/7/31.
 */
public class FindPassCodeFragment extends Fragment {

    private static final String TAG = "FindPassCodeFragment";
    private Button nextBtn;
    private Button findCodeBtn;
    private EditText codeEditText;
    private TextView titleTextView;
    private String phone;
    private boolean isLoadingWeb = false;
    private boolean isValidating = false;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_pass_code, container, false);
        nextBtn = (Button) v.findViewById(R.id.find_pass_code_btn);
        codeEditText = (EditText) v.findViewById(R.id.find_pass_code_edittext);
        titleTextView = (TextView) v.findViewById(R.id.find_pass_code_title);
        findCodeBtn = (Button) v.findViewById(R.id.find_pass_code_getcodebtn);

        phone = getActivity().getIntent().getStringExtra("phone");
        username = getActivity().getIntent().getStringExtra("name");
        Log.i(TAG, "username :" + username);

        String phoneTrim = null;
        if (phone.length() >= 4){
            phoneTrim = phone.substring(phone.length()-4, phone.length());
        }
        titleTextView.setText("你注册的手机尾号为" + phoneTrim);

        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    new FetchValidateTask().execute();
                }
            }
        });

        findCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    findCodeBtn.setBackgroundResource(R.drawable.gray_btn_normal);
                    new FetchCodeTask().execute();
                }
            }
        });

        return v;
    }

    private class FetchCodeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + phone + "");
            String url = Uri.parse(WebLinkStatic.GETPHONECODE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("userid", "")
                    .appendQueryParameter("phone", phone).build().toString();
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
                    findCodeBtn.setBackgroundResource(R.drawable.demand_btn_normal);
                    break;
                case 1:
                    if (!isValidating){
                        isValidating = true;
                        new CountDownTimer(60000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

                            public void onTick(long millisUntilFinished) {
                                findCodeBtn.setText(millisUntilFinished / 1000 + "秒后获取");
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                findCodeBtn.setBackgroundResource(R.drawable.demand_btn_normal);
                                findCodeBtn.setText("获取验证码");
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

    private class FetchValidateTask extends AsyncTask<Void, Void, String> {
        private String codeString;

        @Override
        protected void onPreExecute() {
            codeString = codeEditText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + phone + "");
            String url = Uri.parse(WebLinkStatic.CODEVALIDATE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("phoneOrEmail", phone)
                    .appendQueryParameter("code", codeString)
                    .appendQueryParameter("userid", username).build().toString();
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
                    Intent i = new Intent(getActivity(), FindPassNewPassActivity.class);
                    i.putExtra("name",username);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
