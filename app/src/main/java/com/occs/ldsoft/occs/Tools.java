package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yeliu on 15/7/29.
 */
public class Tools {

    public static final int MEDIA_TYPE_IMAGE = 11;
    public static final int MEDIA_TYPE_VIDEO = 21;
    public static Toast toast;

    //所有个人信息里面的列表内容
    public static final String[] PERSONINFOLIST = {
            "用户名", "姓名", "昵称", "性别", "证件号", "生日", "地址", "毕业院校", "手机号", "邮箱", "QQ", "微信", "推荐人"
    };

    //所有个人信息里面的列表内容（英文对应）
    public static final String[] PERSONINFOLISTENG = {
            "login_name", "user_name", "nick_name", "Sex", "idno", "birthday", "address", "educollege", "mobile", "email", "qq", "weixin", "tjid"
    };


    //所有个人信息里面的列表无法编辑的内容
    public static final String[] PERSONINFONOEDIT = {
            "用户名", "姓名", "证件号", "手机号", "邮箱", "推荐人"
    };

    //所有企业信息里面的列表内容
    public static final String[] COMPANYINFOLIST = {
            "用户名", "企业名称", "企业昵称", "企业行业", "公司地址", "公司电话", "公司网址", "组织机构代码", "公司简介"
    };

    //所有企业信息里面的列表无法编辑的内容
    public static final String[] COMPANYINFONOEDIT = {
            "用户名", "企业名称", "企业昵称", "企业行业", "公司地址", "公司电话", "公司网址", "组织机构代码", "公司简介"
    };

    //通过颜色得到按钮
    public static int colorToBtnStyle(int contentColor) {
        int drawableID;
        switch (contentColor) {
            case R.color.btn_blue_normal:
                drawableID = R.drawable.login_btn_normal;
                break;
            case R.color.btn_cyan_normal:
                drawableID = R.drawable.register_btn_normal;
                break;
            case R.color.btn_orange_normal:
                drawableID = R.drawable.demand_btn_normal;
                break;
            default:
                drawableID = R.drawable.login_btn_normal;
                break;
        }
        return drawableID;
    }

    //得到软件的版本号
    public static String getVersionName(Context c)
    {
        String versionName = "";
        try
        {
            // 获取软件版本号，对应AndroidManifest.xml下android:version name
            versionName = c.getPackageManager().getPackageInfo("com.occs.ldsoft.occs", 0).versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionName;
    }

    //存图标
    public static void saveAvatarIcon(File imgFile, Bitmap img) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imgFile);
            img.compress(Bitmap.CompressFormat.JPEG, 85, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Person.getPerson().setAvatar(getOutputMediaFileUri(MEDIA_TYPE_IMAGE).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static String encodeTobase64(byte[] b) {
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Create a file Uri for saving an image or video
     */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OCCSAPP");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_icon_" + Person.getPerson().getName() + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static int getIndexInStringArray(String item, String[] array) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(item)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

//存当前的tab
    public static void setCurTab(String s, Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("curTab", s);
        editor.commit();
    }

    public static String getCurTab(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("userData", Context.MODE_PRIVATE);
        String curTab = sharedPref.getString("curTab", "none");
        return curTab;
    }

    static public Drawable getAndroidDrawable(String pDrawableName) {
        int resourceId = Resources.getSystem().getIdentifier(pDrawableName, "drawable", "android");
        if (resourceId == 0) {
            return null;
        } else {
            return Resources.getSystem().getDrawable(resourceId);
        }
    }

//通过String拿到相应的Drawable
    static public int getPackageDrawable(Activity activity, String drawableName) {
        int resourceId = activity.getResources().getIdentifier(drawableName, "drawable", activity.getPackageName());
        return resourceId;
    }

    /**
     * Checks to see if the phone is currently in silent mode.
     */
    static public Boolean isPhoneSilent(Context c) {
        AudioManager mAudioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = mAudioManager.getRingerMode();
        if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        } else {
            return false;
        }

    }

    static public void setRingerMode(Context c, int mode){
        AudioManager mAudioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(mode);
    }

    //在屏幕中间显示Toast

    public static void showToastMid(Context c, String message) {
        if (Tools.toast != null){
            Tools.toast.cancel();
            Tools.toast = null;
        }
        Tools.toast = Toast.makeText(c, message, Toast.LENGTH_SHORT);
        Tools.toast.setGravity(Gravity.CENTER, 0, 0);
        Tools.toast.show();
    }
    //测试网络状态
    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            showToastMid(c, "网络异常");
            return false;
        } else
            return true;
    }

    //字符串转long日期
    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }
    //字符串转Date
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }
}
