package com.vontroy.pku_ss_cloud_class.data;

import com.google.gson.JsonObject;

/**
 * Created by vontroy on 2017-01-12.
 */

public class MsgResult extends BaseResult {
    private JsonObject data;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
