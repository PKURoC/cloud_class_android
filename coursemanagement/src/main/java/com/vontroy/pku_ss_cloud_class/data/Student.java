package com.vontroy.pku_ss_cloud_class.data;

import java.io.Serializable;

/**
 * Created by LinkedME06 on 16/10/27.
 */

public final class Student implements Serializable {

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    private String sid;
    private String password;
    private String nick;

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypePassword) {
        this.retypedPassword = retypePassword;
    }

    private String retypedPassword;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
