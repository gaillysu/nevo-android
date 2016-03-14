package com.medcorp.nevo.validic.request;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.model.ValidicRecord;
import com.medcorp.nevo.validic.model.ValidicRecordModel;
import com.medcorp.nevo.validic.model.ValidicRecordMoreModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Created by gaillysu on 16/3/8.
 */
public class AddMoreRecordsRequest extends BaseRequest<ValidicRecordMoreModel>{

    private List<ValidicRecord> records;
    private String   organizationId;
    private String   organizationTokenKey;
    private String   validicUserId;

    public AddMoreRecordsRequest(List<ValidicRecord> records, String organizationId, String organizationTokenKey, String validicUserId) {
        super(ValidicRecordMoreModel.class);
        this.records = records;
        this.organizationId = organizationId;
        this.organizationTokenKey = organizationTokenKey;
        this.validicUserId = validicUserId;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users/%s/fitness.json",organizationId,validicUserId);
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("fitness",new Gson().toJson(records))
                    .put("access_token", organizationTokenKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public ValidicRecordMoreModel loadDataFromNetwork() throws Exception {
        ResponseEntity<ValidicRecordMoreModel> result =  getRestTemplate().postForEntity(buildRequestURL(),buildRequestBody(), ValidicRecordMoreModel.class);
        return  result!=null?result.getBody():null;
    }
}
