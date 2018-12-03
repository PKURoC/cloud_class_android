package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-18.
 */

public class CourseDocArrayResult extends BaseResult {
    private ArrayList<CourseDocResult> data;

    public ArrayList<CourseDocResult> getData() {
        return data;
    }

    public void setData(ArrayList<CourseDocResult> data) {
        this.data = data;
    }
}
