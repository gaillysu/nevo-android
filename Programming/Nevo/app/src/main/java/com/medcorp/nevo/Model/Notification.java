package com.medcorp.nevo.Model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Notification {

    public enum NotificationType {
        SMS, Email, Call,Facebook,Calendar,Wechat, Whatsapp
    }

    private NotificationType type;
    private int color;
    private boolean isOnOff;

    public Notification(NotificationType type, boolean isOnOff,int color)
    {
        this.type = type;
        this.isOnOff = isOnOff;
        this.color = color;
    }

    public NotificationType getType(){return type;}
    public boolean getOnOff() {return isOnOff;}
    public int     getColor() {return color;}

}
