package com.vontroy.pku_ss_cloud_class.user_account.register;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.data.Student;

/**
 * Created by vontroy on 16-11-14.
 */

public class RegContract {
    interface View extends BaseView<RegContract.Presenter> {
        void success();

        void regFail(String msg);
    }

    interface Presenter extends BasePresenter {

        void reg(@NonNull Student student);

    }

}
