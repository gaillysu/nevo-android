package com.medcorp.nevo.network.med.model;


import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.network.base.BaseRequest;
import com.medcorp.nevo.network.med.retrofit.MedCorp;

/**
 * Created by Administrator on 2016/7/20.
 */
public class RequestCreateNewAccountRequest extends BaseRequest<CreateUserModel ,MedCorp>  implements BaseRequest.BaseRetroRequestBody<CreateUserObject>  {

    private String token;
    private CreateUser   user;

    public RequestCreateNewAccountRequest(CreateUser user,String token) {
        super(CreateUserModel.class, MedCorp.class);
        this.user  = user;
        this.token = token;
    }

    @Override
    public CreateUserModel loadDataFromNetwork() throws Exception {
        return getService().userCreate(buildRequestBody(), buildAuthorization(), CONTENT_TYPE);
    }

    @Override
    public CreateUserObject buildRequestBody() {
        CreateUserObject object = new CreateUserObject();
        object.setToken(token);
        CreateUserParameters parameters = new CreateUserParameters();
        parameters.setUser(user);
        object.setParams(parameters);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }
}