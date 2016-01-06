package com.medcorp.nevo.model;

/**
 * Created by gaillysu on 16/1/6.
 */
public class SettingMenu {
    private String title;
    private int icon;
    private boolean withSwitch;

    public SettingMenu(String title,int icon,boolean withSwitch)
    {
        this.title = title;
        this.icon  = icon;
        this.withSwitch = withSwitch;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isWithSwitch() {
        return withSwitch;
    }
}
