package com.nevowatch.nevo.Model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Notification {

    public enum NotificationType {
        SMS, Email, Call,Facebook,Calendar,Wechat
    }

    NotificationType mType;
    int mColor;
    boolean  mIsOnOff;

    public Notification(NotificationType type, boolean isOnOff,int color)
    {
        mType = type;
        mIsOnOff = isOnOff;
        mColor = color;
    }

    public NotificationType getType(){return mType;}
    public boolean getOnOff() {return mIsOnOff;}
    public int     getColor() {return mColor;}

}
