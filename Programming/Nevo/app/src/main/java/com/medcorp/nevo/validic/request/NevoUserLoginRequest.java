package com.medcorp.nevo.validic.request;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.LoginNevoUserRequestObject;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.NevoUserModel;
import com.medcorp.nevo.validic.model.NevoUserParams;
import com.medcorp.nevo.validic.retrofit.MedCorp;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.Date;

/**
 * Created by gaillysu on 16/3/14.
 */
public class NevoUserLoginRequest extends RetrofitSpiceRequest<NevoUserModel,MedCorp> implements BaseRetroRequest<LoginNevoUserRequestObject>  {

    private String email;
    private String password;

    public NevoUserLoginRequest(String email, String password) {
        super(NevoUserModel.class,MedCorp.class);
        this.email = email;
        this.password = password;
    }

    @Override
    public LoginNevoUserRequestObject buildRequestBody() {
        LoginNevoUserRequestObject object  = new LoginNevoUserRequestObject();
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
        return getService().loginNevoUser(buildRequestBody(), "application/x-www-form-urlencoded");
    }
}
