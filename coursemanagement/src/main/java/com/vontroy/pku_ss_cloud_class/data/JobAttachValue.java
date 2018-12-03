package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-20.
 */

public class JobAttachValue extends BaseResult {
    private ArrayList<JobAttachValueDetailResult> values;
    private String uuid;

    public ArrayList<JobAttachValueDetailResult> getValues() {
        return values;
    }

    public void setValues(ArrayList<JobAttachValueDetailResult> values) {
        this.values = values;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
