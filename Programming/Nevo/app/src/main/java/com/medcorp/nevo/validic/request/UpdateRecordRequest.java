package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.ValidicRecordModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gaillysu on 16/3/8.
 */
public class UpdateRecordRequest extends BaseSpringRequest<ValidicRecordModel> {

    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicRecordId;
    private String   validicUserId;
    private int steps;

    public UpdateRecordRequest(String   organizationId,String   organizationTokenKey,String validicUserId,String  validicRecordId,int steps) {
        super(ValidicRecordModel.class);
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
        this.validicRecordId = validicRecordId;
        this.steps = steps;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/routine/%s.json",organizationId,validicUserId,validicRecordId);
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("routine",new JSONObject().put("steps",steps));
            json.put("access_token",organizationTokenKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public ValidicRecordModel loadDataFromNetwork() throws Exception {
        getRestTemplate().put(buildRequestURL(),buildRequestBody(), ValidicRecordModel.class);
        //TODO above put funtion() has no return object, here need read it again?
        return  null;
    }
}
