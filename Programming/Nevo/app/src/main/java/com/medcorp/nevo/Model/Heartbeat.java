package com.medcorp.nevo.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class Heartbeat {

    private final int iD;

    private final int userID;

    private final long createdDate;

    private long date;

    private int maxHrm;

    private int avgHrm;

    private String remarks;

    public Heartbeat(int iD, int userID, long createdDate, long date, int maxHrm, int avgHrm, String remarks) {
        this.iD = iD;
        this.userID = userID;
        this.createdDate = createdDate;
        this.date = date;
        this.maxHrm = maxHrm;
        this.avgHrm = avgHrm;
        this.remarks = remarks;
    }

    public Heartbeat(int iD, int userID, long createdDate) {
        this.iD = iD;
        this.userID = userID;
        this.createdDate = createdDate;
    }

    public int getiD() {
        return iD;
    }

    public int getUserID() {
        return userID;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMaxHrm() {
        return maxHrm;
    }

    public void setMaxHrm(int maxHrm) {
        this.maxHrm = maxHrm;
    }

    public int getAvgHrm() {
        return avgHrm;
    }

    public void setAvgHrm(int avgHrm) {
        this.avgHrm = avgHrm;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
