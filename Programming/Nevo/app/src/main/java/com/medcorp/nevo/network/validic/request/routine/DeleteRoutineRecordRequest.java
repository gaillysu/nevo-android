package com.medcorp.nevo.network.validic.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.network.validic.model.DeleteRoutineRecordRequestObject;
import com.medcorp.nevo.network.validic.model.ValidicDeleteRoutineRecordModel;
import com.medcorp.nevo.network.base.BaseRetroRequest;
import com.medcorp.nevo.network.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class DeleteRoutineRecordRequest extends RetrofitSpiceRequest<ValidicDeleteRoutineRecordModel,Validic> implements BaseRetroRequest<DeleteRoutineRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;

    public DeleteRoutineRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicDeleteRoutineRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }


    @Override
    public DeleteRoutineRecordRequestObject buildRequestBody() {
        DeleteRoutineRecordRequestObject object = new DeleteRoutineRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicDeleteRoutineRecordModel loadDataFromNetwork() throws Exception {
       return getService().deleteRoutineRecordRequest(buildRequestBody(), organizationId, validicUserId, validicRecordId, "application/json");
    }
}
