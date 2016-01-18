package com.occs.ldsoft.occs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by yeliu on 15/7/25.
 */
public class RegisterInputFragment extends Fragment {
    public ListView registerListinput;
    public Button registerSubmitBtn;
    public CheckBox registerCheckbox;
    public TextView registerTextView;
    public Button registerGetCode;
    public int typeNumber;
    public int btnColor;

    private String username = null;
    private String phonenumber = null;
    private String codenumber = null;
    private String password = null;
    private String confirmpass = null;
    private String recommandid = null;

    ArrayList<InputCell> listCells = new ArrayList<InputCell>();
    private static final String TAG = "RegisterInputFragment";
    private boolean isLoadingWeb = false;
    private boolean isValidating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listCells.add(new InputCell(R.drawable.user_01, "请设置用户名", InputType.TYPE_CLASS_TEXT));
        listCells.add(new InputCell(R.drawable.iphone_01, "请输入手机", InputType.TYPE_CLASS_PHONE));
        listCells.add(new InputCell(R.drawable.check_01, "请输入验证码", InputType.TYPE_CLASS_NUMBER));
        listCells.add(new InputCell(R.drawable.password_01, "请设置密码", InputType.TYPE_TEXT_VARIATION_PASSWORD));
        listCells.add(new InputCell(R.drawable.password_01, "请确认密码", InputType.TYPE_TEXT_VARIATION_PASSWORD));
        listCells.add(new InputCell(R.drawable.id_01, "推荐人ID", InputType.TYPE_CLASS_TEXT));
        Person p = Person.getPerson();
        typeNumber = p.getTypeNumber();
        btnColor = p.getColorFromType(p.getTypeNameFromInt(typeNumber));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_input_fragment, container, false);
        registerListinput = (ListView) v.findViewById(R.id.register_input_list);
        registerListinput.setDivider(null);
        registerSubmitBtn = (Button) v.findViewById(R.id.register_submit_btn);
        registerCheckbox = (CheckBox) v.findViewById(R.id.register_input_check);
        registerListinput.setAdapter(new RegisterInputAdapter(listCells));
        registerTextView = (TextView) v.findViewById(R.id.register_input_title);
        registerGetCode = (Button) v.findViewById(R.id.register_getcode);

        TextView contractTxt = (TextView) v.findViewById(R.id.contract_txt);
        contractTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RegisterContractActivity.class);
                startActivity(i);
            }
        });
