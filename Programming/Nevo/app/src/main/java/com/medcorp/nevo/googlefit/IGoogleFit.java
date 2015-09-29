package com.medcorp.nevo.googlefit;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by evan on 2015/6/16 0016.
 */
public interface IGoogleFit {
    /**
     Requests the permissions
     */
    public void requestPermission();

    /**
     Writes the given datapoint to the database
     This function will ensure that there are no doublons
     */
    public void save(GFDataPoint dataPoint);

    /**
     Checks if a data point is present in the DB
     returns an empty Optional if we don't have the right to read this kind of data
     */
    public boolean contains(GFDataPoint dataPoint);

    public GoogleApiClient getclient();
}

