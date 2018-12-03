package com.vontroy.pku_ss_cloud_class.data;

/**
 * Created by vontroy on 17-1-1.
 */

public class StorageResult extends BaseResult {
    private String uuid;
    private String filename;
    private String integrity;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }
}
