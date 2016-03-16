package com.medcorp.nevo.validic.request;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.ValidicRecord;
import com.medcorp.nevo.validic.model.ValidicRecordModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddRecordRequest  extends BaseSpringRequest<ValidicRecordModel> {

    private ValidicRecord record;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddRecordRequest(ValidicRecord record,String organizationId,String organizationTokenKey,String validicUserId) {
        super(ValidicRecordModel.class);
        this.record = record;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/routine.json",organizationId,validicUserId);
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("routine", new Gson().toJson(record))
                    .put("access_token", organizationTokenKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public ValidicRecordModel loadDataFromNetwork() throws Exception {
        ResponseEntity<ValidicRecordModel> result =  getRestTemplate().postForEntity(buildRequestURL(),buildRequestBody(), ValidicRecordModel.class);
        return  result!=null?result.getBody():null;
    }
}
