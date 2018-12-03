package com.vontroy.pku_ss_cloud_class.course.JoinedCourse;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-22.
 */

public class JoinedCourseContract {
    interface View extends BaseView<JoinedCourseContract.Presenter> {
        void listDataChanged();

        void SuccessDropCourse();
    }

    interface Presenter extends BasePresenter {
        void setCourseList(ArrayList<CourseInfo> courseList);

        void getMyCourses(Map params);

        void DropCourse(Map params);

        void subscribe(Map params);
    }
}
