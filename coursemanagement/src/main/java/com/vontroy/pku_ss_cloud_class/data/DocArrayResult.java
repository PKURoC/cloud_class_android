package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-2.
 */

public class DocArrayResult extends BaseResult {
    ArrayList<DocResult> data;

    public ArrayList<DocResult> getData() {
        return data;
    }

    public void setData(ArrayList<DocResult> data) {
        this.data = data;
    }
}
