package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class Heartbeat {
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
}
