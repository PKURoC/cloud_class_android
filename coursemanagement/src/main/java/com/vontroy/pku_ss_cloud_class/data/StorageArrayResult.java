package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-1.
 */

public class StorageArrayResult extends BaseResult {
    private ArrayList<StorageResult> data;

    public ArrayList<StorageResult> getData() {
        return data;
    }

    public void setData(ArrayList<StorageResult> data) {
        this.data = data;
    }
}
