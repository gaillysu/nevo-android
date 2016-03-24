package com.medcorp.nevo.validic.request.sleep;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.sleep.DeleteSleepRecordRequestObject;
import com.medcorp.nevo.validic.model.sleep.ValidicDeleteSleepRecordModel;
import com.medcorp.nevo.validic.request.BaseRetroRequest;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class DeleteSleepRecordRequest extends RetrofitSpiceRequest<ValidicDeleteSleepRecordModel,Validic> implements BaseRetroRequest<DeleteSleepRecordRequestObject> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;

    public DeleteSleepRecordRequest(String organizationId, String organizationTokenKey, String validicUserId, String validicRecordId) {
        super(ValidicDeleteSleepRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }


    @Override
    public DeleteSleepRecordRequestObject buildRequestBody() {
        DeleteSleepRecordRequestObject object = new DeleteSleepRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicDeleteSleepRecordModel loadDataFromNetwork() throws Exception {
       return getService().deleteSleepRecordRequest(buildRequestBody(), organizationId, validicUserId, validicRecordId, "application/json");
    }
}