package com.medcorp.event;

/**
 * Created by karl-john on 17/5/16.
 */
public class SignUpEvent {

    public enum status{
        SUCCESS,FAILED
    }

    private status signUpStatus;

    public SignUpEvent(status signUpStatus) {
        this.signUpStatus = signUpStatus;
    }

    public status getSignUpStatus() {
        return signUpStatus;
    }

    public void setSignUpStatus(status signUpStatus) {
        this.signUpStatus = signUpStatus;
    }
}
