package com.occs.ldsoft.occs;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yeliu on 15/10/12.
 */
public class PushNote {
    private long id;
    private long note_id;
    private long time;
    private String username;
    private String type;
    private long action_code;
    private long is_seen;
    private String message;

    public PushNote(String note_id, String time, String type, String action_code,
                    String message, long isSeen, long id, boolean isAnybody) throws ParseException {
        this.id = id;
        this.note_id = Long.parseLong(note_id);
        this.time = Tools.stringToLong(time, "yyyy-MM-dd HH:mm:ss");
        this.type = type;
        this.action_code = Long.parseLong(action_code);
        this.message = message;
        this.is_seen = isSeen;
        Log.d("Occs Web person main", String.valueOf(Person.getPerson()));
        if (isAnybody) {
            this.username = "";
        }else if (Person.getPerson().getPassword() == null){
            this.username = "";
        }else if ( !Person.getPerson().getPassword().isEmpty()) {
            this.username = Person.getPerson().getName();
        }else{
            this.username = "";
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAction_code() {
        return action_code;
    }

    public void setAction_code(long action_code) {
        this.action_code = action_code;
    }

    public long getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(long is_seen) {
        this.is_seen = is_seen;
    }
}
