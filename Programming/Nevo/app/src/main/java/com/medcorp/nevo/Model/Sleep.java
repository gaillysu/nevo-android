package com.medcorp.nevo.model;

import java.util.Comparator;

/**
 * Created by gaillysu on 15/11/17.
 */
public class Sleep implements Comparable<Sleep>{

    @Override
    public int compareTo(Sleep another) {
        if (getDate() < another.getDate()){
            return -1;
        }else if(getDate() > another.getDate()){
            return 1;
        }
        return 0;
    }

    public enum SleepQuality{
        WAKE,
        LIGHT,
        DEEP
    }

    private final int iD;

    private final int userID;

    private final long createdDate;

    private long date;

    private int totalSleepTime;

    private int totalWakeTime;

    private int totalLightTime;

    private int totalDeepTime;

    private String hourlySleep;

    private String hourlyWake;

    private String hourlyLight;

    private String hourlyDeep;

    private long start;

    private long end;

    private int sleepQuality;

    private String remarks;

    public Sleep(int iD, int userID, long createdDate) {
        this.iD = iD;
        this.userID = userID;
        this.createdDate = createdDate;
    }

    public Sleep(int iD, int userID, long createdDate, long date, int totalSleepTime, int totalWakeTime, int totalLightTime, int totalDeepTime, String hourlySleep, String hourlyWake, String hourlyLight, String hourlyDeep, long start, long end, int sleepQuality, String remarks) {
        this.iD = iD;
        this.userID = userID;
        this.createdDate = createdDate;
        this.date = date;
        this.totalSleepTime = totalSleepTime;
        this.totalWakeTime = totalWakeTime;
        this.totalLightTime = totalLightTime;
        this.totalDeepTime = totalDeepTime;
        this.hourlySleep = hourlySleep;
        this.hourlyWake = hourlyWake;
        this.hourlyLight = hourlyLight;
        this.hourlyDeep = hourlyDeep;
        this.start = start;
        this.end = end;
        this.sleepQuality = sleepQuality;
        this.remarks = remarks;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        this.totalWakeTime = totalWakeTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        this.totalLightTime = totalLightTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        this.totalDeepTime = totalDeepTime;
    }

    public void setHourlySleep(String hourlySleep) {
        this.hourlySleep = hourlySleep;
    }

    public void setHourlyWake(String hourlyWake) {
        this.hourlyWake = hourlyWake;
    }

    public void setHourlyLight(String hourlyLight) {
        this.hourlyLight = hourlyLight;
    }

    public void setHourlyDeep(String hourlyDeep) {
        this.hourlyDeep = hourlyDeep;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
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

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public int getTotalWakeTime() {
        return totalWakeTime;
    }

    public int getTotalLightTime() {
        return totalLightTime;
    }

    public int getTotalDeepTime() {
        return totalDeepTime;
    }

    public String getHourlySleep() {
        return hourlySleep;
    }

    public String getHourlyWake() {
        return hourlyWake;
    }

    public String getHourlyLight() {
        return hourlyLight;
    }

    public String getHourlyDeep() {
        return hourlyDeep;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {

        return
//                "iD = " + iD + " \n " +
//                "userID = " + userID + " \n " +
                "createdDate = " + createdDate + " \n " +
                "date = " + date + " \n " +
//                "totalSleepTime = " + totalSleepTime + " \n " +
//                "totalWakeTime = " + totalWakeTime + " \n " +
//                "totalLightTime = " + totalLightTime + " \n " +
//                "totalDeepTime = " + totalDeepTime + " \n " +
                "hourlySleep = " + hourlySleep + " \n " +
                "hourlyWake = " + hourlyWake + " \n " +
                "hourlyLight = " + hourlyLight + " \n " +
                "hourlyDeep = " + hourlyDeep + " \n " +
//                "start = " + start + " \n " +
//                "end = " + end + " \n " +
//                "sleepQuality = " + sleepQuality + " \n " +
                "remarks = " + remarks + " \n ";

    }
}
