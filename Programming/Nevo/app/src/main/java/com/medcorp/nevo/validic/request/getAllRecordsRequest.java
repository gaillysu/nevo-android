package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.ValidicReadAllRecordsModel;
import com.medcorp.nevo.validic.model.ValidicRecord;

/**
 * Created by gaillysu on 16/3/8.
 */
public class getAllRecordsRequest extends BaseRequest<ValidicReadAllRecordsModel>{

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public getAllRecordsRequest(String   organizationId,String   organizationTokenKey,String   validicUserId) {
        super(ValidicReadAllRecordsModel.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/fitness.json?access_token=%s",organizationId,validicUserId,organizationTokenKey);
    }

    @Override
    public String buildRequestBody() {
        return null;
    }

    @Override
    public ValidicReadAllRecordsModel loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(buildRequestURL(),ValidicReadAllRecordsModel.class);
    }
}
