package com.nevowatch.nevo.Model;

import java.util.Date;
import java.util.List;

/**
 * Created by Hugo on 8/4/15.
 */
public class DailyHistory {

    private Date mDate;
    private List<Integer> mHourlySteps;
    private int mTotalSteps;

    public DailyHistory(Date date, List<Integer> hourlySteps, int totalSteps) {
        mDate = date;
        mHourlySteps = hourlySteps;
        mTotalSteps = totalSteps;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }

    public List<Integer> getHourlySteps() {
        return mHourlySteps;
    }

    public Date getDate() {
        return mDate;
    }


}
