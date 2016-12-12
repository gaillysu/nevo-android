package com.medcorp.ble.model.color;

import io.realm.RealmObject;

/**
 * Created by Jason on 2016/12/8.
 * s
 */
public class LedLamp extends RealmObject {


    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
