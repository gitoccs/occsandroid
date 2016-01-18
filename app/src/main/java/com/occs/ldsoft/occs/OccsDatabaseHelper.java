package com.occs.ldsoft.occs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * Created by yeliu on 15/10/12.
 */
public class OccsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "note.sqlite";
    private static final int VERSION = 4;

    private static final String TABLE_NOTIFICATION = "note";
    private static final String COLUMN_NOTIFICATION_INDEX = "_id";
    private static final String COLUMN_NOTIFICATION_DATE = "time";
    private static final String COLUMN_NOTIFICATION_ID = "note_id";
    private static final String COLUMN_NOTIFICATION_TYPE = "type";
    private static final String COLUMN_NOTIFICATION_ACTION = "action_code";
    private static final String COLUMN_NOTIFICATION_ISSEEN = "is_seen";
    private static final String COLUMN_NOTIFICATION_USERNAME = "username";
    private static final String COLUMN_NOTIFICATION_MESSAGE = "message";

    private static final String TABLE_GONGDAN = "gongdan";
    private static final String COLUMN_GONGDAN_INDEX = "_id";
    private static final String COLUMN_GONGDAN_ID = "gongdan_id";
    private static final String COLUMN_GONGDAN_DATE = "time";
    private static final String COLUMN_GONGDAN_TITLE = "title";
    private static final String COLUMN_GONGDAN_PROJECT = "project";
    private static final String COLUMN_GONGDAN_COST = "cost";
    private static final String COLUMN_GONGDAN_TYPE = "type";
    private static final String COLUMN_GONGDAN_PREIOD = "period";
    private static final String COLUMN_GONGDAN_DEADLINE = "deadline";
    private static final String COLUMN_GONGDAN_STATUS = "status";
    private static final String COLUMN_GONGDAN_ISSEEN = "is_seen";
    private static final String COLUMN_GONGDAN_USERNAME = "username";

    private static final String TAG = "NoteDataHelper";

    public OccsDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG ,"db created");
        createDatabase(db);
    }

    private void createDatabase(SQLiteDatabase db){
        String sqlStr1 = "create table note ("
                + "_id integer primary key autoincrement, username varchar(100), time integer, "
                + "note_id integer, type varchar(100), "
                + "action_code integer, is_seen integer, message varchar(100))";
        db.execSQL(sqlStr1);

        String sqlStr2 = "create table gongdan ("
                + "_id integer primary key autoincrement, title varchar(100), time integer, deadline integer,"
                + " username varchar(100), gongdan_id varchar(100), type varchar(100), project varchar(100), "
                + "period varchar(100), cost varchar(100), is_seen integer, status varchar(100))";
        db.execSQL(sqlStr2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, i + "    " + i1);
        reset(sqLiteDatabase);
    }

    public void reset(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GONGDAN);
        createDatabase(db);
    }

    public NoteCursor queryNotes() {
        Cursor wrapped = getReadableDatabase().query(TABLE_NOTIFICATION,
                null,COLUMN_NOTIFICATION_USERNAME + "=? OR " + COLUMN_NOTIFICATION_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""},null,null,COLUMN_NOTIFICATION_INDEX + " DESC");
        return new NoteCursor(wrapped);
    }

    public GongdanCursor queryGongdans() {
        Cursor wrapped = getReadableDatabase().query(TABLE_GONGDAN,
                null,COLUMN_GONGDAN_USERNAME + "=? OR " + COLUMN_GONGDAN_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""},null,null,COLUMN_GONGDAN_INDEX + " DESC");
        return new GongdanCursor(wrapped);
    }

    public long queryNotesCount() {
        Cursor wrapped = getReadableDatabase().query(TABLE_NOTIFICATION,
                null,COLUMN_NOTIFICATION_USERNAME + "=? OR " + COLUMN_NOTIFICATION_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""},null,null,COLUMN_NOTIFICATION_INDEX + " DESC");
        return wrapped.getCount();
    }

    public long queryGongdanCount() {
        Cursor wrapped = getReadableDatabase().query(TABLE_GONGDAN,
                null,COLUMN_GONGDAN_USERNAME + "=? OR " + COLUMN_GONGDAN_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""},null,null,COLUMN_GONGDAN_INDEX + " DESC");
        return wrapped.getCount();
    }

    public NoteCursor queryLastNote() {
        Cursor wrapped = getReadableDatabase().query(TABLE_NOTIFICATION,
                null, COLUMN_NOTIFICATION_USERNAME + "=? OR " + COLUMN_NOTIFICATION_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""}, null, null, COLUMN_NOTIFICATION_INDEX + " DESC LIMIT 1;");
        return new NoteCursor(wrapped);
    }

    public GongdanCursor queryLastGongdan() {
        Cursor wrapped = getReadableDatabase().query(TABLE_GONGDAN,
                null, COLUMN_GONGDAN_USERNAME + "=? OR " + COLUMN_GONGDAN_USERNAME + "=?",
                new String[]{Person.getPerson().getName(), ""}, null, null, COLUMN_GONGDAN_INDEX + " DESC LIMIT 1;");
        return new GongdanCursor(wrapped);
    }

    public NoteCursor queryNotesUnseen() {
        Cursor wrapped = getReadableDatabase().query(TABLE_NOTIFICATION,
                null, COLUMN_NOTIFICATION_ISSEEN + "=? AND (" + COLUMN_NOTIFICATION_USERNAME + "=? OR " + COLUMN_NOTIFICATION_USERNAME + "=?)",
                new String[]{"0", Person.getPerson().getName(),""}, null, null, null);
        return new NoteCursor(wrapped);
    }

    public GongdanCursor queryGongdanUnseen() {
        Cursor wrapped = getReadableDatabase().query(TABLE_GONGDAN,
                null, COLUMN_GONGDAN_ISSEEN + "=? AND (" + COLUMN_GONGDAN_USERNAME + "=? OR " + COLUMN_GONGDAN_USERNAME + "=?)",
                new String[]{"0", Person.getPerson().getName(),""}, null, null, null);
        return new GongdanCursor(wrapped);
    }

    public int updateNotesIsSeen(){
        String where = "is_seen=? AND (username=? OR username=?)";
        String[] whereArgs = new String[]{"0", Person.getPerson().getName(),""};
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTIFICATION_ISSEEN,1);
        return getWritableDatabase().update(TABLE_NOTIFICATION,cv, where, whereArgs);
    }

    public int updateGongdanIsSeen(){
        String where = "is_seen=? AND (username=? OR username=?)";
        String[] whereArgs = new String[]{"0", Person.getPerson().getName(),""};
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_GONGDAN_ISSEEN,1);
        return getWritableDatabase().update(TABLE_GONGDAN,cv, where, whereArgs);
    }

    public NoteCursor queryNoteAt(long noteId) {
        String[] args = {String.valueOf(noteId), Person.getPerson().getName(),""};
        Cursor wrapped = getReadableDatabase().query(TABLE_NOTIFICATION,null,"_id=? AND (username=? OR username=?)",args,null,null,null);
        return new NoteCursor(wrapped);
    }

    public GongdanCursor queryGongdanAt(long gongdanID) {
        String[] args = {String.valueOf(gongdanID), Person.getPerson().getName(),""};
        Cursor wrapped = getReadableDatabase().query(TABLE_GONGDAN,null,"_id=? AND (username=? OR username=?)",args,null,null,null);
        return new GongdanCursor(wrapped);
    }

    public int deleteNote(long noteId) {
        String[] args = {String.valueOf(noteId), Person.getPerson().getName(), ""};
        return getWritableDatabase().delete(TABLE_NOTIFICATION, "_id=? AND (username=? OR username=?)",args);
    }

    public int deleteGongdan(long gongdanID) {
        String[] args = {String.valueOf(gongdanID), Person.getPerson().getName(), ""};
        return getWritableDatabase().delete(TABLE_GONGDAN, "_id=? AND (username=? OR username=?)",args);
    }

    public int deleteAllNotes(){
        String[] args = {Person.getPerson().getName(),""};
        return getWritableDatabase().delete(TABLE_NOTIFICATION, "username=? OR username=?",args);
    }

    public int deleteAllGongdans(){
        String[] args = {Person.getPerson().getName(),""};
        return getWritableDatabase().delete(TABLE_GONGDAN, "username=? OR username=?",args);
    }

    public long insertNote(PushNote note){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTIFICATION_ID, note.getNote_id());
        cv.put(COLUMN_NOTIFICATION_USERNAME, note.getUsername());
        cv.put(COLUMN_NOTIFICATION_DATE, note.getTime());
        cv.put(COLUMN_NOTIFICATION_ACTION, note.getAction_code());
        cv.put(COLUMN_NOTIFICATION_TYPE, note.getType());
        cv.put(COLUMN_NOTIFICATION_ISSEEN, note.getIs_seen());
        cv.put(COLUMN_NOTIFICATION_MESSAGE, note.getMessage());
        return getWritableDatabase().insert(TABLE_NOTIFICATION, null, cv);
    }

    public long insertGongdan(Gongdan gongdan){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_GONGDAN_ID, gongdan.getWorkid());
        cv.put(COLUMN_GONGDAN_USERNAME, gongdan.getUsername());
        cv.put(COLUMN_GONGDAN_DATE, gongdan.getTime());
        cv.put(COLUMN_GONGDAN_DEADLINE, gongdan.getDeadline());
        cv.put(COLUMN_GONGDAN_TYPE, gongdan.getType());
        cv.put(COLUMN_GONGDAN_ISSEEN, gongdan.getIs_seen());
        cv.put(COLUMN_GONGDAN_TITLE, gongdan.getTitle());
        cv.put(COLUMN_GONGDAN_PROJECT, gongdan.getProject());
        cv.put(COLUMN_GONGDAN_PREIOD, gongdan.getPeriod());
        cv.put(COLUMN_GONGDAN_COST, gongdan.getCost());
        cv.put(COLUMN_GONGDAN_STATUS, gongdan.getStatus());
        return getWritableDatabase().insert(TABLE_GONGDAN,null,cv);
    }

    public static class NoteCursor extends CursorWrapper {

        public NoteCursor(Cursor c) {
            super(c);
        }

        public PushNote getNote() throws ParseException {
            if (isBeforeFirst() || isAfterLast()){
                return null;
            }
            long note_index = getLong(getColumnIndex(COLUMN_NOTIFICATION_INDEX));
            long note_id = getLong(getColumnIndex(COLUMN_NOTIFICATION_ID));
            long note_time = getLong(getColumnIndex(COLUMN_NOTIFICATION_DATE));
            String time = Tools.longToString(note_time, "yyyy-MM-dd HH:mm:ss");
            long note_action = getLong(getColumnIndex(COLUMN_NOTIFICATION_ACTION));
            long note_is_seen = getLong(getColumnIndex(COLUMN_NOTIFICATION_ISSEEN));
            String note_type = getString(getColumnIndex(COLUMN_NOTIFICATION_TYPE));
            String message = getString(getColumnIndex(COLUMN_NOTIFICATION_MESSAGE));
            boolean isAnybody = getString(getColumnIndex(COLUMN_NOTIFICATION_USERNAME)).equals("");
            Log.d(TAG,message);
            return new PushNote(String.valueOf(note_id),time,note_type,
                    String.valueOf(note_action),message, note_is_seen, note_index, isAnybody);
        }
    }

    public static class GongdanCursor extends CursorWrapper {

        public GongdanCursor(Cursor c) {
            super(c);
        }

        public Gongdan getGongdan() throws ParseException {
            if (isBeforeFirst() || isAfterLast()){
                return null;
            }
            long gongdan_index = getLong(getColumnIndex(COLUMN_GONGDAN_INDEX));
            String gongdan_id = getString(getColumnIndex(COLUMN_GONGDAN_ID));
            long gongdan_time = getLong(getColumnIndex(COLUMN_GONGDAN_DATE));
            String time = Tools.longToString(gongdan_time, "yyyy-MM-dd HH:mm:ss");
            long gongdan_deadline = getLong(getColumnIndex(COLUMN_GONGDAN_DEADLINE));
            String deadline = Tools.longToString(gongdan_deadline, "yyyy-MM-dd HH:mm:ss");
            String project = getString(getColumnIndex(COLUMN_GONGDAN_PROJECT));
            long note_is_seen = getLong(getColumnIndex(COLUMN_GONGDAN_ISSEEN));
            String gongdan_type = getString(getColumnIndex(COLUMN_GONGDAN_TYPE));
            String cost = getString(getColumnIndex(COLUMN_GONGDAN_COST));
            String title = getString(getColumnIndex(COLUMN_GONGDAN_TITLE));
            String period = getString(getColumnIndex(COLUMN_GONGDAN_PREIOD));
            String status = getString(getColumnIndex(COLUMN_GONGDAN_STATUS));
            Gongdan gongdan = new Gongdan(gongdan_id, time, deadline, gongdan_type,title, cost,
                    project, period, status, note_is_seen, gongdan_index);
            return gongdan;
        }
    }
}
