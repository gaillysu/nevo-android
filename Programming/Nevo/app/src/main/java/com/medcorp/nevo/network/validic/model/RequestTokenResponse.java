package com.medcorp.nevo.network.validic.model;

import com.medcorp.nevo.network.base.BaseResponse;
import com.medcorp.nevo.network.med.model.UserWithPasswordToken;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RequestTokenResponse extends BaseResponse
{
   private UserWithPasswordToken user;

    public UserWithPasswordToken getUser() {
        return user;
    }

    public void setUser(UserWithPasswordToken user) {
        this.user = user;
    }
}
