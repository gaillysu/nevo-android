package com.nevowatch.nevo.View;

/**
 * NotificationItem
 */
public class NotificationItem {

    private CharSequence mLabel;
    private int mImage;

    public NotificationItem(CharSequence text, int img){
        this.mLabel = text;
        this.mImage = img;
    }

    public CharSequence getmLabel() {
        return mLabel;
    }

    public int getmImage() {
        return mImage;
    }
}
