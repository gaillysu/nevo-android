package com.medcorp.network.med.retrofit;


import com.medcorp.network.med.model.CreateUserModel;
import com.medcorp.network.med.model.CreateUserObject;
import com.medcorp.network.med.model.NevoUserModel;
import com.medcorp.network.validic.model.RequestTokenBody;
import com.medcorp.network.validic.model.RequestTokenResponse;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by med on 16/3/21.
 */
public interface MedCorp {

    @FormUrlEncoded
    @POST("/api/account/register")
    NevoUserModel registerNevoUser(@Field("params[time]") long time, @Field("params[check_key]") String check_key, @Field("user") String user, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/account/login")
    NevoUserModel loginNevoUser(@Field("params[time]") long time,@Field("params[check_key]") String check_key, @Field("user") String user, @Field("password") String password);

    @POST("/user/create")
    CreateUserModel userCreate(@Body CreateUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/user/request_password_token")
    RequestTokenResponse requestToken(@Body RequestTokenBody body, @Header("Authorization") String auth, @Header("Content-Type") String type);
}
