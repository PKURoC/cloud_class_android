package com.vontroy.pku_ss_cloud_class.entry;

import java.io.Serializable;

/**
 * Created by Vontroy on 2016-10-11.
 */

public class CourseInfo implements Serializable {

    private String courseId;
    private int studentNum;
    private String courseName;
    private String courseRealName;
    private String courseTeacher;
    private String courseIntroduction;
    private String other;
    private String classroom;
    private String ownerId;
    private String classTime;
    private String classTime2;
    private boolean selectedCurrently;


    public CourseInfo(String courseName, String courseTeacher) {
        this.courseName = courseName;
        this.courseTeacher = courseTeacher;
    }

    public CourseInfo(String courseName, String courseTeacher, String courseIntroduction) {
        this.courseName = courseName;
        this.courseTeacher = courseTeacher;
        this.courseIntroduction = courseIntroduction;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseRealName() {
        return courseRealName;
    }

    public void setCourseRealName(String courseRealName) {
        this.courseRealName = courseRealName;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseIntroduction() {
        return courseIntroduction;
    }

    public void setCourseIntroduction(String courseIntroduction) {
        this.courseIntroduction = courseIntroduction;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassTime2() {
        return classTime2;
    }

    public void setClassTime2(String classTime2) {
        this.classTime2 = classTime2;
    }

    public boolean isSelectedCurrently() {
        return selectedCurrently;
    }

    public void setSelectedCurrently(boolean selectedCurrently) {
        this.selectedCurrently = selectedCurrently;
    }
}