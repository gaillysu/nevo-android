package com.medcorp.nevo.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Hugo on 8/4/15.
 */
public class DailyHistory {

    private Date date;
    private List<Integer> hourlySteps;
    private int totalSteps;
    //add new from v1.2.2, FW v18/v31, sleep tracker
        //unit:cm->meter
    private int totalDist;
    private List<Integer> hourlyDist;
    //unit: cal->kcal
    private int totalCalories;
    private List<Integer> hourlyCalories;
    private int inactivityTime;
    private int totalInZoneTime;
    private int totalOutZoneTime;
    //unit: minute
    private int totalSleepTime;
    private List<Integer> hourlySleepTime;
    private  int totalWakeTime;
    private  List<Integer> hourlyWakeTime;
    private int totalLightTime;
    private List<Integer> hourlyLightTime;
    private  int totalDeepTime;
    private List<Integer> hourlyDeepTime;
    //end added

    public DailyHistory(Date date) {
        this.date = date;
    }

    public int getTotalSteps() {
        return totalSteps;
    }
    public void setTotalSteps(int TotalSteps) {
         totalSteps = TotalSteps;
    }

    public List<Integer> getHourlySteps() {
        return hourlySteps;
    }
    public void setHourlySteps(List<Integer> HourlySteps) {
        hourlySteps = HourlySteps;
    }

    public int getTotalDist() {
        return totalDist;
    }

    public void setTotalDist(int totalDist) {
        this.totalDist = totalDist;
    }

    public List<Integer> getHourlyDist() {
        return hourlyDist;
    }

    public void setHourlyDist(List<Integer> hourlyDist) {
        this.hourlyDist = hourlyDist;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public List<Integer> getHourlyCalories() {
        return hourlyCalories;
    }

    public void setHourlyCalories(List<Integer> hourlyCalories) {
        this.hourlyCalories = hourlyCalories;
    }

    public int getInactivityTime() {
        return inactivityTime;
    }

    public void setInactivityTime(int inactivityTime) {
        this.inactivityTime = inactivityTime;
    }

    public int getTotalInZoneTime() {
        return totalInZoneTime;
    }

    public void setTotalInZoneTime(int totalInZoneTime) {
        this.totalInZoneTime = totalInZoneTime;
    }

    public int getTotalOutZoneTime() {
        return totalOutZoneTime;
    }

    public void setTotalOutZoneTime(int totalOutZoneTime) {
        this.totalOutZoneTime = totalOutZoneTime;
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public void setTotalSleepTime(int totalSleepTime) {
        this.totalSleepTime = totalSleepTime;
    }

    public List<Integer> getHourlySleepTime() {
        return hourlySleepTime;
    }

    public void setHourlySleepTime(List<Integer> hourlySleepTime) {
        this.hourlySleepTime = hourlySleepTime;
    }

    public int getTotalWakeTime() {
        return totalWakeTime;
    }

    public void setTotalWakeTime(int totalWakeTime) {
        this.totalWakeTime = totalWakeTime;
    }

    public List<Integer> getHourlyWakeTime() {
        return hourlyWakeTime;
    }

    public void setHourlyWakeTime(List<Integer> hourlyWakeTime) {
        this.hourlyWakeTime = hourlyWakeTime;
    }

    public int getTotalLightTime() {
        return totalLightTime;
    }

    public void setTotalLightTime(int totalLightTime) {
        this.totalLightTime = totalLightTime;
    }

    public List<Integer> getHourlyLightTime() {
        return hourlyLightTime;
    }

    public void setHourlyLightTime(List<Integer> hourlyLightTime) {
        this.hourlyLightTime = hourlyLightTime;
    }

    public int getTotalDeepTime() {
        return totalDeepTime;
    }

    public void setTotalDeepTime(int totalDeepTime) {
        this.totalDeepTime = totalDeepTime;
    }

    public List<Integer> getHourlyDeepTime() {
        return hourlyDeepTime;
    }

    public void setHourlyDeepTime(List<Integer> hourlyDeepTime) {
        this.hourlyDeepTime = hourlyDeepTime;
    }

    public Date getDate() {
        return date;
    }

}
