package com.vontroy.pku_ss_cloud_class.home;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-15.
 */

public class HomeContract {
    interface View extends BaseView<Presenter> {

        void markAllMsgAsHasReadSuccess();

        void markSingleMsgAsHasReadSuccess();

        void getRecentMsgSuccess();

        void courseDataChanged();
    }

    interface Presenter extends BasePresenter {
        void setCourseInfos(ArrayList<CourseInfo> courseInfos);

        void setRecentMsgs(ArrayList<MyMsgInfo> recentMsgs);

        void getMyCourses(Map params);

        void getRecent(Map params);

        void hasRead(Map params);

        void hasReadAll(Map params);

        void addCourse();
    }
}
