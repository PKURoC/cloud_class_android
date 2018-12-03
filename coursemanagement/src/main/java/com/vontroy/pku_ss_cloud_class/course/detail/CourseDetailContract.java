package com.vontroy.pku_ss_cloud_class.course.detail;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by LinkedME06 on 16/11/10.
 */

public class CourseDetailContract {
    interface View extends BaseView<Presenter> {
        void listDataChanged();
    }

    interface Presenter extends BasePresenter {

        void setCourseDatas(ArrayList<StorageInfo> coursedatas);

        void setCourseWares(ArrayList<StorageInfo> coursewares);

        void getCourseDocs(Map params, String courseName, String courseId);
    }
}
