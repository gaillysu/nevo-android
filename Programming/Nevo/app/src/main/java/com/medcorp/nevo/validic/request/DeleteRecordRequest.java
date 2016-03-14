package com.medcorp.nevo.validic.request;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by gaillysu on 16/3/8.
 */
public class DeleteRecordRequest extends BaseRequest<Void> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;

    public DeleteRecordRequest(String   organizationId,String   organizationTokenKey,String validicUserId,String  validicRecordId) {
        super(Void.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/routine/%s.json",organizationId,validicUserId,validicRecordId);
    }

    @Override
    public String buildRequestBody() {
        return null;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("access_token",organizationTokenKey);
        getRestTemplate().delete(buildRequestURL(),map);
        return null;
    }
}
