package com.medcorp.ble.model.color;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Jason on 2016/12/8.
 */

public class LedLamp extends RealmObject implements Serializable{

    private String name;
    private int color;
    private boolean isSelect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
