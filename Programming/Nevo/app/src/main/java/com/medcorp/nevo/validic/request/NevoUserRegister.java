package com.medcorp.nevo.validic.request;

import com.medcorp.nevo.validic.model.NevoUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.Date;

/**
 * Created by gaillysu on 16/3/14.
 */
public class NevoUserRegister extends BaseSpringRequest<NevoUser> {

    private String email;
    private String password;

    public NevoUserRegister(String email, String password) {
        super(NevoUser.class);
        this.email = email;
        this.password = password;
    }

    @Override
    public String buildRequestURL() {
        return String.format("http://api.nevowatch.com/api/account/register");
    }

    @Override
    public String buildRequestBody() {
        JSONObject json = new JSONObject();
        JSONObject jsonparams = new JSONObject();
        long time = new Date().getTime()/1000;
        String token_key = String.format("%d-nevo2015medappteam",time);
        String token= new String(Hex.encodeHex(DigestUtils.md5(token_key)));
        try {
            jsonparams.put("time",time);
            jsonparams.put("check_key",token);
            json.put("params",jsonparams.toString());
            json.put("user",email);
            json.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public NevoUser loadDataFromNetwork() throws Exception {
        ResponseEntity<NevoUser> result = getRestTemplate().postForEntity(buildRequestURL(),buildRequestBody(),NevoUser.class);
        return result!=null?result.getBody():null;
    }
}
