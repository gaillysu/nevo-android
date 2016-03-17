package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.ValidicReadAllRecordsModel;
import com.medcorp.nevo.validic.retrofit.Validic;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by gaillysu on 16/3/8.
 */
public class GetAllRecordsRequest extends RetrofitSpiceRequest<ValidicReadAllRecordsModel,Validic> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public GetAllRecordsRequest(String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicReadAllRecordsModel.class,Validic.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public ValidicReadAllRecordsModel loadDataFromNetwork() throws Exception {
        return getService().getAllRecordsRequest(organizationId,validicUserId,organizationTokenKey);
    }
}
