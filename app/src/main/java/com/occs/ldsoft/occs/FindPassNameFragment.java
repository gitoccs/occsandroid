package com.occs.ldsoft.occs;

import android.content.Intent;
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
public class FindPassNameFragment extends Fragment {

    private Button nextBtn;
    private EditText nameEditText;
    private boolean isLoadingWeb = false;

    private static final String TAG = "FindPassNameFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_pass_name, container, false);
        nextBtn = (Button) v.findViewById(R.id.find_pass_name_btn);
        nameEditText = (EditText) v.findViewById(R.id.find_pass_name_edittext);

        nextBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())){
                    if (!nameEditText.getText().toString().equals("")){
                        new FetchPhoneNumber().execute();
                    }else{
                        Tools.showToastMid(getActivity(),"请填写用户名");
                    }
                }
            }
        });

        return v;
    }

    private class FetchPhoneNumber extends AsyncTask<Void, Void, String> {
        private String nameText;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nameText = nameEditText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.FINDPHONE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("username", nameText)
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
                    Intent i = new Intent(getActivity(), FindPassCodeActivity.class);
                    String[] phoneStr = msg.split("\\：");
                    Log.d(TAG, phoneStr[1]);
                    if (phoneStr[1].equals("0") || phoneStr[1].length() != 11){
                        Tools.showToastMid(getActivity().getApplicationContext(),"您的手机号码未绑定或不正确，请联系客服人员");
                        return;
                    }
                    i.putExtra("phone", phoneStr[1]);
                    i.putExtra("name", nameEditText.getText().toString());
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
