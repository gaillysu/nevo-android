package com.medcorp.network.base;

/**
 * Created by Administrator on 2016/7/20.
 */
public class BaseResponse {

    private float version;
    private String message;
    private int status;

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
