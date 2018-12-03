package com.vontroy.pku_ss_cloud_class.entry;

import android.os.Parcel;
import android.os.Parcelable;

import com.vontroy.pku_ss_cloud_class.utils.Constants;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageInfo implements Parcelable {
    private String fileName;
    private String jobName;
    private String remarks;
    private String uuid;
    private FileUtils.FileType type;

    //JobAttachAdditionalData
    private String integrity;
    private String isSubmitJob;
    private String courseId;
    private String courseName;
    private String sid;
    private String groupId;  //课件资料gid="doc", 老师发布的作业资料gid="doc", 个人提交的作业gid="job", 小组资料gid=groupId, 小组作业gid=groupId
    private String id; //无意义
    private String jobid;
    private Constants.StorageType storageType; // "group", "storage", "jobAttach", "personalJob", "groupJob", "courseWare", "courseData"
    private boolean localExists;
    private boolean none;

    public StorageInfo(String fileName, String remarks) {
        this.fileName = fileName;
        this.remarks = remarks;
    }

    public StorageInfo(String fileName, String remarks, FileUtils.FileType type) {
        this.fileName = fileName;
        this.remarks = remarks;
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.remarks);
        dest.writeString(this.uuid);
        dest.writeString(this.integrity);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected StorageInfo(Parcel in) {
        this.fileName = in.readString();
        this.remarks = in.readString();
        this.uuid = in.readString();
        this.integrity = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : FileUtils.FileType.values()[tmpType];
    }

    public static final Creator<StorageInfo> CREATOR = new Creator<StorageInfo>() {
        @Override
        public StorageInfo createFromParcel(Parcel source) {
            return new StorageInfo(source);
        }

        @Override
        public StorageInfo[] newArray(int size) {
            return new StorageInfo[size];
        }
    };

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public static Creator<StorageInfo> getCREATOR() {
        return CREATOR;
    }

    public Constants.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(Constants.StorageType storageType) {
        this.storageType = storageType;
    }

    public boolean isLocalExists() {
        return localExists;
    }

    public void setLocalExists(boolean localExists) {
        this.localExists = localExists;
    }

    public boolean isNone() {
        return none;
    }

    public void setNone(boolean none) {
        this.none = none;
    }
}
