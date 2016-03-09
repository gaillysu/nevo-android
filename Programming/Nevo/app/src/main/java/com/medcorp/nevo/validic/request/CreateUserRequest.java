package com.medcorp.nevo.validic.request;

import com.google.gson.Gson;
import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Created by gaillysu on 16/3/8.
 */
public class CreateUserRequest extends BaseRequest<ValidicUser>{

    private NevoUser user;
    private String   access_token;
    private String   organization_id;

    public CreateUserRequest(NevoUser user,String organization_id,String access_token) {
        super(ValidicUser.class);
        this.user = user;
        this.organization_id = organization_id;
        this.access_token = access_token;
    }

    @Override
    public ValidicUser loadDataFromNetwork() throws Exception {
        ResponseEntity<ValidicUser> result =  getRestTemplate().postForEntity(buildRequestURL(),buildRequestBody(), ValidicUser.class);
        return result.getBody();
    }

    @Override
    public String buildRequestURL() {
        return String.format("https://api.validic.com/v1/organizations/%s/users.json",organization_id);
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("user",new Gson().toJson(user));
            json.put("access_token",access_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
