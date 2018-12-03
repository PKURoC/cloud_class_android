package com.vontroy.pku_ss_cloud_class.entry;

import com.vontroy.pku_ss_cloud_class.data.Student;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vontroy on 2016-12-22.
 */

public class GroupInfo implements Serializable {
    private String groupName;
    private String groupIntroduction;
    private String groupId;
    private String ownerName;
    private String courseId;
    private String invitation;
    private ArrayList<Student> groupMembers;

    public GroupInfo(String groupName, String groupIntroduction) {
        this.groupName = groupName;
        this.groupIntroduction = groupIntroduction;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIntroduction() {
        return groupIntroduction;
    }

    public void setGroupIntroduction(String groupIntroduction) {
        this.groupIntroduction = groupIntroduction;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public ArrayList<Student> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<Student> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
