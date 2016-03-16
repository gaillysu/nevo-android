package com.medcorp.nevo.validic.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.io.Serializable;

/**
 * Created by gaillysu on 16/3/8.
 */
public abstract class BaseSpringRequest<T> extends SpringAndroidSpiceRequest<T> {

    public BaseSpringRequest(Class<T> clazz) {
        super(clazz);
    }

    public abstract String buildRequestURL();

    public abstract String buildRequestBody();
}
