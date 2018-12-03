package com.vontroy.pku_ss_cloud_class.course.add;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddCourseContract {
    interface View extends BaseView<Presenter> {
        void listDataChanged();
    }

    interface Presenter extends BasePresenter {
        void setCourseList(ArrayList<CourseInfo> courseList);

        void setJoinedCourseInfos(ArrayList<CourseInfo> joinedCourseInfos);

        void getCourses();

        void getMyCourses(Map<String, String> params);
    }
}
