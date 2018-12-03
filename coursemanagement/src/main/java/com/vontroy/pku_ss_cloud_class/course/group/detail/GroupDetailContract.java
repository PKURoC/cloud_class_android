package com.vontroy.pku_ss_cloud_class.course.group.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;

import java.util.Map;

/**
 * Created by vontroy on 2016-12-28.
 */

public class GroupDetailContract {
    interface View extends BaseView<Presenter> {
        @Nullable
        android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);

        void joinGroupSuccess();
    }

    interface Presenter extends BasePresenter {
        void joinGroup(Map params);
    }
}
