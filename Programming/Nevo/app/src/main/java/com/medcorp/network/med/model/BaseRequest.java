package com.medcorp.network.med.model;

/**
 * Created by Jason on 2016/8/9.
 */

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.apache.commons.codec.binary.Base64;

abstract class BaseRequest<T,R> extends RetrofitSpiceRequest<T,R> {
    final String CONTENT_TYPE = "application/json";
    BaseRequest(Class<T> clazz, Class<R> retrofitInterfaceClass) {
        super(clazz, retrofitInterfaceClass);
    }
    //for HTTP "get"/"header", hasn't body, so needn't implement this function, others need do it.
    interface BaseRetroRequestBody<B> {
        B  buildRequestBody();
    }
    String buildAuthorization(){
        // TODO add to strings/config
        String authorization = "Basic ";
        String username = "apps";
        String password = "med_app_development";
        return authorization + new String(new Base64().encode(new String(username+":"+password).getBytes()));
    }
}
