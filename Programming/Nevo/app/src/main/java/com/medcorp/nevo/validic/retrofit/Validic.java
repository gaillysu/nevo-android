package com.medcorp.nevo.validic.retrofit;

import com.medcorp.nevo.validic.model.routine.AddRoutineRecordRequestObject;
import com.medcorp.nevo.validic.model.routine.DeleteRoutineRecordRequestObject;
import com.medcorp.nevo.validic.model.routine.UpdateRoutineRecordRequestObject;
import com.medcorp.nevo.validic.model.routine.ValidicDeleteRoutineRecordModel;
import com.medcorp.nevo.validic.model.routine.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.nevo.validic.model.routine.ValidicReadRoutineRecordModel;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModel;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.medcorp.nevo.validic.model.sleep.AddSleepRecordRequestObject;
import com.medcorp.nevo.validic.model.sleep.DeleteSleepRecordRequestObject;
import com.medcorp.nevo.validic.model.sleep.ValidicDeleteSleepRecordModel;
import com.medcorp.nevo.validic.model.sleep.ValidicReadMoreSleepRecordsModel;
import com.medcorp.nevo.validic.model.sleep.ValidicReadSleepRecordModel;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecordModel;
import com.medcorp.nevo.validic.request.CreateUserRequestObject;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
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

    @POST("/organizations/{ORGANIZATION_ID}/users/{USER_ID}/routine.json")
    ValidicRoutineRecordModel addRoutineRecordRequest(@Body AddRoutineRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("USER_ID") String user, @Header("Content-Type") String type);

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicReadRoutineRecordModel getRoutineRecordRequest(@Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Query("access_token") String accessToken);

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine.json")
    ValidicReadMoreRoutineRecordsModel getMoreRoutineRecordsRequest(@Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Query("access_token") String accessToken,@Query("start_date") String start_date,@Query("end_date") String end_date,@Query("expanded") int expanded,@Query("page") int page);

    @DELETEWITHBODY("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicDeleteRoutineRecordModel deleteRoutineRecordRequest(@Body DeleteRoutineRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Header("Content-Type") String type);

    @PUT("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/routine/{VALIDIC_RECORD_ID}.json")
    ValidicRoutineRecordModel updateRoutineRecordRequest(@Body UpdateRoutineRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Header("Content-Type") String type);

    //sleep REST API
    @POST("/organizations/{ORGANIZATION_ID}/users/{USER_ID}/sleep.json")
    ValidicSleepRecordModel addSleepRecordRequest(@Body AddSleepRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("USER_ID") String user, @Header("Content-Type") String type );

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/sleep/{VALIDIC_RECORD_ID}.json")
    ValidicReadSleepRecordModel getSleepRecordRequest(@Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Query("access_token") String accessToken);

    @GET("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/sleep.json")
    ValidicReadMoreSleepRecordsModel getMoreSleepRecordsRequest(@Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Query("access_token") String accessToken,@Query("start_date") String start_date,@Query("end_date") String end_date,@Query("expanded") int expanded,@Query("page") int page);

    @DELETEWITHBODY("/organizations/{ORGANIZATION_ID}/users/{VALIDIC_USER_ID}/sleep/{VALIDIC_RECORD_ID}.json")
    ValidicDeleteSleepRecordModel deleteSleepRecordRequest(@Body DeleteSleepRecordRequestObject object, @Path("ORGANIZATION_ID") String organization, @Path("VALIDIC_USER_ID") String user, @Path("VALIDIC_RECORD_ID") String recordId, @Header("Content-Type") String type);

    //NO update sleep to validic
}