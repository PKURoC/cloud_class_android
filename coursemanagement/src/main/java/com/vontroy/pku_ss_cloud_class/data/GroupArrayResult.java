package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-12-27.
 */

public class GroupArrayResult extends BaseResult {
    private ArrayList<GroupResult> data;

    public ArrayList<GroupResult> getData() {
        return data;
    }

    public void setData(ArrayList<GroupResult> data) {
        this.data = data;
    }
}
