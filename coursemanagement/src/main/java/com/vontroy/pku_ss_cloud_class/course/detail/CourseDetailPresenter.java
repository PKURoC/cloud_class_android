package com.vontroy.pku_ss_cloud_class.course.detail;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.CourseDocArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseDocResult;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/11/10.
 */

public class CourseDetailPresenter implements CourseDetailContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final CourseDetailContract.View mCourseDetailView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;


    private ArrayList<StorageInfo> coursedatas;
    private ArrayList<StorageInfo> coursewares;

    private File[] downloadedCourseWareItems;
    private File[] downloadedCourseDataItems;

    public CourseDetailPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                                 @NonNull CourseDetailContract.View view,
                                 @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mCourseDetailView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mCourseDetailView.setPresenter(this);
    }

    @Override
    public void setCourseDatas(ArrayList<StorageInfo> coursedatas) {
        this.coursedatas = coursedatas;
    }

    @Override
    public void setCourseWares(ArrayList<StorageInfo> coursewares) {
        this.coursewares = coursewares;
    }

    @Override
    public void getCourseDocs(Map params, final String courseName, final String courseId) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getCourseDocs, params, CourseDocArrayResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<CourseDocArrayResult>() {
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
                    public void onNext(CourseDocArrayResult courseDocArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(courseDocArrayResult.getCode())) {
                            ArrayList<CourseDocResult> courseDocResults = courseDocArrayResult.getData();
                            readDownloadedFiles(courseName);
                            for (CourseDocResult courseDocResult : courseDocResults) {
                                String fileName = courseDocResult.getFilename();
                                String[] fileNameSeg = fileName.split("\\.");
                                String type;
                                int segLen = fileNameSeg.length;
                                if (segLen == 1) {
                                    type = "other";
                                } else {
                                    type = fileNameSeg[segLen - 1];
                                }

                                StorageInfo storageInfo = new StorageInfo("", "");
                                storageInfo.setFileName(fileName);
                                storageInfo.setUuid(courseDocResult.getUuid());
                                storageInfo.setRemarks(type);
                                storageInfo.setIntegrity(courseDocResult.getIntegrity());
                                storageInfo.setCourseName(courseName);
                                storageInfo.setCourseId(courseId);

                                storageInfo.setLocalExists(false);
                                if ("0".equals(courseDocResult.getType())) {
                                    storageInfo.setStorageType(Constants.StorageType.COURSE_WARE);
                                    if (downloadedCourseWareItems != null) {
                                        for (File courseWareFile : downloadedCourseWareItems) {
                                            if (storageInfo.getFileName().equals(courseWareFile.getName())) {
                                                storageInfo.setLocalExists(true);
                                                break;
                                            }
                                        }
                                    }
                                    coursewares.add(storageInfo);
                                } else {
                                    storageInfo.setStorageType(Constants.StorageType.COURSE_DATA);
                                    if (downloadedCourseDataItems != null) {
                                        for (File courseDataFile : downloadedCourseDataItems) {
                                            if (storageInfo.getFileName().equals(courseDataFile.getName())) {
                                                storageInfo.setLocalExists(true);
                                                break;
                                            }
                                        }
                                    }
                                    coursedatas.add(storageInfo);
                                }
                            }

                            if (coursewares.size() == 0) {
                                StorageInfo noCourseWare = new StorageInfo("暂无课件", "");
                                noCourseWare.setStorageType(Constants.StorageType.COURSE_WARE);
                                noCourseWare.setNone(true);
                                coursewares.add(noCourseWare);
                            }

                            if (coursedatas.size() == 0) {
                                StorageInfo noCourseData = new StorageInfo("暂无资料", "");
                                noCourseData.setStorageType(Constants.StorageType.COURSE_DATA);
                                noCourseData.setNone(true);
                                coursedatas.add(noCourseData);
                            }

                            mCourseDetailView.listDataChanged();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }


//        Map<String, String> pa = new HashMap<>();
//        pa.put("type","test110");
//        pa.put("postid", "dd");
//        Subscription subscription = mServerImp
//                .common(requestTag, Request.Method.GET, ServerInterface.testUrl, pa, TestResult.class)
//                .subscribeOn(mSchedulerProvider.computation())
//                .observeOn(mSchedulerProvider.ui())
//                .subscribe(new Observer<TestResult>() {
//                    @Override
//                    public void onCompleted() {
//                        //mTaskDetailView.setLoadingIndicator(false);
//                        Log.d("ddd", "onCompleted: ");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("ddd", "onError: ");
//                    }
//
//                    @Override
//                    public void onNext(TestResult loginResult) {
//                        Log.d("ddd", "onNext: " + loginResult.getStatus());
//                        //showTask(task);
//                    }
//                });
//        mSubscriptions.add(subscription);

    @Override
    public void subscribe() {
        //此处为页面打开后开始加载数据时调用的方法
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    public void readDownloadedFiles(String courseName) {
        String courseWareDir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课件";
        String courseDataDir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课程资料";

        File downloadedCourseWareDir = new File(courseWareDir);
        File downloadedCourseDataDir = new File(courseDataDir);

        if (downloadedCourseDataDir.exists()) {
            downloadedCourseDataItems = downloadedCourseDataDir.listFiles();
        }

        if (downloadedCourseWareDir.exists()) {
            downloadedCourseWareItems = downloadedCourseWareDir.listFiles();
        }
    }
}