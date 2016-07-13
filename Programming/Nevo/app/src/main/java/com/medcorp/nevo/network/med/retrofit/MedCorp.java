package com.medcorp.nevo.network.med.retrofit;


import com.medcorp.nevo.network.med.model.NevoUserModel;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
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


}
