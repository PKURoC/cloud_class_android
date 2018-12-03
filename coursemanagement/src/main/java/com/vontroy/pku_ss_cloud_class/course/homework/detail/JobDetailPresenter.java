package com.vontroy.pku_ss_cloud_class.course.homework.detail;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.JobArrayResult;
import com.vontroy.pku_ss_cloud_class.data.JobAttachResult;
import com.vontroy.pku_ss_cloud_class.data.JobAttachValue;
import com.vontroy.pku_ss_cloud_class.data.JobAttachValueDetailResult;
import com.vontroy.pku_ss_cloud_class.data.JobResult;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 2016-12-30.
 */

public class JobDetailPresenter implements JobDetailContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final JobDetailContract.View mJobDetailView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<StorageInfo> jobFileInfos;

    private ArrayList<StorageInfo> submittedFileInfos;

    private File[] downloadedFileItems;

    public JobDetailPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                              @NonNull JobDetailContract.View view,
                              @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mJobDetailView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mJobDetailView.setPresenter(this);
    }

    @Override
    public void setJobFileInfo(ArrayList<StorageInfo> jobFileInfos) {
        this.jobFileInfos = jobFileInfos;
    }

    @Override
    public void getJobFiles(Map<String, String> params, final String courseName, final String jobName) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getJobFiles, params, JobAttachResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JobAttachResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(JobAttachResult jobAttachResult) {
                        if (jobAttachResult.getCode().equals("0")) {
                            ArrayList<JobAttachValue> values = jobAttachResult.getData();
                            if (values.size() > 0) {
                                ArrayList<JobAttachValueDetailResult> value = values.get(0).getValues();
                                String uuid = values.get(0).getUuid();
                                readDownloadedFiles(Constants.StorageType.JOB_ATTACH, courseName, jobName);
                                for (JobAttachValueDetailResult valueElement : value) {
                                    StorageInfo jobFileInfo = new StorageInfo("", "");
                                    jobFileInfo.setCourseId(valueElement.getCourseid());
                                    jobFileInfo.setId(valueElement.getId());
                                    jobFileInfo.setIntegrity(valueElement.getIntegrity());
                                    jobFileInfo.setIsSubmitJob(valueElement.getIssubmitjob());
                                    jobFileInfo.setSid(valueElement.getSid());
                                    jobFileInfo.setFileName(valueElement.getFilename());
                                    jobFileInfo.setCourseName(courseName);
                                    jobFileInfo.setUuid(uuid);
                                    jobFileInfo.setJobName(jobName);
                                    jobFileInfo.setIntegrity(valueElement.getIntegrity());
                                    jobFileInfo.setStorageType(Constants.StorageType.JOB_ATTACH);
                                    jobFileInfo.setLocalExists(false);

                                    if (downloadedFileItems != null) {
                                        for (File file : downloadedFileItems) {
                                            String fileName = file.getName();
                                            if (fileName.equals(jobFileInfo.getFileName())) {
                                                jobFileInfo.setLocalExists(true);
                                                break;
                                            }
                                        }
                                    }

                                    jobFileInfos.add(jobFileInfo);
                                }
                            }
                        }
                        mJobDetailView.attachDataChanged();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void submitJob(@NonNull final Map<String, String> params) {
        updateJobFile(params);
    }

    @Override
    public void setSubmittedFileInfos(ArrayList<StorageInfo> submittedFileInfos) {
        this.submittedFileInfos = submittedFileInfos;
    }

    @Override
    public void getSubmittedJobFiles(Map params, final String courseName, final String courseId, final String jobName, final String jobId) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getMyJobs, params, JobArrayResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JobArrayResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(JobArrayResult jobArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (jobArrayResult.getCode().equals("0")) {
                            ArrayList<JobResult> finishedJobResults = jobArrayResult.getFinished();

                            for (JobResult finishedJobResult : finishedJobResults) {
                                if (!jobId.equals(finishedJobResult.getJobid())) {
                                    continue;
                                } else {
                                    StorageInfo submittedFile = new StorageInfo(finishedJobResult.getFilename(), "");
                                    String type = finishedJobResult.getType();
                                    if ("2".equals(type)) {
                                        submittedFile.setStorageType(Constants.StorageType.PERSONAL_JOB);
                                        readDownloadedFiles(Constants.StorageType.PERSONAL_JOB, courseName, jobName);
                                    } else if ("1".equals(type)) {
                                        submittedFile.setStorageType(Constants.StorageType.GROUP_JOB);
                                        readDownloadedFiles(Constants.StorageType.GROUP_JOB, courseName, jobName);
                                    }

                                    submittedFile.setJobid(finishedJobResult.getJobid());
                                    submittedFile.setCourseId(courseId);
                                    submittedFile.setIntegrity(finishedJobResult.getIntegrity());
                                    submittedFile.setCourseName(courseName);
                                    submittedFile.setJobName(finishedJobResult.getName());
                                    submittedFile.setUuid(finishedJobResult.getUuid());
                                    submittedFile.setLocalExists(false);
                                    if (downloadedFileItems != null) {
                                        for (File file : downloadedFileItems) {
                                            String fileName = file.getName();
                                            if (fileName.equals(finishedJobResult.getFilename())) {
                                                submittedFile.setLocalExists(true);
                                                break;
                                            }
                                        }
                                    }

                                    submittedFileInfos.add(submittedFile);
                                }
                            }
                            mJobDetailView.submittedFileInfosChanged();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    private void updateJobFile(final Map<String, String> params) {
        Subscription subscription = ServerImp.getInstance()
                .common(requestTag, Request.Method.POST, ServerInterface.deleteJob, params, BaseResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<BaseResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(BaseResult baseResult) {
                        Log.d("ddd", "onNext: ");
                        if (baseResult.getCode().equals("0")) {
                            submitJobFile(params);
                        } else if ("2".equals(baseResult.getCode())) {
                            String msg = "没有权限, 你不是小组组长!";
                            mJobDetailView.showSubmitJobMsg(msg);
                        }
                    }

                });
    }

    private void submitJobFile(final Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.submitJob, params, JobResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JobResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(JobResult jobResult) {
                        if (jobResult.getCode().equals("0")) {
                            String uuid = jobResult.getUuid();

                            mJobDetailView.uploadJobFile(uuid);
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }


    @Override
    public void subscribe() {
        //此处为页面打开后开始加载数据时调用的方法
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    public void readDownloadedFiles(Constants.StorageType storageType, String courseName, String jobName) {
        String dir = "";
        switch (storageType) {
            case JOB_ATTACH:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/作业附件";
                break;
            case PERSONAL_JOB:
            case GROUP_JOB:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/已提交作业";
                break;
        }

        File downloadedFileDir = new File(dir);

        if (!downloadedFileDir.exists()) {
            downloadedFileDir.mkdir();
        } else {
            downloadedFileItems = downloadedFileDir.listFiles();
        }
    }

//    private void updateFile(Map<String, String> params, final String uuid) {
//        Subscription subscription = mServerImp
//                .common(requestTag, Request.Method.GET, ServerInterface.updateFile, params, JobResult.class)
//                .subscribeOn(mSchedulerProvider.computation())
//                .observeOn(mSchedulerProvider.ui())
//                .subscribe(new Observer<JobResult>() {
//                    @Override
//                    public void onCompleted() {
//                        //mTaskDetailView.setLoadingIndicator(false);
//                        Log.d("ddd", "onCompleted: ");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("ddd", "onError: ");
//                        e.printStackTrace();
//
//                    }
//
//                    @Override
//                    public void onNext(JobResult jobResult) {
//                        if (jobResult.getCode().equals("0")) {
//                            mJobDetailView.uploadJobFile(uuid);
//                        }
//                    }
//
//                });
//        mSubscriptions.add(subscription);
//    }
}
