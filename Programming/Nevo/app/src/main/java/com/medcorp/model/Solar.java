package com.medcorp.model;

import com.medcorp.util.Common;

import java.util.Date;

/**
 * Created by karl-john on 25/8/2016.
 */

public class Solar {

    private int id;

    private Date createdDate;

    private Date date;

    private int userId;

    private String hourlyHarvestingTime;

    private int totalHarvestingTime;

    public Solar(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Solar(Date createdDate, Date date, int userId, String hourlyHarvestingTime, int totalHarvestingTime) {
        this.createdDate = createdDate;
        this.date = date;
        this.userId = userId;
        this.hourlyHarvestingTime = hourlyHarvestingTime;
        this.totalHarvestingTime = totalHarvestingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHourlyHarvestingTime() {
        return hourlyHarvestingTime;
    }

    public void setHourlyHarvestingTime(String hourlyHarvestingTime) {
        this.hourlyHarvestingTime = hourlyHarvestingTime;
    }

    public int getTotalHarvestingTime() {
        return totalHarvestingTime;
    }

    public void setTotalHarvestingTime(int totalHarvestingTime) {
        this.totalHarvestingTime = totalHarvestingTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int[] getHourlyHarvestingTimeInt(){
        return Common.convertJSONArrayIntToArray(getHourlyHarvestingTime());
    }
}
