package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by yeliu on 15/8/4.
 * 大部分的web API的访问部分
 */
public class WebFunctionHelper {

    public abstract static class FetchLoginTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "FetchLoginTask";
        public static boolean isLoadingWeb;
        private static ProgressBar progressbar;
        private static String nameStr;
        private static String passStr;
        private static String msg;

        public FetchLoginTask(String nameStr, String passStr, ProgressBar progressBar) {
            this.nameStr = nameStr;
            this.passStr = passStr;
            this.progressbar = progressBar;
        }

        public abstract void successFunction(String msg);
        public abstract void failFunction(String msg);

        @Override
        protected void onPreExecute() {
            if (progressbar != null){progressbar.setVisibility(View.VISIBLE);}
        }

        @Override
        protected String doInBackground(Void... voids) {
            FetchLoginTask.isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.LOGINAPI).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("key","")
                    .appendQueryParameter("username", nameStr)
                    .appendQueryParameter("password", passStr).build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            if (progressbar != null){progressbar.setVisibility(View.INVISIBLE);}
            FetchLoginTask.isLoadingWeb = false;
            Log.i("xmlString : ", xmlString);
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
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("status")){
                        status = readText(parser);
                    }

                    if (parser.getName().equals("msg")){
                        msg = readText(parser);
                    }
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo){
                case 0:
                    failFunction(msg);
                    break;
                case 1:
                    successFunction(msg);
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

        public static void fetchLoginSuccess(Activity c){
            JPushInterface.setDebugMode(true);
            JPushInterface.init(c);
            JPushInterface.setAlias(c.getApplicationContext(), nameStr, new TagAliasCallback() {
                @Override
                public void gotResult(int responseCode, String alias, Set<String> tags) {
                    // TODO
                    if (responseCode == 0) {
                        Log.i("alias", alias.toString());
                    }
                }
            });

            Log.d(TAG, msg);
            String[] msgAry = msg.split("\\,");
            String moblieStr = "";
            if (msgAry.length != 4){
                if (msgAry.length == 3){
                    moblieStr = "";
                }else{
                    Tools.showToastMid(c,"你的登录信息异常，请和管理员联系");
                    return;
                }
            }else{
                moblieStr = msgAry[3];
            }
            String key = msgAry[0];
            String type = msgAry[1];
            String email = msgAry[2];
            String phoneNumber = moblieStr;
            Intent i = new Intent(c, TabViewContainerActivity.class);
            i.putExtra("typenumber", Integer.parseInt(type));
            i.putExtra("phonenumber", phoneNumber);
            i.putExtra("name", nameStr);
            i.putExtra("password", passStr);
            i.putExtra("email", email);
            i.putExtra("key", key);

            Person p = Person.getPerson();
            p.setName(nameStr);
            p.setPassword(passStr);
            p.setMobile(phoneNumber);
            p.setEmail(email);
            p.setKey(key);
            p.setTypeNumber(Integer.parseInt(type));
            p.setPersonPreference(c.getApplicationContext());

            switch (Integer.parseInt(type)){
                case 1 :
                    String[] titleStr1 = {"我的首页", "工单市场", "岗位认证"};
                    String[] onImageStr1 = {"message_1", "gongdan_1", "job_1"};
                    i.putExtra("titleArray", titleStr1);
                    i.putExtra("imageArray", onImageStr1);
                    i.putExtra("selectTab",0);
                    i.putExtra("type", "个人");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    c.startActivity(i);
                    c.finish();
                    break;
                case 9 :
                    String[] titleStr2 = {"我的首页", "需求市场", "发布需求"};
                    String[] onImageStr2 = {"message_2", "need_1", "post_1"};
                    i.putExtra("titleArray", titleStr2);
                    i.putExtra("imageArray", onImageStr2);
                    i.putExtra("selectTab",0);
                    i.putExtra("type","企业");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    c.startActivity(i);
                    c.finish();
                    break;
                case 91 :
                    String[] titleStr3 = {"我的首页", "需求市场", "发布需求"};
                    String[] onImageStr3 = {"message_3", "need_2", "post_2"};
                    i.putExtra("titleArray", titleStr3);
                    i.putExtra("imageArray", onImageStr3);
                    i.putExtra("selectTab",0);
                    i.putExtra("type","软企");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    c.startActivity(i);
                    c.finish();
                    break;
            }
        }
    }

    /////////////////////////////// Fetch Person Info  /////////////////////////////////////////

    public abstract static class FetchPersonOrComInfo extends AsyncTask<Void, Void, String> {

        private static final String TAG = "FetchPersonInfo";
        public static String linkurl;
        public static boolean isLoadingWeb;
        public static String key;
        public static String mobile;
        public static String name;
        public static String email;
        public static String password;
        public static int typeNumber;
        public static String parName;
        public static Context context;

        public abstract void successFunction();

        public FetchPersonOrComInfo(String type, String key, String mobile, String name,
                               String email, String password, int typeNumber, Context c) {
            if (type.equals("个人")) {
                FetchPersonOrComInfo.linkurl = WebLinkStatic.PERSONINFO;
                FetchPersonOrComInfo.parName = "username";
            }else{
                FetchPersonOrComInfo.linkurl = WebLinkStatic.COMPANYINFO;
                FetchPersonOrComInfo.parName = "corpname";
            }

            FetchPersonOrComInfo.key = key;
            FetchPersonOrComInfo.mobile = mobile;
            FetchPersonOrComInfo.name = name;
            FetchPersonOrComInfo.email = email;
            FetchPersonOrComInfo.password = password;
            FetchPersonOrComInfo.typeNumber = typeNumber;
            FetchPersonOrComInfo.context = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            FetchPersonOrComInfo.isLoadingWeb = true;
            Person p = Person.getPerson();
            String username = p.getName();
            Log.d(TAG,linkurl);
            String url = Uri.parse(linkurl).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter(parName,username).build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            FetchPersonOrComInfo.isLoadingWeb = false;
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
            String avatar = "";
            String realname = "";
            String nickname = "";
            String sex = "";
            String idno = "";
            String birthday = "";
            String address = "";
            String educollege = "";
            String degree = "";
            String profession = "";
            String skill = "";
            String qq = "";
            String weixin = "";
            String tjid = "";
            String amount = "0";
            String ocoin = "0";
            String ocoinCash = "0";
            String ocoinFree = "0";
            String industry = "";
            String url = "";
            String orgcode = "";
            String summary = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("avatar")){avatar = readText(parser);}
                    if (parser.getName().equals("nick_name")){nickname = readText(parser);}
                    if (parser.getName().equals("user_name")){realname = readText(parser);}
                    if (parser.getName().equals("sex")){sex = readText(parser);}
                    if (parser.getName().equals("idno")){idno = readText(parser);}
                    if (parser.getName().equals("birthday")){birthday = readText(parser);}
                    if (parser.getName().equals("address")){address = readText(parser);}
                    if (parser.getName().equals("educollege")){educollege = readText(parser);}
                    if (parser.getName().equals("degree")){degree = readText(parser);}
                    if (parser.getName().equals("profession")){profession = readText(parser);}
                    if (parser.getName().equals("skill")){skill = readText(parser);}
                    if (parser.getName().equals("qq")){qq = readText(parser);}
                    if (parser.getName().equals("weixin")){weixin = readText(parser);}
                    if (parser.getName().equals("tjid")){tjid = readText(parser);}
                    if (parser.getName().equals("amount")){amount = readText(parser);}
                    if (parser.getName().equals("ocoin")){ocoin = readText(parser);}
                    if (parser.getName().equals("ocoinCash")){ocoinCash = readText(parser);}
                    if (parser.getName().equals("ocoinFree")){ocoinFree = readText(parser);}
                    if (parser.getName().equals("industry")){industry = readText(parser);}
                    if (parser.getName().equals("url")){url = readText(parser);}
                    if (parser.getName().equals("orgcode")){orgcode = readText(parser);}
                    if (parser.getName().equals("summary")){summary = readText(parser);}
                }
                eventType = parser.next();
            }
            Person p = Person.getInstance(avatar, name, realname, nickname, sex, idno, birthday, address,
                    educollege, degree, mobile, email, profession, skill, qq, weixin, tjid, amount,
                    ocoin, ocoinCash, ocoinFree, typeNumber, key, password, industry, url, orgcode, summary);
            p.setPersonPreference(context.getApplicationContext());

            Log.i(TAG, name + mobile + realname);
            successFunction();
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

