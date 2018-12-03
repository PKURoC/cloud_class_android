package com.vontroy.pku_ss_cloud_class.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by vontroy on 16-12-22.
 */

public class GroupDetailResult extends BaseResult {
    private JsonObject data;
    private JsonArray members;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public JsonArray getMembers() {
        return members;
    }

    public void setMembers(JsonArray members) {
        this.members = members;
    }
}
