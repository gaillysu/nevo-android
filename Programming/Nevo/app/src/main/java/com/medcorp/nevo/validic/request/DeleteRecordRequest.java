package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.DeleteRecordRequestObject;
import com.medcorp.nevo.validic.model.ValidicDeleteRecordModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaillysu on 16/3/8.
 */
public class DeleteRecordRequest extends RetrofitSpiceRequest<ValidicDeleteRecordModel,Validic> implements BaseRetroRequest<DeleteRecordRequestObject>{

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;

    public DeleteRecordRequest(String   organizationId,String   organizationTokenKey,String validicUserId,String  validicRecordId) {
        super(ValidicDeleteRecordModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }


    @Override
    public DeleteRecordRequestObject buildRequestBody() {
        DeleteRecordRequestObject object = new DeleteRecordRequestObject();
        object.setAccess_token(organizationTokenKey);
        return object;
    }

    @Override
    public ValidicDeleteRecordModel loadDataFromNetwork() throws Exception {
       return getService().deleteRecordRequest(buildRequestBody(),organizationId,validicUserId,validicRecordId,"application/json");
    }
}
