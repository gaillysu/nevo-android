package com.nevowatch.nevo.Model;

/**
 * Created by imaze on 15/4/2.
 */
public class NevoData {

    private String mClockTime;
    private Boolean isClockOpen;
    private int mStepGoal;

    public String getmClockTime() {
        return mClockTime;
    }

    public void setmClockTime(String mClockTime) {
        this.mClockTime = mClockTime;
    }

    public Boolean getIsClockOpen() {
        return isClockOpen;
    }

    public void setIsClockOpen(Boolean isClockOpen) {
        this.isClockOpen = isClockOpen;
    }

    public int getmStepGoal() {
        return mStepGoal;
    }

    public void setmStepGoal(int mStepGoal) {
        this.mStepGoal = mStepGoal;
    }
}
