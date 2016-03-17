package com.medcorp.nevo.validic.request;

/**
 * Created by gaillysu on 16/3/17.
 */
public interface BaseRetroRequest<T> {
    public T buildRequestBody();
}
