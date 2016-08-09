package com.medcorp.network.med.model;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.med.retrofit.MedCorp;

/**
 * Created by gaillysu on 16/3/14.
 */
public class CreateAccountRequest extends com.medcorp.network.base.BaseRequest<CreateUserModel,MedCorp>
        implements BaseRequest.BaseRetroRequestBody<CreateUserObject> {

    private String token;
    private CreateUser   user;

    public CreateAccountRequest(CreateUser user, String token) {
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
