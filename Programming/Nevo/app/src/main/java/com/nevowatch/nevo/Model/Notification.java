package com.nevowatch.nevo.Model;

import android.widget.Switch;

/**
 * Notification
 */
public class Notification {

    private boolean isState;
    private int mChoosenColor;
    private Switch mSwitch;

    public boolean isState() {
        return isState;
    }

    public void setState(boolean isState) {
        this.isState = isState;
    }

    public int getmChoosenColor() {
        return mChoosenColor;
    }

    public void setmChoosenColor(int mChoosenColor) {
        this.mChoosenColor = mChoosenColor;
    }

    public Switch getmSwitch() {
        return mSwitch;
    }

    public void setmSwitch(Switch mSwitch) {
        this.mSwitch = mSwitch;
    }
}
