package com.vontroy.pku_ss_cloud_class.course.homework.detail;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 2016-12-30.
 */

public class JobDetailContract {
    interface View extends BaseView<Presenter> {
        void attachDataChanged();

        void uploadJobFile(String uuid);

        void submittedFileInfosChanged();

        void showSubmitJobMsg(String msg);
    }

    interface Presenter extends BasePresenter {

        void setJobFileInfo(ArrayList<StorageInfo> jobFileInfos);

        void getJobFiles(Map<String, String> params, String courseName, String jobName);

        void submitJob(@NonNull Map<String, String> params);

        void setSubmittedFileInfos(ArrayList<StorageInfo> submittedFileInfos);

        void getSubmittedJobFiles(Map params, String courseName, String courseId, String jobName, String jobId);
    }
}
