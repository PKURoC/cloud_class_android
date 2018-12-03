package com.vontroy.pku_ss_cloud_class.data;

import java.io.Serializable;

/**
 * Created by LinkedME06 on 16/10/28.
 */

public class BaseResult implements Serializable {

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
