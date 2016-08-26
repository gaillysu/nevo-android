package com.medcorp.event;

/**
 * Created by karl-john on 17/5/16.
 */
public class LoginEvent {

    public enum status{
        SUCCESS,FAILED
    }

    private status loginStatus;

    public LoginEvent(status loginStatus) {
        this.loginStatus = loginStatus;
    }

    public status getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(status loginStatus) {
        this.loginStatus = loginStatus;
    }
}
