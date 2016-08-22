package com.medcorp.network.med.retrofit;


import com.medcorp.network.med.model.CreateUserModel;
import com.medcorp.network.med.model.CreateUserObject;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.model.LoginUserObject;
import com.medcorp.network.validic.model.RequestTokenBody;
import com.medcorp.network.validic.model.RequestTokenResponse;

import net.medcorp.library.user.UserLoginModel;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by med on 16/3/21.
 */
public interface MedCorp {
    @POST("/user/login")
    LoginUserModel userLogin(@Body LoginUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/user/create")
    CreateUserModel userCreate(@Body CreateUserObject object, @Header("Authorization") String auth, @Header("Content-Type") String type);

    @POST("/user/request_password_token")
    RequestTokenResponse requestToken(@Body RequestTokenBody body, @Header("Authorization") String auth, @Header("Content-Type") String type);
}