//        registerSubmitBtn.setBackgroundColor(getResources().getColor(btnColor));

        String registerTitle = "注册";
        switch (typeNumber) {
            case 1:
                registerTitle += "个人用户";
                break;
            case 91:
                registerTitle += "软件公司";
                break;
            case 9:
                registerTitle += "企业用户";
                break;
        }
        registerTextView.setText(registerTitle);

        registerSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "username: " + username + "   codenumber: " + codenumber + "   password: " + password + "   confirmpass: " + confirmpass);
                if (username != null && username != ""
                        && codenumber != null && codenumber != ""
                        && password != null && password != ""
                        && confirmpass != null && confirmpass != "") {
                    if (password.equals(confirmpass)) {
                        if (registerCheckbox.isChecked()) {
                            if (!isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                                new FetchValidateTask().execute();
                            }
                        } else {
                            Tools.showToastMid(getActivity(), "请同意协议内容");
                            return;
                        }
                    } else {
                        Tools.showToastMid(getActivity(), "两次密码输入不同");
                        return;
                    }

                } else {
                    Tools.showToastMid(getActivity(), "请输入除了推荐人外的选项");
                    return;
                }
            }
        });

        registerGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoadingWeb && !isValidating && Tools.isNetworkConnected(getActivity())) {
                    registerGetCode.setBackgroundResource(R.drawable.gray_btn_normal);
                    new FetchCodeTask().execute();
                }
            }
        });

        return v;
    }

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.equals("")) { // for backspace
                return source;
            }
            if (source.toString().matches("[a-zA-Z0-9_]+")) {
                return source;
            }
            return "";
        }
    };

    private class RegisterInputAdapter extends ArrayAdapter<InputCell> {

        public RegisterInputAdapter(ArrayList<InputCell> cells) {
            super(getActivity(), 0, cells);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.register_list_cell, null);
            }
            InputCell cell = getItem(position);

            final EditText editTxt = (EditText) convertView.findViewById(R.id.register_cell_edittxt);
            editTxt.setFilters(new InputFilter[]{filter});
            editTxt.setHint(cell.getHint());
            editTxt.setCompoundDrawablesWithIntrinsicBounds(cell.getImage(), 0, 0, 0);
            if (position == 3 || position == 4) {
            } else {
                editTxt.setInputType(cell.getInputType());
            }
            editTxt.setTag(position);
            editTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int pos = (int) editTxt.getTag();
                    switch (pos) {
                        case 0:
                            editTxt.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(12)});
                            if (editTxt.getText().toString().equals("_")) {
                                editTxt.setText("");
                                username = "";
                                return;
                            }
                            username = editTxt.getText().toString();
                            Log.d(TAG, "username :" + username);
                            break;
                        case 1:
                            phonenumber = editable.toString();
                            Log.d(TAG, "phonenumber :" + phonenumber);
                            break;
                        case 2:
                            codenumber = editable.toString();
                            Log.d(TAG, "codenumber :" + codenumber);
                            break;
                        case 3:
                            password = editable.toString();
                            Log.d(TAG, "password :" + password);
                            break;
                        case 4:
                            confirmpass = editable.toString();
                            Log.d(TAG, "confirmpass :" + confirmpass);
                            break;
                        case 5:
                            recommandid = editable.toString();
                            break;
                    }
                }
            });

            return convertView;
        }
    }

    private class InputCell {
        private int image;
        private String hint;
        private int inputType;

        public InputCell(int img, String txt, int type) {
            image = img;
            hint = txt;
            inputType = type;
        }

        public int getInputType() {
            return inputType;
        }

        public void setInputType(int inputType) {
            this.inputType = inputType;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }
    }

    private class FetchCodeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + phonenumber + "");
            String url = Uri.parse(WebLinkStatic.GETPHONECODEREG).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("userid", "")
                    .appendQueryParameter("phone", phonenumber).build().toString();
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
                    if (parser.getName().equals("status")) {
                        status = readText(parser);
                        Log.d(TAG, status);
                    }
                    ;

                    if (parser.getName().equals("msg")) {
                        msg = readText(parser);
                        Log.d(TAG, msg);
                    }
                    ;
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo) {
                case 0:
                case 2:
                case 3:
                    Tools.showToastMid(getActivity(), msg);
                    registerGetCode.setBackgroundResource(R.drawable.demand_btn_normal);
                    break;
                case 1:
                    if (!isValidating) {
                        isValidating = true;
                        new CountDownTimer(60000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

                            public void onTick(long millisUntilFinished) {
                                registerGetCode.setText(millisUntilFinished / 1000 + "秒后获取");
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                registerGetCode.setBackgroundResource(R.drawable.demand_btn_normal);
                                registerGetCode.setText("获取验证码");
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

    private class FetchRegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + phonenumber + "");
            String url = Uri.parse(WebLinkStatic.TELREGISTER).buildUpon()
                    .appendQueryParameter("key", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("phone", phonenumber)
                    .appendQueryParameter("groupid", String.valueOf(typeNumber)).build().toString();
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
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

        }

        void parseLogInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType = parser.next();
            String status = null;
            String msg = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("status")) {
                        status = readText(parser);
                        Log.d(TAG, status);
                    }
                    ;

                    if (parser.getName().equals("msg")) {
                        msg = readText(parser);
                        Log.d(TAG, msg);
                    }
                    ;
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo) {
                case 0:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    Tools.showToastMid(getActivity(), msg);
                    break;
                case 1:
                    Tools.showToastMid(getActivity(), msg);
                    Person p = Person.getPerson();
                    p.setName(username);
                    p.setPassword(password);
                    p.setPersonPreference(getActivity().getApplicationContext());
                    Intent i = new Intent(getActivity(), StartActivity.class);
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

    private class FetchValidateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Log.d(TAG, "phonenumber is: " + phonenumber + "");
            String url = Uri.parse(WebLinkStatic.CODEVALIDATE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("phoneOrEmail", phonenumber)
                    .appendQueryParameter("code", codenumber)
                    .appendQueryParameter("userid", "").build().toString();
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
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

        }

        void parseLogInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType = parser.next();
            String status = null;
            String msg = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("status")) {
                        status = readText(parser);
                        Log.d(TAG, status);
                    }
                    ;

                    if (parser.getName().equals("msg")) {
                        msg = readText(parser);
                        Log.d(TAG, msg);
                    }
                    ;
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo) {
                case 0:
                case 2:
                case 3:
                    Tools.showToastMid(getActivity(), msg);
                    break;
                case 1:
                    if (Tools.isNetworkConnected(getActivity()) && !isLoadingWeb) {
                        if (username.length() >= 5) {
                            new FetchRegisterTask().execute();
                        } else {
                            Tools.showToastMid(getActivity(), "用户名大于5个字符");
                        }
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
}
