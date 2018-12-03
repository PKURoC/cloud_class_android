package com.vontroy.pku_ss_cloud_class.course;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;

import java.util.Map;

/**
 * Created by LinkedME06 on 16/11/9.
 */

public class CourseContract {
    interface View extends BaseView<CourseContract.Presenter> {
        void successJoinCourse();

        void successDropCourse();
    }

    interface Presenter extends BasePresenter {
        void JoinCourse(@NonNull Map params);

        void DropCourse(Map<String, String> params);
    }
}
