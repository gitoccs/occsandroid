package com.occs.ldsoft.occs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by yeliu on 15/8/5.
 */
public class StartFragment extends Fragment {

    private static final String TAG = "StartFragment";
    public String nameStr;
    public String passStr;
    public Boolean okTologin;
    private String downloadUrl;

    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    Log.d(TAG, "准备安装文件");
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!FetchVersionQuery.isLoadingWeb && Tools.isNetworkConnected(getActivity())){
            new FetchVersionQuery().execute();
        }
    }

    public void initLogin(){
        Person p = Person.getPersonLogin(getActivity().getApplicationContext());
        nameStr = p.getName();
        passStr = p.getPassword();
        Log.i(TAG, "name: " + nameStr + "    " + "password: " + passStr);
        if (nameStr.isEmpty() || passStr.isEmpty()){
            goToLogoin();
        }else{
            if(!WebFunctionHelper.FetchLoginTask.isLoadingWeb && Tools.isNetworkConnected(getActivity())){
                new FetchLoginGo(nameStr, passStr, null).execute();
            }
        }
    }

    public void compareVersion(HashMap<String,String> versionMap){
        String tarVer = versionMap.get("version");
        String[] tarVerList = tarVer.split("\\.");
        int tarVerNum = Integer.parseInt(tarVerList[0])*100 + Integer.parseInt(tarVerList[1])*10
                + Integer.parseInt(tarVerList[2]);
        int tarMain = Integer.parseInt(tarVerList[0]);

        String curVer = Tools.getVersionName(getActivity().getApplicationContext());
        Log.d(TAG,curVer);
        String[] curVerList = curVer.split("\\.");
        int curVerNum = Integer.parseInt(curVerList[0])*100 + Integer.parseInt(curVerList[1])*10
                + Integer.parseInt(curVerList[2]);
        int curMain = Integer.parseInt(curVerList[0]);

        if (tarVerNum > curVerNum) {
            String strategy = versionMap.get("strategy");
            downloadUrl = versionMap.get("url");
            String optionStr = versionMap.get("optional");
            String mustStr = versionMap.get("must");
            Log.d(TAG, optionStr);
            switch (strategy) {
                case "optional":
                    dialog("提示", optionStr);
                    okTologin = true;
                    break;
                case "must":
                    dialog("提示", mustStr);
                    okTologin = false;
                    break;
                case "main_version_must":
                    if (tarMain > curMain) {
                        dialog("提示", mustStr);
                        okTologin = false;
                    } else {
                        dialog("提示", optionStr);
                        okTologin = true;
                    }
                    break;
            }
        } else {
          initLogin();
        }
    }

    protected void dialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (okTologin){
                    initLogin();
                } else {
                    getActivity().finish();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void goToLogoin() {
        Intent i = new Intent(getActivity(), LogInActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_fragment_layout,container,false);
        return v;
    }

    private class FetchLoginGo extends WebFunctionHelper.FetchLoginTask {

        public FetchLoginGo(String nameStr, String passStr, ProgressBar progressBar) {
            super(nameStr, passStr, null);
        }

        @Override
        public void successFunction(String msg) {
            WebFunctionHelper.FetchLoginTask.fetchLoginSuccess(getActivity());
        }

        @Override
        public void failFunction(String msg) {
            Tools.showToastMid(getActivity(), msg);
            goToLogoin();
        }
    }

    private class FetchVersionQuery extends WebFunctionHelper.FetchVersion {

        @Override
        public void getResultFunction(HashMap<String, String> versionMap) {
            compareVersion(versionMap);
        }
    }

    private void showDownloadDialog()
    {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk()
    {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     *@date 2012-4-26
     *@blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(downloadUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists())
                    {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "occs.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do
                    {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0)
                        {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk()
    {
        File apkfile = new File(mSavePath, "occs.apk");
        if (!apkfile.exists())
        {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(i);
    }
}
