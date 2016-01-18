package com.occs.ldsoft.occs;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.text.ParseException;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yeliu on 15/7/24.
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "Yale Push Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);
            //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知: " + message);
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);

            PushRouter.saveNoteBroadcast(context, extras, message);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            Log.d(TAG, extras);
            PushRouter.routeNotificationFromJson(context,extras);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


}