package com.medcorp.nevo.validic.request;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.AddRecordRequestObject;
import com.medcorp.nevo.validic.model.ValidicRecord;
import com.medcorp.nevo.validic.model.ValidicRecordModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddRecordRequest  extends RetrofitSpiceRequest<ValidicRecordModel,Validic> implements BaseRetroRequest<AddRecordRequestObject> {

    private ValidicRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddRecordRequest(ValidicRecord record,String organizationId,String organizationTokenKey,String validicUserId) {
        super(ValidicRecordModel.class,Validic.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }
    @Override
    public AddRecordRequestObject buildRequestBody() {
        AddRecordRequestObject object = new AddRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        object.setRoutine(record);
        Log.i(this.getClass().getSimpleName(),"object: "+new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRecordModel loadDataFromNetwork() throws Exception {
        return getService().addRecordRequest(buildRequestBody(), organizationId, validicUserId,"application/json");
    }
}