////   problem here

    public abstract static class FetchAllWorkCount extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;

        public abstract void getResultFunction(ArrayList<String> ary);

        @Override
        protected String doInBackground(Void... voids) {
            FetchLoginTask.isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.GETALLWORKCOUNT).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("key", Person.getPerson().getKey())
                    .build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            FetchLoginTask.isLoadingWeb = false;
            if(xmlString == null){
                return;
            }
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
            ArrayList<String> workCountStr = new ArrayList<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("a") || parser.getName().equals("b")||
                            parser.getName().equals("c")|| parser.getName().equals("d")){
                        workCountStr.add(readText(parser));
                    }
                }
                eventType = parser.next();
            }
            getResultFunction(workCountStr);
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

    public abstract static class FetchVersion extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;

        public abstract void getResultFunction(HashMap<String, String> map);

        @Override
        protected String doInBackground(Void... voids) {
            FetchLoginTask.isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.GETVERSION).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            FetchLoginTask.isLoadingWeb = false;
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
            HashMap<String, String> map = new HashMap<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("version")){
                        map.put("version",readText(parser));
                    }else if (parser.getName().equals("url")){
                        map.put("url",readText(parser));
                    }else if (parser.getName().equals("description-must")){
                        map.put("must",readText(parser));
                    }else if (parser.getName().equals("description-optional")){
                        map.put("optional",readText(parser));
                    }else if (parser.getName().equals("strategy")){
                        map.put("strategy",readText(parser));
                    }
                }
                eventType = parser.next();
            }
            getResultFunction(map);
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

    static abstract class FetchUploadUserInfo extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;
        public static String userInfo;

        public FetchUploadUserInfo(String str) {
            this.userInfo = str;
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Person p = Person.getPerson();
            String key = p.getKey();

            String url = Uri.parse(WebLinkStatic.UPLOADUSERINDO).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("key", key)
                    .appendQueryParameter("jsonData", userInfo)
                    .build().toString();
            Log.v("upload url", url);
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
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
                    }

                    if (parser.getName().equals("msg")) {
                        msg = readText(parser);
                    }
                }
                eventType = parser.next();
            }
            Log.v("Upload user info", status);
            Log.v("Upload ", msg);
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

    static abstract class FetchLogoutTask extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;
        public static Fragment f;

        public FetchLogoutTask(Fragment f) {
            this.f = f;
        }

        @Override
        protected String doInBackground(Void... voids) {
            isLoadingWeb = true;
            Person p = Person.getPerson();
            String key = p.getKey();

            String url = Uri.parse(WebLinkStatic.LOGOUTAPI).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("key", key).build().toString();
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
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
                    }
                    ;

                    if (parser.getName().equals("msg")) {
                        msg = readText(parser);
                    }
                    ;
                }
                eventType = parser.next();
            }
            int foo = Integer.parseInt(status);
            switch (foo) {
                case 0:
                    Tools.showToastMid(f.getActivity(), msg);
                    break;
                case 1:
                    JPushInterface.setDebugMode(true);
                    JPushInterface.init(f.getActivity());
                    JPushInterface.setAlias(f.getActivity().getApplicationContext(), "Anybody", new TagAliasCallback() {
                        @Override
                        public void gotResult(int responseCode, String alias, Set<String> tags) {
                            // TODO
                            if (responseCode == 0) {
                                Log.i("alias", alias.toString());
                            }
                        }
                    });

                    if (Person.getPerson() != null) {
                        Person.getInstance().setPersonPreference(f.getActivity().getApplicationContext());
                    }

                    Intent i = new Intent(f.getActivity(), LogInActivity.class);
                    f.startActivity(i);
                    f.getActivity().finish();
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

    //////////////// image loader //////////////////////////////////
    public static abstract class FetchImageTask extends AsyncTask<Void, Void, Void> {
        public Bitmap bitmap;

        public abstract void successFunction(Bitmap bitmap);
        public abstract String getBitmap();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String imageStr = getBitmap();
                if (!imageStr.equals("")) {
                    bitmap = getBitmapFromURL(imageStr);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            successFunction(bitmap);
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                java.net.URL url = new java.net.URL(src);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public abstract static class FetchAppMessage extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;
        public String noteID;

        public abstract void getResultFunction(HashMap<String, String> map);

        public FetchAppMessage(String noteID) {
            this.noteID = noteID;
        }

        @Override

        protected String doInBackground(Void... voids) {
            FetchLoginTask.isLoadingWeb = true;
            String url = Uri.parse(WebLinkStatic.GETAPPMESSAGE).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("MsgId", this.noteID)
                    .build().toString();
            Log.d("url string is:",url);
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            Log.d("xmlString ",xmlString);
            FetchLoginTask.isLoadingWeb = false;
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
            HashMap<String, String> map = new HashMap<>();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("Send_Time")){
                        map.put("send_time",readText(parser));
                    }else if (parser.getName().equals("push_identity")){
                        map.put("push_identity",readText(parser));
                    }else if (parser.getName().equals("Short_Content")) {
                        map.put("short_content", readText(parser));
                    }
                }
                eventType = parser.next();
            }
            getResultFunction(map);
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

    public abstract static class FetchGongdanDetail extends AsyncTask<Void, Void, String> {
        public static boolean isLoadingWeb;
        public String workid;

        public abstract void getResultFunction(XmlPullParser xmlStr) throws IOException, XmlPullParserException;

        public FetchGongdanDetail(String workid) {
            this.workid = workid;
        }

        @Override

        protected String doInBackground(Void... voids) {
            FetchLoginTask.isLoadingWeb = true;
            Log.d("web fetch helper", workid);
            String url = Uri.parse(WebLinkStatic.GETPROJECTDETAILS).buildUpon()
                    .appendQueryParameter("keyword", WebLinkStatic.KEYWORD)
                    .appendQueryParameter("workid", this.workid)
                    .appendQueryParameter("ispublic", "1")
                    .build().toString();
            Log.d("url string is:",url);
            return new WebFetcher().fetchItems(url);
        }

        @Override
        protected void onPostExecute(String xmlString) {
            Log.d("xmlString ",xmlString);
            FetchLoginTask.isLoadingWeb = false;
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
            getResultFunction(parser);
        }

        protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }
    }
}
