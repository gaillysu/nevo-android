package com.medcorp.nevo.util;

import com.medcorp.nevo.model.Sleep;

import java.util.Comparator;

/**
 * Created by karl-john on 30/11/15.
 */
public class SleepSorter implements Comparator<Sleep>{
    @Override
    public int compare(Sleep lhs, Sleep rhs) {
        return lhs.compareTo(rhs);
    }
}
