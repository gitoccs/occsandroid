package com.occs.ldsoft.occs;

import java.text.ParseException;

/**
 * Created by yeliu on 15/7/28.
 */
public class Gongdan {
    private long id;
    private String workid;
    private String username;
    private long time;
    private long deadline;
    private String title;
    private String type;
    private String cost;
    private String project;
    private String period;
    private long is_seen;
    private String status;

    public Gongdan(String gongdan_id, String time, String deadline, String type, String title,
                    String cost, String project, String period, String status,
                   long isSeen, long id) throws ParseException {
        this.id = id;
        this.workid = gongdan_id;
        this.time = Tools.stringToLong(time, "yyyy-MM-dd HH:mm:ss");
        this.deadline = Tools.stringToLong(deadline, "yyyy-MM-dd HH:mm:ss");
        this.type = type;
        this.title = title;
        this.project = project;
        this.status = status;
        this.period = period;
        this.cost = cost;
        this.is_seen = isSeen;
        if ( !Person.getPerson().getName().isEmpty()) {
            this.username = Person.getPerson().getName();
        }else{
            this.username = "";
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(long is_seen) {
        this.is_seen = is_seen;
    }

    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
