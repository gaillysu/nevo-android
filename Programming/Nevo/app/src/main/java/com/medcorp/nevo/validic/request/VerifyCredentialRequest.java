package com.medcorp.nevo.validic.request;

import android.util.Log;

import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import org.json.JSONObject;

/**
 * Created by gaillysu on 16/3/8.
 */
public class VerifyCredentialRequest extends BaseRequest<VerifyCredentialModel> {

    private String   access_token;
    private String   organization_id;

    public VerifyCredentialRequest(String   organization_id,String   access_token) {
        super(VerifyCredentialModel.class);
        this.organization_id = organization_id;
        this.access_token = access_token;
    }

    @Override
    public VerifyCredentialModel loadDataFromNetwork() throws Exception {
       return getRestTemplate().getForObject(buildRequestURL(),VerifyCredentialModel.class);
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s.json?access_token=%s",organization_id,access_token);
    }

    @Override
    public String buildRequestBody() {
        return null;
    }
}