package com.medcorp;

import android.util.Log;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ApplicationFlage {

    public enum Flage{
        LUNAR,NEVO;
    }

    public static final Flage FLAGE = Flage.LUNAR;

    static{
        Log.w("Karl","Hello");
    }
}
