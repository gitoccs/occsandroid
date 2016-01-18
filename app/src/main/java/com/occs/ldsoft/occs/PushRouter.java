package com.occs.ldsoft.occs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by yeliu on 15/11/20.
 */
public class PushRouter {

    private static final String TAG = "PushRouter";

    private static String actionToRouter(String action){
        if(action.equals(""))
            return "";
        String pushAction;
        switch (Integer.parseInt(action)){
            case 1:
                pushAction = "个人主页";
                break;
            case 2:
                pushAction = "需求市场";
                break;
            case 3:
                pushAction = "工单市场";
                break;
            case 4:
                pushAction = "岗位应聘";
                break;
            case 5:
                pushAction = "需求发布";
                break;
            case 6:
                pushAction = "特殊页面";
                break;
            case 7:
                pushAction = "模板1";
                break;
            case 8:
                pushAction = "推送工单详情";
                break;
            case 9:
                pushAction = "推送需求详情";
                break;
            case 10:
                pushAction = "个人账户";
                break;
            case 11:
                pushAction = "个人信息";
                break;
            default:
                pushAction = "";
                break;
        }
        return pushAction;
    }

    public static void saveNoteBroadcast(final Context context, String extras, String message){
        String action = null;
        String note_id = null;
        String note_time = null;
        String note_type = null;
        boolean isAnybody = false;

        try {
            JSONObject jObject = new JSONObject(extras);
            note_time = jObject.getString("time");
            action = jObject.getString("action");
            note_id = jObject.getString("id");
            note_type = actionToRouter(action);
            if (jObject.getString("identity").equals("1"))
                isAnybody = true;
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }

        try {
            PushNote note = new PushNote(note_id, note_time, note_type, action, message, 0, -1, isAnybody);
            Context appContext = context.getApplicationContext();
            PushNotificationManager noteManger = PushNotificationManager.getInstance();
            long count = noteManger.insertNote(note, appContext);

            Intent i;
            if(action.equals("8")){
                if (Tools.isNetworkConnected(context) && !WebFunctionHelper.FetchAppMessage.isLoadingWeb){
                    new WebFunctionHelper.FetchAppMessage(note_id){

                        @Override
                        public void getResultFunction(HashMap<String, String> map) {
                            try {
                                JSONObject extra = new JSONObject(map.get("push_identity"));
                                String workid = extra.getString("workid");
                                if (Tools.isNetworkConnected(context) && !WebFunctionHelper.FetchGongdanDetail.isLoadingWeb){
                                    new WebFunctionHelper.FetchGongdanDetail(workid){

                                        @Override
                                        public void getResultFunction(XmlPullParser parser) throws IOException, XmlPullParserException {
                                            int eventType = parser.next();
                                            String title = "";
                                            String project = "";
                                            String type = "";
                                            String period = "";
                                            String cost = "";
                                            String time = "";
                                            String deadline = "";
                                            String staus = "";

                                            while (eventType != XmlPullParser.END_DOCUMENT) {

                                                if (eventType == XmlPullParser.START_TAG) {
                                                    if (parser.getName().equals("title")){
                                                        title = readText(parser);
                                                    }

                                                    if (parser.getName().equals("projectname")){
                                                        project = readText(parser);
                                                    }

                                                    if (parser.getName().equals("workdays")){
                                                        period = readText(parser);
                                                    }

                                                    if(parser.getName().equals("typename")){
                                                        type = readText(parser);
                                                    }

                                                    if (parser.getName().equals("balances")){
                                                        cost = readText(parser);
                                                    }

                                                    if (parser.getName().equals("addtime")){
                                                        time = readText(parser);
                                                    }

                                                    if (parser.getName().equals("deadline")){
                                                        deadline = readText(parser);
                                                        Log.d(TAG,deadline);
                                                        long nowlong = Tools.dateToLong(new Date());
                                                        long deadlong = 0;
                                                        try {
                                                            deadlong = Tools.stringToLong(deadline,"yyyy-MM-dd HH:mm:ss");
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if(deadlong == 0){
                                                            staus = "无截止时间";
                                                        } else if(nowlong <= deadlong){
                                                            staus = "竞标中";
                                                        } else {
                                                            staus = "竞标结束";
                                                        }
                                                    }
                                                }
                                                eventType = parser.next();
                                            }
                                            try {
                                                Gongdan gongdan = new Gongdan(workid, time, deadline, type, title, cost, project, period,staus,0,-1);
                                                long num = GongdanSuitableManager.getInstance().insertGongdan(gongdan, context.getApplicationContext());
                                                Intent i = new Intent("com.occs.sysNoteReceiver");
                                                context.sendBroadcast(i);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.execute();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();
                }
            }else{
                i = new Intent("com.occs.sysNoteReceiver");
                i.putExtra("id", note_id);
                context.sendBroadcast(i);
            }
        } catch (ParseException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    public static void routeNotificationWithNote(Context context, PushNote note) {
        String note_type = PushRouter.actionToRouter(String.valueOf(note.getAction_code()));
        goRouteNote(context, String.valueOf(note.getNote_id()),note_type,false);
    }

    public static void routeNotificationFromJson(Context context, String extras){

        String action = null;
        String note_id = null;
        String note_time = null;
        String note_type = null;

        try {
            JSONObject jObject = new JSONObject(extras);
            note_time = jObject.getString("time");
            action = jObject.getString("action");
            note_id = jObject.getString("id");
            note_type = PushRouter.actionToRouter(action);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        goRouteNote(context, note_id, note_type, true);
    }

    public static void goRouteNote(Context context, String note_id, String note_type, boolean newIntent){
        Log.d(TAG, String.valueOf(newIntent));
        if (newIntent){
            Intent i;
            if (Person.getPerson().getPassword() == null || Person.getPerson().getPassword().isEmpty()){
                i = new Intent(context, LogInActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("typenumber", Person.getPerson().getTypeNumber());
                i.putExtra("phonenumber", Person.getPerson().getMobile());
                i.putExtra("name", Person.getPerson().getName());
                i.putExtra("password", Person.getPerson().getPassword());
                i.putExtra("email", Person.getPerson().getEmail());
                i.putExtra("key", Person.getPerson().getKey());
                i.putExtra("selectTab",1);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
                return;
            }else{
                i = new Intent(context, TabViewContainerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("typenumber", Person.getPerson().getTypeNumber());
                i.putExtra("phonenumber", Person.getPerson().getMobile());
                i.putExtra("password", Person.getPerson().getPassword());
                i.putExtra("name", Person.getPerson().getName());
                i.putExtra("email", Person.getPerson().getEmail());
                i.putExtra("key", Person.getPerson().getKey());
                i.putExtra("selectTab", 0);

                switch (Person.getPerson().getTypeNumber()) {
                    case 1:
                        String[] titleStr1 = {"我的首页", "工单市场", "岗位认证"};
                        String[] onImageStr1 = {"message_1", "gongdan_1", "job_1"};
                        i.putExtra("titleArray", titleStr1);
                        i.putExtra("imageArray", onImageStr1);
                        i.putExtra("type", "个人");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                        break;
                    case 9:
                        String[] titleStr2 = {"我的首页", "需求市场", "发布需求"};
                        String[] onImageStr2 = {"message_2", "need_1", "post_1"};
                        i.putExtra("titleArray", titleStr2);
                        i.putExtra("imageArray", onImageStr2);
                        i.putExtra("type", "企业");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                        break;
                    case 91:
                        String[] titleStr3 = {"我的首页", "需求市场", "发布需求"};
                        String[] onImageStr3 = {"message_3", "need_2", "post_2"};
                        i.putExtra("titleArray", titleStr3);
                        i.putExtra("imageArray", onImageStr3);
                        i.putExtra("type", "软企");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(i);
                        break;
                }
            }
        }

        if (note_type != null && !note_type.isEmpty()){
            switch (note_type){
                case "个人主页":
                case "需求市场":
                case "工单市场":
                case "岗位应聘":
                case "需求发布":
                    openTabFunction(context, note_type, newIntent);
                    break;
                case "个人账户":
                case "个人信息":
                case "推送工单详情":
                case "推送需求详情":
                    openPageFunction(context, note_type, note_id, newIntent);
                    break;
                case "模板1":
                    openTemplateFunction(context,note_type, note_id, newIntent);
                    break;
                case "特殊页面":
                    openSpecialFunction(context,note_type, note_id, newIntent);
                    break;
            }
        }else{
            return;
        }
    }

    public static void openPageFunction(Context context, String pushAction, String note_id, boolean newIntent) {
        String action = null;
        switch (pushAction){
            case "个人信息":
                action = ".PersonInfoActivity";
                break;
            case "个人账户":
                action = ".MyAccountWebActivity";
                break;
            case "推送工单详情":
                action = ".GongdanSingleWebActivity";
                break;
        }

        if (action != null){
            Log.d(TAG, "send com.occs.openActivity");
            Intent i = new Intent("com.occs.openActivity");
            i.putExtra("action",action);
            i.putExtra("pushAction",pushAction);
            i.putExtra("id", note_id);
            i.putExtra("newIntent", newIntent);
//            PendingIntent sendPI = PendingIntent.getBroadcast(context, 0, i,PendingIntent.FLAG_CANCEL_CURRENT);
            context.sendStickyBroadcast(i);
        }
    }

    private static void openTemplateFunction(Context context, String pushAction, String note_id, boolean newIntent){
        String action = null;
        switch (pushAction){
            case "模板1":
                action = ".OccsSingleWebPageActivity";
                break;
        }
        if (action != null){
            Intent i = new Intent("com.occs.openActivity");
            i.putExtra("id",note_id);
            i.putExtra("action",action);
            i.putExtra("pushAction",pushAction);
            i.putExtra("baseurl",WebLinkStatic.NOTETEMPLATE1);
            i.putExtra("newIntent", newIntent);
            context.sendStickyBroadcast(i);
        }
    }

    private static void openSpecialFunction(Context context, String pushAction,String note_id, boolean newIntent){
        String action = null;
        switch (pushAction){
            case "特殊页面":
                action = ".OccsSingleWebPageActivity";
                break;
        }
        if (action != null){
            Intent i = new Intent("com.occs.openActivity");
            i.putExtra("id",note_id);
            i.putExtra("action",action);
            i.putExtra("pushAction",pushAction);
            i.putExtra("baseurl","getUrlFromAPI");
            i.putExtra("newIntent", newIntent);
            context.sendStickyBroadcast(i);
        }
    }

    private static void openTabFunction(Context context, String pushAction, boolean newIntent){
        Intent i;

        if (pushAction == null || pushAction.isEmpty()){
            i = new Intent(context, LogInActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("typenumber", Person.getPerson().getTypeNumber());
            i.putExtra("phonenumber", Person.getPerson().getMobile());
            i.putExtra("name", Person.getPerson().getName());
            i.putExtra("email", Person.getPerson().getEmail());
            i.putExtra("password", Person.getPerson().getPassword());
            i.putExtra("key", Person.getPerson().getKey());
            i.putExtra("selectTab",1);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
            return;
        }else{
            i = new Intent(context, TabViewContainerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("typenumber", Person.getPerson().getTypeNumber());
            i.putExtra("phonenumber", Person.getPerson().getMobile());
            i.putExtra("name", Person.getPerson().getName());
            i.putExtra("password", Person.getPerson().getPassword());
            i.putExtra("email", Person.getPerson().getEmail());
            i.putExtra("key", Person.getPerson().getKey());
            i.putExtra("selectTab",1);
        }

        switch (pushAction){
            case "个人主页":
                i.putExtra("selectTab",0);
                break;
            case "工单市场":
            case "需求市场":
                i.putExtra("selectTab",1);
                break;
            case "需求发布":
                i.putExtra("selectTab",2);
                break;
        }

        switch (Person.getPerson().getTypeNumber()){
            case 1 :
                String[] titleStr1 = {"我的首页", "工单市场", "岗位认证"};
                String[] onImageStr1 = {"message_1", "gongdan_1", "job_1"};
                i.putExtra("titleArray", titleStr1);
                i.putExtra("imageArray", onImageStr1);
                i.putExtra("type", "个人");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
                break;
            case 9 :
                String[] titleStr2 = {"我的首页", "需求市场", "发布需求"};
                String[] onImageStr2 = {"message_2", "need_1", "post_1"};
                i.putExtra("titleArray", titleStr2);
                i.putExtra("imageArray", onImageStr2);
                i.putExtra("type","企业");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
                break;
            case 91 :
                String[] titleStr3 = {"我的首页", "需求市场", "发布需求"};
                String[] onImageStr3 = {"message_3", "need_2", "post_2"};
                i.putExtra("titleArray", titleStr3);
                i.putExtra("imageArray", onImageStr3);
                i.putExtra("type","软企");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
                break;
        }
    }
}
