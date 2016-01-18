package com.occs.ldsoft.occs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by yeliu on 15/7/31.
 */
public class FindPassNewPassFragment extends Fragment {

    private static final String TAG = "FindPassNewPassFragment";
    EditText pass1;
    EditText pass2;
    Button submitBtn;
    String username;

    private boolean isLoadingWeb = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_pass_newpass, container, false);
        pass1 = (EditText) v.findViewById(R.id.find_pass_newpass1);
        pass2 = (EditText) v.findViewById(R.id.find_pass_newpass2);
        submitBtn = (Button) v.findViewById(R.id.find_pass_newpass_btn);

        username = getActivity().getIntent().getStringExtra("name");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1 = pass1.getText().toString();
                String str2 = pass2.getText().toString();
                if (str1 != "" && str1.equals(str2)){
                    if(!isLoadingWeb && Tools.isNetworkConnected(getActivity())){
                        new FetchChangPassword().execute();
                    }
                }
            }
        });

        return v;
    }

    private class FetchChangPassword extends AsyncTask<Void, Void, String> {
        private String passString;

        @Override
        protected void onPreExecute() {
            passString = pass1.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.CHANGEPASSWORD).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("password", passString)
                    .appendQueryParameter("username", username)
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
                    p.setName(username);
                    p.setPassword(pass1.getText().toString());
                    p.setPersonPreference(getActivity().getApplicationContext());

                    Intent i = new Intent(getActivity(), LogInActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
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
