package com.vontroy.pku_ss_cloud_class.entry;

import com.vontroy.pku_ss_cloud_class.utils.Constants;

/**
 * Created by vontroy on 17-1-26.
 */

public class FileTransferInfo {
    private String fileName;
    private int fileLength;
    private String remarks;
    private String uuid;
    private FileUtils.FileType type;

    //JobAttachAdditionalData
    private String integrity;
    private String isSubmitJob;
    private String courseId;
    private String sid;
    private String groupId;  //课件资料gid="doc", 老师发布的作业资料gid="doc", 个人提交的作业gid="job", 小组资料gid=groupId, 小组作业gid=groupId
    private String id; //无意义
    private String jobid;
    private Constants.StorageType storageType; // "group", "storage", "jobAttach", "personalJob", "groupJob", "courseWare", "courseData"

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public FileUtils.FileType getType() {
        return type;
    }

    public void setType(FileUtils.FileType type) {
        this.type = type;
    }

    public String getIntegrity() {
        return integrity;
    }

    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    public String getIsSubmitJob() {
        return isSubmitJob;
    }

    public void setIsSubmitJob(String isSubmitJob) {
        this.isSubmitJob = isSubmitJob;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public Constants.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(Constants.StorageType storageType) {
        this.storageType = storageType;
    }
}
