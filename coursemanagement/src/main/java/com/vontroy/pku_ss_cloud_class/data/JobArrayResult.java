package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 2016-12-30.
 */

public class JobArrayResult extends BaseResult {
    private ArrayList<JobResult> data;
    private ArrayList<JobResult> finished;
    private ArrayList<JobResult> unfinished;

    public ArrayList<JobResult> getData() {
        return data;
    }

    public void setData(ArrayList<JobResult> data) {
        this.data = data;
    }

    public ArrayList<JobResult> getFinished() {
        return finished;
    }

    public void setFinished(ArrayList<JobResult> finished) {
        this.finished = finished;
    }

    public ArrayList<JobResult> getUnfinished() {
        return unfinished;
    }

    public void setUnfinished(ArrayList<JobResult> unfinished) {
        this.unfinished = unfinished;
    }
}
