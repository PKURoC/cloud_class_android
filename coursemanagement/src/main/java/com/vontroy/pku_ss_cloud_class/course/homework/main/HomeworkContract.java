package com.vontroy.pku_ss_cloud_class.course.homework.main;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.JobDetailInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-16.
 */

public class HomeworkContract {
    interface View extends BaseView<HomeworkContract.Presenter> {
        void listDataChanged();
    }

    interface Presenter extends BasePresenter {

        void setFinishedJobDetailInfos(ArrayList<JobDetailInfo> jobInfos);

        void setUnfinishedJobDetailInfos(ArrayList<JobDetailInfo> unfinishedJobDetailInfos);

        void getJobs(Map params, String courseName);

        void getMyDocs(Map params);
    }
}
