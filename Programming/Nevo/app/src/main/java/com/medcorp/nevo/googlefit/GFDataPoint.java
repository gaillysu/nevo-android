package com.medcorp.nevo.googlefit;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
/**
 * Created by evan on 2015/6/16 0016.
 */
public interface GFDataPoint{
    /**
     GFDataPoint should be mapable to a DataSet
     It's the only prerequisite
     */
    public DataSet getDataSet();

    /**
     * sometime we should use write with the sessionRequest to insert data
     * @return
     */
    public SessionInsertRequest getSessionInsertRequest();

    /**
     if a GFDataPoint has present in database, when its value changed, perhaps need update it.
     */
    public boolean isUpToDate();

    /**
     * read session request
     */
    public SessionReadRequest getSessionReadRequest();

    /**
     * delete session request
     */
    public DataDeleteRequest getDataDeleteRequest();

    /**
     * save google fit value
     * @param value :read it from google fit
     */
    public void saveValue(int value);
}