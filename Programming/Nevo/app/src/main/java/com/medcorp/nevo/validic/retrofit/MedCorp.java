package com.medcorp.nevo.validic.retrofit;


import com.medcorp.nevo.validic.model.NevoUserModel;
import retrofit.http.POST;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;

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
