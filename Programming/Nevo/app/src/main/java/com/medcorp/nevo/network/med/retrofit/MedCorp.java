package com.medcorp.nevo.network.med.retrofit;


import com.medcorp.nevo.network.med.model.CreateUserModel;
import com.medcorp.nevo.network.med.model.CreateUserObject;
import com.medcorp.nevo.network.med.model.NevoUserModel;

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
    NevoUserModel registerNevoUser(@Field("params[time]") long time,@Field("params[check_key]") String check_key, @Field("user") String user, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/account/login")
    NevoUserModel loginNevoUser(@Field("params[time]") long time,@Field("params[check_key]") String check_key, @Field("user") String user, @Field("password") String password);

    @POST("/user/create")
    CreateUserModel userCreate(@Body CreateUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);
}
