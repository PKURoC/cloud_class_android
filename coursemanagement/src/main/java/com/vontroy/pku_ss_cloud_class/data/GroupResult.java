package com.vontroy.pku_ss_cloud_class.data;

import java.util.ArrayList;

/**
 * Created by vontroy on 12/22/16.
 */

public class GroupResult extends BaseResult {
    private String name;
    private String about;
    private String id;
    private String ownername;
    private ArrayList<Student> groupMembers;
    private String courseid;
    private String invitation;
    private String data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Student> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(ArrayList<Student> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
