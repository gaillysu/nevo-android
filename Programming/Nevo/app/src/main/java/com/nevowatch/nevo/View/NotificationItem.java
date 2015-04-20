package com.nevowatch.nevo.View;

/**
 * NotificationItem
 */
public class NotificationItem {

    private int mIcon;
    private CharSequence mLabel;
    private int mImage;

    public NotificationItem(int icon, CharSequence text, int img){
        this.mIcon = icon;
        this.mLabel = text;
        this.mImage = img;
    }

    public int getmIcon() {
        return mIcon;
    }

    public CharSequence getmLabel() {
        return mLabel;
    }

    public int getmImage() {
        return mImage;
    }
}
