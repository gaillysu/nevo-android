package com.medcorp.nevo.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by gaillysu on 15/11/17.
 */
public class Steps {
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
     * one day's total steps, include walk and run
     */
    public static final String fSteps = "Steps";
    @DatabaseField
    private int Steps;

    /**
     * one day's total walk steps
     */
    public static final String fWalkSteps = "WalkSteps";
    @DatabaseField
    private int WalkSteps;

    /**
     * one day's total run steps
     */
    public static final String fRunSteps = "RunSteps";
    @DatabaseField
    private int RunSteps;

    /**
     * one day's total distance ,unit is meter.
     */
    public static final String fDistance = "Distance";
    @DatabaseField
    private int Distance;

    /**
     * one day's total distance ,unit is calorie
     */
    public static final String fCalories = "Calories";
    @DatabaseField
    private int Calories;


    /**
     * one day's hourly steps, such as: int HourlySteps[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlySteps = "HourlySteps";
    @DatabaseField
    private String HourlySteps;

    /**
     * one day's hourly distance, such as: int HourlyDistance[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlyDistance = "HourlyDistance";
    @DatabaseField
    private String HourlyDistance;

    /**
     * one day's hourly calories, such as: int HourlyCalories[n] = {0,2000,3000,...,1000}, here "n" is fixed to 24
     * array to string  is "[0,2000,3000,...,1000]" that will be saved to the table
     */
    public static final String fHourlyCalories = "HourlyCalories";
    @DatabaseField
    private String HourlyCalories;


    /**
     * match Zone duration, unit is minute
     */
    public static final String fInZoneTime = "InZoneTime";
    @DatabaseField
    private int InZoneTime;

    /**
     * out of Zone duration, unit is minute
     */
    public static final String fOutZoneTime = "OutZoneTime";
    @DatabaseField
    private int OutZoneTime;

    /**
     * no activity duration, unit is minute
     */
    public static final String fNoActivityTime = "NoActivityTime";
    @DatabaseField
    private int NoActivityTime;

    /**
     * goal result, reached or not, boolean type
     */
    public static final String fGoalReached = "GoalReached";
    @DatabaseField
    private boolean GoalReached;

    /**
     * remarks field, save extend  infomation
     * it is a Json string
     */

    public static final String fRemarks = "Remarks";
    @DatabaseField
    private String Remarks;
}
