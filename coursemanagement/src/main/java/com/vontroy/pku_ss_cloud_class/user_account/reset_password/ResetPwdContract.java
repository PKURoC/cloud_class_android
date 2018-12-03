package com.vontroy.pku_ss_cloud_class.user_account.reset_password;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;

import java.util.Map;

/**
 * Created by vontroy on 2017-02-24.
 */

public class ResetPwdContract {
    interface View extends BaseView<ResetPwdContract.Presenter> {
        void resetPwdSuccess();

        void resetPwdFailed(String msg);
    }

    interface Presenter extends BasePresenter {
        void resetPassword(Map<String, String> params);
    }
}
