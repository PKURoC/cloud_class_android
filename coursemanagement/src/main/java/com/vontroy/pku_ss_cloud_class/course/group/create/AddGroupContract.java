package com.vontroy.pku_ss_cloud_class.course.group.create;


import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;

import java.util.Map;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddGroupContract {
    interface View extends BaseView<Presenter> {
        void createGroupSuccess();
    }

    interface Presenter extends BasePresenter {
        void addGroup(@NonNull Map params);
    }
}
