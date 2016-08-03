package com.medcorp.network.med.model;

import com.google.gson.annotations.Expose;

/**
 * Created by med on 16/3/21.
 */
public class LoginNevoUserRequestObject {
    @Expose
    NevoUserParams params;

    @Expose
    String user;

    @Expose
    String password;

    public NevoUserParams getParams() {
        return params;
    }

    public void setParams(NevoUserParams params) {
        this.params = params;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
