package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class HeartbeatDAO {
    /**
     * field name and initialize value, Primary field
     */
    public static final String fID = "ID";
    @DatabaseField(generatedId = true)
    private int ID;

    /**
     * which user ID
     */
    public static final String fUserID = "UserID";
    @DatabaseField
    private int UserID;

    /**
     * created date
     */
    public static final String fCreatedDate = "CreatedDate";
    @DatabaseField
    private long CreatedDate;

    /**
     *  date, one day which is Year/Month/Day
     */
    public static final String fDate = "Date";
    @DatabaseField
    private long Date;


    /**
     * one day's Max heartrate, unit is bpm
     */
    public static final String fMaxHrm = "MaxHrm";
    @DatabaseField
    private int MaxHrm;

    /**
     * one day's Avg heartrate, unit is bpm
     */
    public static final String fAvgHrm = "AvgHrm";
    @DatabaseField
    private int AvgHrm;

    /**
     * remarks field, save extend infomation
     * it is a Json string
     */

    public static final String fRemarks = "Remarks";
    @DatabaseField
    private String Remarks;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public long getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        CreatedDate = createdDate;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public int getMaxHrm() {
        return MaxHrm;
    }

    public void setMaxHrm(int maxHrm) {
        MaxHrm = maxHrm;
    }

    public int getAvgHrm() {
        return AvgHrm;
    }

    public void setAvgHrm(int avgHrm) {
        AvgHrm = avgHrm;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
