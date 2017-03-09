package com.medcorp.network.validic.request.routine;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.network.base.BaseRequest;
import com.medcorp.network.validic.model.AddRoutineRecordRequestObject;
import com.medcorp.network.validic.retrofit.Validic;
import com.medcorp.network.validic.model.ValidicRoutineRecord;
import com.medcorp.network.validic.model.ValidicRoutineRecordModel;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddRoutineRecordRequest extends BaseRequest<ValidicRoutineRecordModel,Validic> implements BaseRequest.BaseRetroRequestBody<AddRoutineRecordRequestObject> {

    private ValidicRoutineRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddRoutineRecordRequest(ValidicRoutineRecord record, String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicRoutineRecordModel.class,Validic.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }
    @Override
    public AddRoutineRecordRequestObject buildRequestBody() {
        AddRoutineRecordRequestObject object = new AddRoutineRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        object.setRoutine(record);
        Log.i(this.getClass().getSimpleName(),"object: "+new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicRoutineRecordModel loadDataFromNetwork() throws Exception {
        return getService().addRoutineRecordRequest(buildRequestBody(), organizationId, validicUserId, "application/json");
    }
}
