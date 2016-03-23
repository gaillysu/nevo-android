package com.medcorp.nevo.validic.request.sleep;

import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.sleep.AddSleepRecordRequestObject;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecord;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecordModel;
import com.medcorp.nevo.validic.request.BaseRetroRequest;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by med on 16/3/23.
 */
public class AddSleepRecordRequest extends RetrofitSpiceRequest<ValidicSleepRecordModel,Validic> implements BaseRetroRequest<AddSleepRecordRequestObject> {

    private ValidicSleepRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddSleepRecordRequest(ValidicSleepRecord record,String organizationId,String organizationTokenKey,String validicUserId) {
        super(ValidicSleepRecordModel.class, Validic.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public AddSleepRecordRequestObject buildRequestBody() {
        AddSleepRecordRequestObject object = new AddSleepRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        object.setSleep(record);
        Log.i(this.getClass().getSimpleName(), "object: " + new Gson().toJson(object));
        return object;
    }

    @Override
    public ValidicSleepRecordModel loadDataFromNetwork() throws Exception {
        return getService().addSleepRecordRequest(buildRequestBody(), organizationId, validicUserId,"application/json");
    }
}
