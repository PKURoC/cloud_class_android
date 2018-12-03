package com.vontroy.pku_ss_cloud_class.data;

/**
 * Created by vontroy on 17-1-20.
 */

public class JobAttachValueDetailResult extends BaseResult {
    private String integrity;
    private String filename;
    private String issubmitjob;
    private String id;
    private String courseid;
    private String sid;

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIssubmitjob() {
        return issubmitjob;
    }

    public void setIssubmitjob(String issubmitjob) {
        this.issubmitjob = issubmitjob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
