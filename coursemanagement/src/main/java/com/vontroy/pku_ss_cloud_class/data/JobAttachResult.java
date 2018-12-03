package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-20.
 */

public class JobAttachResult extends BaseResult {
    private ArrayList<JobAttachValue> data;

    public ArrayList<JobAttachValue> getData() {
        return data;
    }

    public void setData(ArrayList<JobAttachValue> data) {
        this.data = data;
    }
}
