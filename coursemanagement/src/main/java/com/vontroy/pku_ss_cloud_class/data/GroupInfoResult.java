package com.vontroy.pku_ss_cloud_class.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by vontroy on 16-12-22.
 */

public class GroupInfoResult extends BaseResult {
    private JsonArray members;
    private JsonObject groupinfo;

    public JsonArray getMembers() {
        return members;
    }

    public void setMembers(JsonArray members) {
        this.members = members;
    }

    public JsonObject getGroupinfo() {
        return groupinfo;
    }

    public void setGroupinfo(JsonObject groupinfo) {
        this.groupinfo = groupinfo;
    }
}
