package com.medcorp.nevo.network.med.model;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestCheckRequestBody {
    private String token;
    private RequestTokenBodyParameters params;

    public RequestCheckRequestBody(){}

    public RequestCheckRequestBody(String token, RequestTokenBodyParameters params) {
        this.token = token;
        this.params = params;
    }

    public RequestTokenBodyParameters getParams() {
        return params;
    }

    public void setParams(RequestTokenBodyParameters params) {
        this.params = params;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
