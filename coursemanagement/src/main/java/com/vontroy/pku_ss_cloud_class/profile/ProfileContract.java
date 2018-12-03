package com.vontroy.pku_ss_cloud_class.profile;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-17.
 */

public class ProfileContract {
    interface View extends BaseView<Presenter> {

        void listDataChanged();

        void getCourseInfosSuccess();

        void updateNickSuccess(String nick);
    }

    interface Presenter extends BasePresenter {
        void setCourseInfos(ArrayList<CourseInfo> courseInfos);

        void getMyCourses(Map params);

        void modifyNick(Map<String, String> params);
    }
}
