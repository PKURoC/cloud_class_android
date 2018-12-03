package com.vontroy.pku_ss_cloud_class.data;

import com.google.gson.JsonObject;

/**
 * Created by LinkedME06 on 16/10/29.
 */

public class LoginResult extends BaseResult {
    private String token;
    private JsonObject data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
