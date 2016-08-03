package com.medcorp.network.med.model;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.med.retrofit.MedCorp;
import com.medcorp.network.base.BaseRetroRequest;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

/**
 * Created by gaillysu on 16/3/14.
 */
public class NevoUserRegisterRequest extends RetrofitSpiceRequest<NevoUserModel,MedCorp> implements BaseRetroRequest<RegisterNevoUserRequestObject> {

    private String email;
    private String password;

    public NevoUserRegisterRequest(String email, String password) {
        super(NevoUserModel.class,MedCorp.class);
        this.email = email;
        this.password = password;
    }


    @Override
    public RegisterNevoUserRequestObject buildRequestBody() {
        RegisterNevoUserRequestObject object = new RegisterNevoUserRequestObject();
        object.setUser(email);
        object.setPassword(password);
        NevoUserParams params = new NevoUserParams();
        long time = new Date().getTime()/1000;
        params.setTime(time);
        String token_base = String.format("%d-nevo2015medappteam",time);
        String token_key= new String(Hex.encodeHex(DigestUtils.md5(token_base)));
        params.setCheck_key(token_key);
        object.setParams(params);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public NevoUserModel loadDataFromNetwork() throws Exception {
        RegisterNevoUserRequestObject object = buildRequestBody();
        return getService().registerNevoUser(object.getParams().getTime(),object.getParams().getCheck_key(),object.getUser(),object.getPassword());
    }
}
