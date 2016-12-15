package com.medcorp;

import android.util.Log;

/**
 * Created by Administrator on 2016/7/1.
 */
public class ApplicationFlag {

    /**
     *
     */
    public enum Flag {
        LUNAR,NEVO;
    }

    public static final Flag FLAG = Flag.NEVO;

    static{
        Log.w("Karl","Hello nevo");
    }
}
