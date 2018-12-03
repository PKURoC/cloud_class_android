package com.vontroy.pku_ss_cloud_class.data;

/**
 * Created by vontroy on 17-1-18.
 */

public class CourseDocResult extends BaseResult {
    private String filename;
    private String type;
    private String uuid;
    private String integrity;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }
}
