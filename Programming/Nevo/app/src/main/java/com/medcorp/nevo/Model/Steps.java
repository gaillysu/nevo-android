package com.medcorp.nevo.model;

public class Steps {

    private final int iD;

    private final int userID;

    private final long createdDate;

    private long date;

    private int steps;

    private int walkSteps;

    private int runSteps;

    private int distance;

    private int calories;

    private String hourlySteps;

    private String hourlyDistance;

    private String hourlyCalories;

    private int inZoneTime;

    private int outZoneTime;

    private int noActivityTime;

    private int goal;

    private String remarks;


    public Steps(int iD, int userID, long createdDate) {
        this.iD = iD;
        this.userID = userID;
        this.createdDate = createdDate;
    }

    public Steps(int iD, int userID, long createdDate, long date, int steps, int walkSteps, int runSteps, int distance, int calories, String hourlySteps, String hourlyDistance, String hourlyCalories, int inZoneTime, int outZoneTime, int noActivityTime, int goal, String remarks) {
        this.iD = iD;
        this.userID = userID;
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
        this.remarks = remarks;
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

    public int getUserID() {
        return userID;
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
}
