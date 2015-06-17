package com.nevowatch.nevo.NevoGFT;

import com.google.android.gms.fitness.data.DataSet;

/**
 * Created by evan on 2015/6/16 0016.
 */
public interface GFDataPoint{
    /**
     GFDataPoint should be mapable to a DataSet
     It's the only prerequisite
     */
    public DataSet toDataSet();

    /**
     if a GFDataPoint has present in database, when its value changed, perhaps need update it.
     */
    public boolean isUpdate();
}
