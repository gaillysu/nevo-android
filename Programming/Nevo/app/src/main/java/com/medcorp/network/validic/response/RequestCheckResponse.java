package com.medcorp.network.validic.response;

import com.medcorp.network.med.model.UserWithPasswordToken;

/**
 * Created by Administrator on 2016/7/13.
 */
public class RequestCheckResponse  extends BaseResponse {

    private UserWithPasswordToken user;
    public UserWithPasswordToken getUser() {
        return user;
    }

    public void setUser(UserWithPasswordToken user) {
        this.user = user;
    }
}
