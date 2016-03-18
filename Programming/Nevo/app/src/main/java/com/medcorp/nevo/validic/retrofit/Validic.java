package com.medcorp.nevo.validic.retrofit;

import com.medcorp.nevo.validic.model.AddRecordRequestObject;
import com.medcorp.nevo.validic.model.DeleteRecordRequestObject;
import com.medcorp.nevo.validic.model.UpdateRecordRequestObject;
import com.medcorp.nevo.validic.model.ValidicDeleteRecordModel;
import com.medcorp.nevo.validic.model.ValidicReadAllRecordsModel;
import com.medcorp.nevo.validic.model.ValidicReadRecordModel;
import com.medcorp.nevo.validic.model.ValidicRecordModel;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.DELETE;

/**
 * Created by Karl on 3/16/16.
 */
public interface Validic {
    @GET("/organizations/{organization}.json")
    VerifyCredentialModel verifyCredential(@Path("organization") String organization, @Query("access_token") String accessToken);

    @POST("/organizations/{organization}/authorization/new_user")
    ValidicUser createUserRequest(@Body CreateUserRequestObject object, @Path("organization") String organization, @Header("Content-Type") String type );

    @POST("/organizations/{ORGANIZATION_ID}/users/{USER_ID}/routine.json")
    ValidicRecordModel addRecordRequest(@Body AddRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("USER_ID") String user, @Header("Content-Type") String type );

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicReadRecordModel getRecordRequest(@Path("ORGANIZATION_ID") String organization,@Path("VALIDIC_USER_ID") String user,@Path("VALIDIC_RECORD_ID") String recordId, @Query("access_token") String accessToken);

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine.json")
    ValidicReadAllRecordsModel getAllRecordsRequest(@Path("ORGANIZATION_ID") String organization,@Path("VALIDIC_USER_ID") String user,@Query("access_token") String accessToken);

    @DELETEWITHBODY("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicDeleteRecordModel deleteRecordRequest(@Body DeleteRecordRequestObject object,@Path("ORGANIZATION_ID") String organization,@Path("VALIDIC_USER_ID") String user,@Path("VALIDIC_RECORD_ID") String recordId, @Header("Content-Type") String type);

    @PUT("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicRecordModel updateRecordRequest(@Body UpdateRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Header("Content-Type") String type );

}