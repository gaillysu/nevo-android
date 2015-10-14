package com.medcorp.nevo.View;

/**
 * NotificationItem
 */
public class NotificationItem {

    private int mIcon;
    private CharSequence mLabel;
    private int mImage;

    public NotificationItem(int icon, CharSequence text, int img){
        this.mLabel = text;
        this.mImage = img;
        this.mIcon = icon;
    }

    public CharSequence getmLabel() {
        return mLabel;
    }

    public int getmImage() {
        return mImage;
    }

    public int getmIcon() {
        return mIcon;
    }
}
