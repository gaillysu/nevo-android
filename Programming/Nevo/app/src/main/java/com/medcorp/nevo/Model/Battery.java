package com.medcorp.nevo.model;

/**
 * Created by gaillysu on 15/11/24.
 */
public class Battery {
    byte  mBatterylevel;
    public Battery(byte level)
    {
        this.mBatterylevel = level;
    }
    public byte getBatterylevel() {return mBatterylevel;}
}
