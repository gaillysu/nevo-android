package com.medcorp.nevo.validic.request;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Created by gaillysu on 16/3/8.
 */
public class CreateUserRequest extends BaseRequest<ValidicUser>{

    private String nevouser_id;
    private String   access_token;
    private String   organization_id;
    private String   pincode;

    public CreateUserRequest(String nevouser_id,String organization_id,String access_token,String pincode) {
        super(ValidicUser.class);
        this.nevouser_id = nevouser_id;
        this.organization_id = organization_id;
        this.access_token = access_token;
        this.pincode = pincode;
    }

    @Override
    public ValidicUser loadDataFromNetwork() throws Exception {
        ResponseEntity<ValidicUser> result =  getRestTemplate().postForEntity(buildRequestURL(),buildRequestBody(), ValidicUser.class);
        return result!=null?result.getBody():null;
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/authorization/new_user",organization_id);
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("user",new JSONObject().put("uid",nevouser_id));
            json.put("access_token",access_token);
            json.put("pin",pincode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
