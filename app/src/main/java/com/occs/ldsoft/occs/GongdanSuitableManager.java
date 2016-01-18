package com.occs.ldsoft.occs;

import android.content.Context;
import com.occs.ldsoft.occs.OccsDatabaseHelper.GongdanCursor;

/**
 * Created by yeliu on 15/11/27.
 */
public class GongdanSuitableManager {
    private static GongdanSuitableManager manager;
    private OccsDatabaseHelper mHelper;

    public OccsDatabaseHelper getHelper(Context c) {
        if (mHelper == null){
            mHelper = new OccsDatabaseHelper(c);
        }
        return mHelper;
    }

    public static GongdanSuitableManager getInstance(){
        if (manager == null){
            manager = new GongdanSuitableManager();
        }
        return manager;
    }

    public long insertGongdan(Gongdan gongdan, Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.insertGongdan(gongdan);
    }

    public GongdanCursor queryGongdans(Context c) {
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.queryGongdans();
    }

    public GongdanCursor queryGongdanAt(Context c, long gongdanid){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.queryGongdanAt(gongdanid);
    }

    public GongdanCursor queryLastGongdan(Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.queryLastGongdan();
    }

    public int updateIsSeen(Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.updateGongdanIsSeen();
    }

    public int getNoSeenCount(Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        GongdanCursor wrapper = mHelper.queryGongdanUnseen();
        return wrapper.getCount();
    }

    public long getAllcount(Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.queryGongdanCount();
    }

    public int deleteGongdanAt(Context c,long gongdanid){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.deleteGongdan(gongdanid);
    }

    public int deleteAll(Context c){
        mHelper = GongdanSuitableManager.getInstance().getHelper(c);
        return mHelper.deleteAllGongdans();
    }
}
