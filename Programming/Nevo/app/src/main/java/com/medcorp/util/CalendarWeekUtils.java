package com.medcorp.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jason on 2016/8/26.
 */
public class CalendarWeekUtils {

    private Date date;
    private Date WeekStartDate;
    private Date WeekEndDate;
    private Date lastWeekStart;
    private Date lastWeekEnd;
    private Date monthStartDate;

    public Date getMonthEndDate() {
        return monthEndDate;
    }

    public Date getMonthStartDate() {
        return monthStartDate;
    }

    private Date monthEndDate;

    public Date getLastWeekStart() {
        return lastWeekStart;
    }

    public Date getLastWeekEnd() {
        return lastWeekEnd;
    }


    public Date getWeekStartDate() {
        return WeekStartDate;
    }

    public Date getWeekEndDate() {
        return WeekEndDate;
    }

    public CalendarWeekUtils(Date date) {
        this.date = date;
        calculateWeekStartAndWeekEnd();
    }

    private void calculateWeekStartAndWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        WeekStartDate = new Date(date.getTime() - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * 24 * 60 * 60 * 1000L);
        WeekEndDate = new Date(WeekStartDate.getTime() + 6 * 24 * 60 * 60 * 1000L);

        lastWeekEnd = new Date(date.getTime());
        lastWeekStart = new Date(lastWeekEnd.getTime() - 6 * 24 * 60 * 60 * 1000L);

        monthEndDate = date;
        monthStartDate = new Date(monthEndDate.getTime()-29*24*60*60*1000L);
    }
}
