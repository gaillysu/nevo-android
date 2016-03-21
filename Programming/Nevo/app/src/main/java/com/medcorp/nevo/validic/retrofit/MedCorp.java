package com.medcorp.nevo.validic.retrofit;

import com.medcorp.nevo.validic.model.RegisterNevoUserRequestObject;
import com.medcorp.nevo.validic.model.LoginNevoUserRequestObject;
import com.medcorp.nevo.validic.model.NevoUserModel;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by med on 16/3/21.
 */
public interface MedCorp {

    @POST("/api/account/register")
    NevoUserModel registerNevoUser(@Body RegisterNevoUserRequestObject object, @Header("Content-Type") String type);

    @POST("/api/account/login")
    NevoUserModel loginNevoUser(@Body LoginNevoUserRequestObject object, @Header("Content-Type") String type);
}
