package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by yeliu on 15/7/28.
 */
public class OccsWebContentFragment extends Fragment {

    public WebView webView;
    public String url;
    public TextView titleText;
    public ProgressBar progress;
    public ImageButton webViewBack;
    public Toolbar toolbar;
    public static final String TAG = "OccsWebContentFragment";

    public String name = null;
    public String key = null;
    public String phoneNumber = null;
    public String email = null;
    public int typeNumber = 0;
    public int contentColor = 0;
    public int curPage = 1;
    public boolean isFromMainPage;
    public String weburl = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Person p = Person.getPerson();
        name = p.getName();
        key = p.getKey();
        email = p.getEmail();
        phoneNumber = p.getMobile();
        typeNumber = p.getTypeNumber();
        if (typeNumber != 0){
            contentColor = p.getColorFromType(p.getTypeNameFromInt(typeNumber));
        } else {
            contentColor = R.color.btn_orange_normal;
        }

        Log.d(TAG, "all things: " + name + ", " + key + ", " + email + ", " + phoneNumber + ", " + typeNumber);
        Intent i = getActivity().getIntent();
        isFromMainPage = i.getBooleanExtra("isFromMainPage", false);
    }

    public String makeUrl(String baseUrl){
        Log.e(TAG, "phone number: " + phoneNumber);
        String url = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("name", name)
                .appendQueryParameter("key",key)
                .appendQueryParameter("phoneNum", phoneNumber)
                .appendQueryParameter("typeNum", String.valueOf(typeNumber))
                .appendQueryParameter("email", email)
                .build().toString();
        return url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        webView.loadUrl(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.webview_layout, container, false);
        webView = (WebView)v.findViewById(R.id.occs_webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        progress = (ProgressBar)v.findViewById(R.id.web_progressBar1);
        toolbar = (Toolbar)v.findViewById(R.id.drawer_toolbar);
        webViewBack = (ImageButton) v.findViewById(R.id.webview_back_btn);
        toolbar.setBackgroundColor(getResources().getColor(contentColor));
        ImageView leftHandle = (ImageView) v.findViewById(R.id.web_left_handle);
        titleText = (TextView)v.findViewById(R.id.webview_title_txt);
        titleText.setText(Tools.getCurTab(getActivity().getApplicationContext()));

        showHideBackBtn();
        if (isFromMainPage){
            toolbar.setBackgroundColor(getResources().getColor(R.color.btn_orange_normal));
            leftHandle.setVisibility(View.GONE);
        }

        webViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curPage > 1){
                    curPage--;
                    String functionStr = "javascript:setpage" + curPage + "()";
                    webView.loadUrl(functionStr);
                }else{
                    String className = getActivity().getLocalClassName();
                    if(!className.equals("TabViewContainerActivity")){
                        getActivity().finish();
                    }
                }
            }
        });

        setHasOptionsMenu(true);
        return v;
    }

    private void showHideBackBtn() {
        if (curPage <= 1){
            String className = getActivity().getLocalClassName();
            if(className.equals("TabViewContainerActivity")){
                webViewBack.setVisibility(View.INVISIBLE);
            }
            webViewBack.setImageDrawable(getResources().getDrawable(R.drawable.close_01));
        }else{
            webViewBack.setVisibility(View.VISIBLE);
            webViewBack.setImageDrawable(getResources().getDrawable(R.drawable.left_01));
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.startsWith("info://")) {
                String pageValue = url.split("\\?")[1].trim();
                curPage = Integer.parseInt(pageValue.substring(4,pageValue.length()));
                if (curPage != 99){
                    Log.i(TAG, "info url current page is: " + curPage);
                    showHideBackBtn();
                    return true;
                }else{
                    Log.i(TAG, "going to reflesh page");
                    curPage = 1;
                    view.loadUrl(weburl);
                    showHideBackBtn();
                    return true;
                }
            } else if (url != null && url.startsWith("action://")){
                String[] paras = url.split("\\/\\/");
                if (paras == null || paras.length<2){return true;}
                String action = paras[1].split("\\?")[0].trim();
                Log.d(TAG,"Action is :" + action);
                if (action.equals("newIntent")){
                    String activityName = paras[1].split("\\?")[1].trim()+"Activity";
                    String packName = getActivity().getPackageName();
                    try {
                        Log.i(TAG,packName + "." + activityName);
                        Class activity = Class.forName(packName + "." + activityName);
                        Intent i = new Intent(getActivity(), activity);
                        i.putExtra("hideLeftPan",true);
                        startActivity(i);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return true;
                    }
                } else if (action.equals("newToast")){
                    String msg = paras[1].split("\\?")[1].trim();
                    try {
                        msg = URLDecoder.decode(msg, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Tools.showToastMid(getActivity(),msg);
                } else if (action.equals("showLeftDrawer")) {
                    Intent i = new Intent("com.occs.openLeftDrawer");
                    getActivity().sendBroadcast(i);
                } else if (action.equals("showWeb")){
                    String webArgs = url.split("\\?")[1];
                    try {
                        webArgs = URLDecoder.decode(webArgs, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,webArgs.split("\\|")[0]);
                    Intent i = new Intent(getActivity(),OccsSingleWebPageActivity.class);
                    i.putExtra("title", webArgs.split("\\|")[0]);
                    i.putExtra("baseurl", webArgs.split("\\|")[1]);
                    i.putExtra("arguments", "");

                    getActivity().startActivity(i);
                }
                return  true;
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
        }
    }
}
