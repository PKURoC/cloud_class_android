package com.vontroy.pku_ss_cloud_class.data;

import java.io.Serializable;

/**
 * Created by LinkedME06 on 16/10/29.
 */

public class TestResult implements Serializable {

    //type postid两个参数
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String message;

}
