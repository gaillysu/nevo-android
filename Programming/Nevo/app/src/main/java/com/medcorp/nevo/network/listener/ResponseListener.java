package com.medcorp.nevo.network.listener;

/**
 * Created by gaillysu on 16/3/15.
 */
public interface ResponseListener {
    public void onException(String jsonString);
    public void processResponse(String jsonString);
}
