package com.vontroy.pku_ss_cloud_class.user_account.login;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.data.Student;

import java.util.Map;

/**
 * Created by Vontroy on 16/10/27.
 */

public class LoginContract {

    interface View extends BaseView<Presenter> {
        void success();

        void setToken(String token);

        void setSid(String sid);

        void setNick(String nick);

        void resetPwdSuccess(String msg);

        void fail(String msg);
    }

    interface Presenter extends BasePresenter {

        void login(@NonNull Student student);

        void forgotPwd(Map<String, String> params);
    }

}
