package com.medcorp.model;

import java.util.Date;

public class Steps implements Comparable<Steps>{

    private int iD = (int) Math.floor(Math.random()*Integer.MAX_VALUE);

    private String validicRecordID = "0";

    private String nevoUserID = "1";

    private long createdDate = new Date().getTime();

    private long date = new Date().getTime();

    private int steps = 1000;

    private int walkSteps = 400;

    private int runSteps = 600;

    private int distance = 1000;

    private int walkDistance = 400;

    private int runDistance = 600;

    private int walkDuration = 100;

    private int runDuration = 100;

    private int calories = 0;

    private String hourlySteps = "[1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3]";

    private String hourlyDistance = "[1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3]";

    private String hourlyCalories = "[1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3]";

    private int inZoneTime = 100;

    private int outZoneTime = 1000;

    private int noActivityTime = 1000;

    private int goal = 5000;

    private String remarks;


    public Steps(long createdDate) {
        this.createdDate = createdDate;
    }

    public Steps( long createdDate, long date, int steps, int walkSteps, int runSteps, int distance, int calories, String hourlySteps, String hourlyDistance, String hourlyCalories, int inZoneTime, int outZoneTime, int noActivityTime, int goal, int walkDistance,int runDistance,int walkDuration,int runDuration,String remarks) {
        this.createdDate = createdDate;
        this.date = date;
        this.steps = steps;
        this.walkSteps = walkSteps;
        this.runSteps = runSteps;
        this.distance = distance;
        this.calories = calories;
        this.hourlySteps = hourlySteps;
        this.hourlyDistance = hourlyDistance;
        this.hourlyCalories = hourlyCalories;
        this.inZoneTime = inZoneTime;
        this.outZoneTime = outZoneTime;
        this.noActivityTime = noActivityTime;
        this.goal = goal;
        this.walkDistance = walkDistance;
        this.runDistance = runDistance;
        this.walkDuration = walkDuration;
        this.runDuration = runDuration;
        this.remarks = remarks;
    }

    public void setiD(int iD) {
        this.iD = iD;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setWalkSteps(int walkSteps) {
        this.walkSteps = walkSteps;
    }

    public void setRunSteps(int runSteps) {
        this.runSteps = runSteps;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setHourlySteps(String hourlySteps) {
        this.hourlySteps = hourlySteps;
    }

    public void setHourlyDistance(String hourlyDistance) {
        this.hourlyDistance = hourlyDistance;
    }

    public void setHourlyCalories(String hourlyCalories) {
        this.hourlyCalories = hourlyCalories;
    }

    public void setInZoneTime(int inZoneTime) {
        this.inZoneTime = inZoneTime;
    }

    public void setOutZoneTime(int outZoneTime) {
        this.outZoneTime = outZoneTime;
    }

    public void setNoActivityTime(int noActivityTime) {
        this.noActivityTime = noActivityTime;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getiD() {
        return iD;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public long getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public int getWalkSteps() {
        return walkSteps;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public int getDistance() {
        return distance;
    }

    public int getCalories() {
        return calories;
    }

    public String getHourlySteps() {
        return hourlySteps;
    }

    public String getHourlyDistance() {
        return hourlyDistance;
    }

    public String getHourlyCalories() {
        return hourlyCalories;
    }

    public int getInZoneTime() {
        return inZoneTime;
    }

    public int getOutZoneTime() {
        return outZoneTime;
    }

    public int getNoActivityTime() {
        return noActivityTime;
    }

    public int getGoal() {
        return goal;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(int runDistance) {
        this.runDistance = runDistance;
    }

    public int getWalkDuration() {
        return walkDuration;
    }

    public void setWalkDuration(int walkDuration) {
        this.walkDuration = walkDuration;
    }

    public int getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(int runDuration) {
        this.runDuration = runDuration;
    }

    public String getValidicRecordID() {
        return validicRecordID;
    }

    public void setValidicRecordID(String validicRecordID) {
        this.validicRecordID = validicRecordID;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }

    @Override
    public int compareTo(Steps another) {
        if (getDate() < another.getDate()){
            return -1;
        }else if(getDate() > another.getDate()){
            return 1;
        }
        return 0;
    }




}
