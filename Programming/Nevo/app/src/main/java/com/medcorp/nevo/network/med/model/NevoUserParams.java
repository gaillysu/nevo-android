package com.medcorp.nevo.network.med.model;

/**
 * Created by med on 16/3/21.
 */
public class NevoUserParams {
    long time;
    String check_key;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCheck_key() {
        return check_key;
    }

    public void setCheck_key(String check_key) {
        this.check_key = check_key;
    }
}
