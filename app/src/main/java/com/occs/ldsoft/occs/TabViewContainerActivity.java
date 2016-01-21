package com.occs.ldsoft.occs;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.occs.ldsoft.occs.WebFunctionHelper.FetchAllWorkCount;
import static com.occs.ldsoft.occs.WebFunctionHelper.FetchPersonOrComInfo;

public class TabViewContainerActivity extends TabFragmentActivity {

    private FragmentTabHost mTabHost;
    private View indicator = null;
    private static final String TAG = "TabViewContainerActivity";
    public int contentColor = 0;
    public ArrayList<TabObject> tabObjects = new ArrayList<TabObject>();
    public int indicatorLayout = R.layout.tab_indicator1;
    public int initSelectTab;
    private String memberType;

    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerFrame;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isLoadingWeb = false;

    private int typeNumber;
    private String key;
    private String password;
    private String name;
    private String email;
    private String mobile;

    public BroadcastReceiver openLeftDrawerReceiver;
    public BroadcastReceiver openActivityReceiver;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout_container);
        memberType = getIntent().getStringExtra("type");
        contentColor = getColorFromType(memberType);
        String[] titleAry = getIntent().getStringArrayExtra("titleArray");
        String[] imageAry = getIntent().getStringArrayExtra("imageArray");
        initSelectTab = getIntent().getIntExtra("selectTab", 0);

        key = getIntent().getStringExtra("key");
        mobile = getIntent().getStringExtra("phonenumber");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        typeNumber = getIntent().getIntExtra("typenumber", 0);

        Log.d(TAG, key + "  " + mobile + "  " + password);

        if (!FetchPersonOrComInfo.isLoadingWeb && Tools.isNetworkConnected(getBaseContext())) {
            new FetchPersonOrComInfo(Person.getPerson().getTypeNameFromInt(typeNumber),
                    key,mobile,name,email,password, typeNumber,TabViewContainerActivity.this) {
                @Override
                public void successFunction() {
                    Log.i(TAG, "loading info data successed !");
                    if (fragment != null){
                        PersonLeftDrawerFragment pldf = (PersonLeftDrawerFragment)fragment;
                        try {
                            pldf.updatePersonIcon();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.execute();
            if(Person.getPerson().getKey() != null && !Person.getPerson().getKey().isEmpty()){
                new FetchAllWorkCount() {
                    @Override
                    public void getResultFunction(ArrayList<String> ary) {
                        if (fragment != null){
                            PersonLeftDrawerFragment pldf = (PersonLeftDrawerFragment)fragment;
                            Log.d(TAG,ary.get(0));
                            pldf.gongdanAll.setText(ary.get(0));
                            pldf.gongdanBidding.setText(ary.get(1));
                            pldf.gongdanOnGoing.setText(ary.get(2));
                            pldf.gongdanFinish.setText(ary.get(3));
                        }
                    }
                }.execute();
            }
            updateOcoinTextView();
        }

//        ActionBar bar = getActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(contentColor)));
//        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        bar.setCustomView(R.layout.actionbar_abs_layout);
//        bar.setDisplayHomeAsUpEnabled(true);
//        titleTxtView = (TextView)bar.getCustomView().findViewById(R.id.actionbar_title);

        // mTabHost = new FragmentTabHost(this);
        // mTabHost.setup(this, getSupportFragmentManager(),
        // R.id.menu_settings);

        for (int i=0; i< titleAry.length; i++){
            boolean isSe = false;
            if (initSelectTab == i) {isSe = true;};
            TabObject tab = new TabObject(titleAry[i], imageAry[i], isSe);
            tabObjects.add(tab);
        }

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (int i=0; i< tabObjects.size(); i++){
            addOccsWebTab(tabObjects.get(i));
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                changeTabSpecState(s);
                Tools.setCurTab(s, getApplicationContext());
            }
        });
        mTabHost.setCurrentTab(initSelectTab);

        // --------------------------   drawer layout code  ----------------------------

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFrame = (FrameLayout) findViewById(R.id.left_drawer);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.left_drawer);

        if (fragment == null){
            fragment = PersonLeftDrawerFragment.newInstance(contentColor);
            fm.beginTransaction().add(R.id.left_drawer, fragment).commit();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (fragment != null){
                    PersonLeftDrawerFragment pldf = (PersonLeftDrawerFragment)fragment;
                    try {
                        pldf.updatePersonIcon();
                        if(Person.getPerson().getKey() != null && !Person.getPerson().getKey().isEmpty()){
                            new FetchAllWorkCount() {
                                @Override
                                public void getResultFunction(ArrayList<String> ary) {
                                    if (fragment != null){
                                        PersonLeftDrawerFragment pldf = (PersonLeftDrawerFragment)fragment;
                                        Log.d(TAG,ary.get(0));
                                        pldf.gongdanAll.setText(ary.get(0));
                                        pldf.gongdanBidding.setText(ary.get(1));
                                        pldf.gongdanOnGoing.setText(ary.get(2));
                                        pldf.gongdanFinish.setText(ary.get(3));
                                    }
                                }
                            }.execute();
                        }
                        updateOcoinTextView();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        IntentFilter receiverFilter = new IntentFilter("com.occs.openLeftDrawer");
        openLeftDrawerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDrawerLayout.openDrawer(mDrawerFrame);
            }
        };
        registerReceiver(openLeftDrawerReceiver, receiverFilter);

        IntentFilter openActivityFilter = new IntentFilter("com.occs.openActivity");
        openActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                removeStickyBroadcast(intent);
                Log.i(TAG,"com.occs.openActivity receiver");
                String packName = context.getPackageName();
                try {
                    String topClass = getMyTopActivity();
                    String goClass = intent.getStringExtra("action");
                    if(!goClass.contains(topClass)){
                        if(goClass.contains("OccsSingleWebPageActivity")){
                            Class cls = Class.forName(packName + intent.getStringExtra("action"));
                            final Intent i = new Intent(TabViewContainerActivity.this, cls);
                            i.putExtra("arguments","&id=" + intent.getStringExtra("id") + "&action=" + intent.getStringExtra("pushAction"));
                            if (intent.getStringExtra("baseurl").equals("getUrlFromAPI")){
                                new WebFunctionHelper.FetchAppMessage(intent.getStringExtra("id")){

                                    @Override
                                    public void getResultFunction(HashMap<String, String> map) {
                                        try {
                                            String str = map.get("push_identity");
                                            str.trim();
                                            JSONObject extra = new JSONObject(str);
                                            String url = extra.getString("url");
                                            i.putExtra("baseurl", "http://" + url);
                                            i.putExtra("title", "通知浏览");
                                            startActivity(i);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.execute();
                            }else{
                                i.putExtra("baseurl",intent.getStringExtra("baseurl"));
                                i.putExtra("title", "通知浏览");
                                startActivity(i);
                            }
                            Log.d(TAG, "open activity :" + intent.getStringExtra("action"));
                        }else{
                            Class cls = Class.forName(packName + intent.getStringExtra("action"));
                            String pushAction = intent.getStringExtra("pushAction");
                            String noteid = intent.getStringExtra("id");

                            Intent i = new Intent(TabViewContainerActivity.this, cls);
                            if(pushAction != null && pushAction.contains("工单详情")){
                                new FetchNoteWorkId(noteid, false).execute();
                            }else{
                                startActivity(i);
                            }
                            Log.d(TAG,"open activity :" + intent.getStringExtra("action"));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(openActivityReceiver, openActivityFilter);
        Log.i(TAG, "set up com.occs.openActivity receiver");
    }

    public List<ActivityManager.RunningTaskInfo> listActivitys(){
        ActivityManager activityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        return runningTasks;
    }

    public String getMyTopActivity(){
        List<ActivityManager.RunningTaskInfo> runningTasks = listActivitys();
        String topActivity = runningTasks.get(0).topActivity.getClassName();
        String[] topActClass = topActivity.split("\\.");
        return topActClass[topActClass.length-1];
    }

    private void updateOcoinTextView() {
        if (fragment != null){
            PersonLeftDrawerFragment pldf = (PersonLeftDrawerFragment)fragment;
            pldf.oCoinTextView.setText("O币：" + ((int)Float.parseFloat(Person.getPerson().getOcoin())+(int)Float.parseFloat(Person.getPerson().getOcoinCash())));
        }
    }

    /** Swaps fragments in the main content view */
    public void selectItem(int position) {
        Log.d(TAG,"position is : " + position);
        switch (position){
            case 0:
                Intent i = new Intent(this, AccountModifyActivity.class);
                i.putExtra("color", contentColor);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, MyAccountWebActivity.class);
                i.putExtra("color", contentColor);
                startActivity(i);
                break;
            case 2:
                i = new Intent(this, HelperActivity.class);
                i.putExtra("color", contentColor);
                startActivity(i);
                break;
            case 3:
                i = new Intent(this, ContactServiceActivity.class);
                i.putExtra("color", contentColor);
                startActivity(i);
                break;
            case 4:
                i = new Intent(this, AboutActivity.class);
                i.putExtra("color", contentColor);
                startActivity(i);
                break;
            default:
                break;
        }
        listActivitys();
        // Create a new fragment and specify the planet to show based on position
        // Highlight the selected item, update the title, and close the drawer

//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerFrame);
    }

    private int getColorFromType(String memberType) {
        switch (memberType){
            case "个人":
                return R.color.btn_blue_normal;
            case "软企":
                return R.color.btn_cyan_normal;
            case "企业":
                return R.color.btn_orange_normal;
        }
        return 0;
    }


    private void changeTabSpecState(String s) {
        for (int i=0; i<mTabHost.getTabWidget().getTabCount(); i++){
            boolean isSelected = false;
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.web_tab_label);
            String txt = (String) tv.getText();
            if (txt.equals(s)){
                isSelected = true;
            }
            ImageView iv = (ImageView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.web_tab_image);
            TabObject tabObject = tabObjects.get(i);
            String onImage = tabObject.getOnImage();
            int imgInt;
            if(isSelected){
                imgInt = Tools.getPackageDrawable(this, onImage);
                tv.setTextColor(getResources().getColor(contentColor));
            }else{
                String[] words = onImage.split("\\_");
                imgInt = Tools.getPackageDrawable(this, words[0] + "_0");
                tv.setTextColor(getResources().getColor(R.color.light_gray));
            }
            iv.setImageResource(imgInt);
        }
    }

    public void addOccsWebTab(TabObject tab){

        String title = tab.getTitle();
        int layoutId = indicatorLayout;
        String onImage = tab.getOnImage();
        boolean isOn = tab.isOn();

        Bundle b = new Bundle();
        b.putString("key", title);
        indicator = getIndicatorView(title, layoutId, onImage, isOn);

        try {
            mTabHost.addTab(mTabHost.newTabSpec(title)
                    .setIndicator(indicator), getFragmentFromString(title), b);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public View getIndicatorView(String name, int layoutId, String onImage, boolean isOn) {

        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = (TextView) v.findViewById(R.id.web_tab_label);
        tv.setText(name);
        ImageView iv = (ImageView) v.findViewById(R.id.web_tab_image);
        int imgInt = 0;
        if (isOn) {
            imgInt = Tools.getPackageDrawable(this, onImage);
            tv.setTextColor(getResources().getColor(contentColor));
        }else{
            String[] words = onImage.split("\\_");
            imgInt = Tools.getPackageDrawable(this, words[0] + "_0");
            tv.setTextColor(getResources().getColor(R.color.light_gray));
        }

        iv.setImageResource(imgInt);

        DrawLine line = new DrawLine(this);
        View layout = v.findViewById(R.id.indicator_relativelayout);
        ((RelativeLayout) layout).addView(line, 0);

        return v;
    }

    public Class getFragmentFromString(String classStr) throws ClassNotFoundException {
        String packName = getPackageName();
        switch (classStr){
            case "我的首页":
                return Class.forName(packName + ".OccsWebPersonMainFragment");
            case "工单市场":
                return Class.forName(packName + ".OccsWebGongdanFragment");
            case "岗位认证":
                return Class.forName(packName + ".OccsWebJobFragment");
            case "需求市场":
                return Class.forName(packName + ".OccsWebDemandFragment");
            case "发布需求":
                return Class.forName(packName + ".OccsWebSendDemandFragment");
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(openLeftDrawerReceiver);
        unregisterReceiver(openActivityReceiver);
        super.onDestroy();
    }

    @Override
    protected void backBtnAction() {
        OccsWebContentFragment fragment = (OccsWebContentFragment) getSupportFragmentManager()
                .findFragmentByTag(tabObjects.get(mTabHost.getCurrentTab()).getTitle());
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
            return;
        }
        if (fragment.curPage > 1){
            fragment.curPage--;
            String functionStr = "javascript:setpage" + fragment.curPage + "()";
            fragment.webView.loadUrl(functionStr);
        }else {
            new CountDownTimer(3000, 1000) {
            //CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    returnCount = 0;
                }
            }.start();
            if (returnCount == 0){
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                returnCount++;
            }else {
//                Intent i = new Intent(this, LogInActivity.class);
//                startActivity(i);
                finish();
            }
        }
    }

    private class FetchNoteWorkId extends WebFunctionHelper.FetchAppMessage {

        public boolean isSave;

        public FetchNoteWorkId(String noteID, boolean isSave) {
            super(noteID);
            this.isSave = isSave;
        }

        @Override
        public void getResultFunction(HashMap<String, String> map) {
            try {
                JSONObject extra = new JSONObject(map.get("push_identity"));
                String workid = extra.getString("workid");
                if (Tools.isNetworkConnected(getApplicationContext()) && !FetchProjectDetails.isLoadingWeb){
                    new FetchProjectDetails(workid,isSave,TabViewContainerActivity.this).execute();
                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class FetchProjectDetails extends WebFunctionHelper.FetchGongdanDetail {

        public boolean isSave;
        public Context context;

        public FetchProjectDetails(String workid, boolean isSave, Context c) {
            super(workid);
            this.isSave = isSave;
            this.context = c;
        }

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
                if (isSave){
                    Gongdan gongdan = new Gongdan(workid, time, deadline, type, title, cost, project, period,staus,0,-1);
                    long num = GongdanSuitableManager.getInstance().insertGongdan(gongdan, context.getApplicationContext());
                    Intent i = new Intent("com.occs.sysNoteReceiver");
                    context.sendBroadcast(i);
                }else{
                    Intent i = new Intent(context, GongdanSingleWebActivity.class);
                    i.putExtra("urlArguments","&workid=" + workid);
                    context.startActivity(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
