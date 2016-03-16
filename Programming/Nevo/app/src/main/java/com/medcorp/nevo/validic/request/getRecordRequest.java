package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.ValidicReadRecordModel;

/**
 * Created by gaillysu on 16/3/8.
 */
public class getRecordRequest extends BaseSpringRequest<ValidicReadRecordModel> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;
    private String   validicRecordId;

    public getRecordRequest(String organizationId,String organizationTokenKey,String validicUserId,String validicRecordId) {
        super(ValidicReadRecordModel.class);

        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/fitness/%s.json?access_token=%s",organizationId,validicUserId,validicRecordId,organizationTokenKey);
    }

    @Override
    public String buildRequestBody() {
        return null;
    }

    @Override
    public ValidicReadRecordModel loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(buildRequestURL(),ValidicReadRecordModel.class);
    }
}
