package com.medcorp.event.validic;

import com.medcorp.model.User;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicCreateUserEvent {
    private User user;

    public ValidicCreateUserEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
