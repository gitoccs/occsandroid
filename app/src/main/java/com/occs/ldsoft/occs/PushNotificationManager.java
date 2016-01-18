package com.occs.ldsoft.occs;

import android.content.Context;
import android.util.Log;

import com.occs.ldsoft.occs.OccsDatabaseHelper.NoteCursor;

/**
 * Created by yeliu on 15/10/12.
 */
public class PushNotificationManager {
    private static PushNotificationManager manager;
    private OccsDatabaseHelper mHelper;

    public OccsDatabaseHelper getHelper(Context c) {
        if (mHelper == null){
            mHelper = new OccsDatabaseHelper(c);
        }
        return mHelper;
    }

    public static PushNotificationManager getInstance(){
        if (manager == null){
            manager = new PushNotificationManager();
        }
        return manager;
    }

    public long insertNote(PushNote note, Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.insertNote(note);
    }

    public NoteCursor queryNotes(Context c) {
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.queryNotes();
    }

    public NoteCursor queryNoteAt(Context c, long noteid){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.queryNoteAt(noteid);
    }

    public NoteCursor queryLastNote(Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.queryLastNote();
    }

    public int updateIsSeen(Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.updateNotesIsSeen();
    }

    public int getNoSeenCount(Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        NoteCursor wrapper = mHelper.queryNotesUnseen();
        return wrapper.getCount();
    }

    public long getAllcount(Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.queryNotesCount();
    }

    public int deleteNoteAt(Context c,long noteid){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.deleteNote(noteid);
    }

    public int deleteAll(Context c){
        mHelper = PushNotificationManager.getInstance().getHelper(c);
        return mHelper.deleteAllNotes();
    }
}
