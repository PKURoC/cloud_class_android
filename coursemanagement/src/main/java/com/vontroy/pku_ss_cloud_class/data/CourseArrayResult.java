package com.vontroy.pku_ss_cloud_class.data;

import java.util.List;

/**
 * Created by vontroy on 16-11-19.
 */

public class CourseArrayResult extends BaseResult {
    private List<CourseResult> data;

    public CourseArrayResult(List<CourseResult> data) {
        this.data = data;
    }

    public List<CourseResult> getData() {
        return data;
    }

    public void setData(List<CourseResult> data) {
        this.data = data;
    }
}
