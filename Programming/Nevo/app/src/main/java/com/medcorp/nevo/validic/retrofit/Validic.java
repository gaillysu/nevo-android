package com.medcorp.nevo.validic.retrofit;

import com.medcorp.nevo.validic.model.ValidicUser;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Karl on 3/16/16.
 */
public interface Validic {
    @GET("/organizations/{organization}.json")
    VerifyCredentialModel verifyCredential(@Path("organization") String organization, @Query("access_token") String accessToken);

    @POST("/organizations/{organization}/authorization/new_user")
    ValidicUser createUserRequest(@Body CreateUserRequestObject object, @Path("organization") String organization, @Header("Content-Type") String type );


}