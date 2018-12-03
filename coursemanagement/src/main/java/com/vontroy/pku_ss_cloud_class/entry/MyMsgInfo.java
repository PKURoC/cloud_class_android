package com.vontroy.pku_ss_cloud_class.entry;

/**
 * Created by vontroy on 16-11-17.
 */

public class MyMsgInfo {
    private String title;
    private String content;
    private String state;
    private String time;
    private String notifyId;
    private String accurateTime;

    public MyMsgInfo(String title) {
        this.title = title;
    }

    public MyMsgInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public MyMsgInfo(String title, String content, String state) {
        this.title = title;
        this.content = content;
        this.state = state;
    }

    public MyMsgInfo(String title, String content, String state, String time) {
        this.title = title;
        this.content = content;
        this.state = state;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getAccurateTime() {
        return accurateTime;
    }

    public void setAccurateTime(String accurateTime) {
        this.accurateTime = accurateTime;
    }
}
