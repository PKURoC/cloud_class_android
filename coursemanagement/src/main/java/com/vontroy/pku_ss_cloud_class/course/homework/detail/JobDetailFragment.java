package com.vontroy.pku_ss_cloud_class.course.homework.detail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.StorageListAdapter;
import com.vontroy.pku_ss_cloud_class.databinding.JobDetailFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.JobDetailInfo;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 2016-12-30.
 */

public class JobDetailFragment extends Fragment implements JobDetailContract.View {
    private JobDetailFragBinding jobDetailFragBinding;
    private JobDetailContract.Presenter mPresenter;
    private static final int REQUEST_CODE = 1010;
    private String jobFilePath;
    private StorageListAdapter jobAttachAdapter;
    private StorageListAdapter submissionAdapter;
    private ArrayList<StorageInfo> jobAttaches;
    private ArrayList<StorageInfo> submittedFileInfos;

    private File[] downloadedFileItems;

    public static JobDetailFragment newInstance() {
        return new JobDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.job_detail_frag, container, false);
        jobDetailFragBinding = JobDetailFragBinding.bind(root);
        Bundle bundle = getArguments();
        final JobDetailInfo jobDetailInfo = (JobDetailInfo) bundle.getSerializable("jobDetailInfo");
        String courseName = jobDetailInfo.getCoursename();

        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");

        jobAttaches = new ArrayList<>();

        jobAttachAdapter = new StorageListAdapter(getActivity(), jobAttaches, "all");

        String courseId = jobDetailInfo.getCourseid();

        submittedFileInfos = new ArrayList<>();

//        StorageInfo submittedFile = new StorageInfo(jobDetailInfo.getFilename(), "");
//        String type = jobDetailInfo.getType();
//        if ("2".equals(type)) {
//            submittedFile.setStorageType(Constants.StorageType.PERSONAL_JOB);
//        } else if ("1".equals(type)) {
//            submittedFile.setStorageType(Constants.StorageType.GROUP_JOB);
//        }
//        submittedFile.setJobid(jobDetailInfo.getJobid());
//        submittedFile.setCourseId(jobDetailInfo.getCourseid());
//        submittedFile.setCourseName(courseName);
//        submittedFile.setJobName(jobDetailInfo.getName());
//        submittedFile.setUuid(jobDetailInfo.getUuid());
//        submittedFileInfos.add(submittedFile);
        submissionAdapter = new StorageListAdapter(getActivity(), submittedFileInfos);

        jobDetailFragBinding.jobDetailName.setText(jobDetailInfo.getName());
        jobDetailFragBinding.jobDetailAbout.setText(jobDetailInfo.getAbout());
        jobDetailFragBinding.submitTime.setText(jobDetailInfo.getSubmittime());
        jobDetailFragBinding.deadLine.setText(jobDetailInfo.getDeadline());
        jobDetailFragBinding.attachList.setAdapter(jobAttachAdapter);
        setListViewHeightBasedOnChildren(jobDetailFragBinding.attachList);

        if (jobDetailInfo.isFinished()) {
            jobDetailFragBinding.mySubmission.setAdapter(submissionAdapter);
            setListViewHeightBasedOnChildren(jobDetailFragBinding.mySubmission);
        } else {
            jobDetailFragBinding.mySubmissionLl.setVisibility(View.GONE);
        }

        jobDetailFragBinding.selectJobFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });


        jobDetailFragBinding.submitJobBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Map<String, String> submitJobParams = new HashMap<String, String>();
                submitJobParams.put("token", token);
                submitJobParams.put("sid", sid);
                submitJobParams.put("jid", jobDetailInfo.getJobid());
                submitJobParams.put("jtype", jobDetailInfo.getType());
                submitJobParams.put("cid", jobDetailInfo.getCourseid());

                mPresenter.submitJob(submitJobParams);
                //uploadJobFile(jobDetailInfo.getUuid());
            }
        });

        Map<String, String> params = new HashMap<>();
        String jid = jobDetailInfo.getJobid();
        params.put("sid", sid);
        params.put("token", token);
        params.put("jid", jid);

        mPresenter.setJobFileInfo(jobAttaches);
        mPresenter.getJobFiles(params, courseName, jobDetailInfo.getName());

        Map<String, String> getSubmittedJobFilesParams = new HashMap<>();
        getSubmittedJobFilesParams.put("sid", sid);
        getSubmittedJobFilesParams.put("cid", courseId);
        getSubmittedJobFilesParams.put("token", token);

        mPresenter.setSubmittedFileInfos(submittedFileInfos);
        mPresenter.getSubmittedJobFiles(getSubmittedJobFilesParams, courseName, courseId, jobDetailInfo.getName(), jobDetailInfo.getJobid());

        return root;
    }

    @Override
    public void setPresenter(@NonNull JobDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Uri uri = data.getData();
                jobFilePath = FilePath.getPath(getActivity(), uri);
                jobDetailFragBinding.jobFilePath.setText(jobFilePath);
            }
        }
    }

    @Override
    public void attachDataChanged() {
        jobAttachAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(jobDetailFragBinding.attachList);
    }

    @Override
    public void uploadJobFile(String uuid) {
        try {
            ServerImp.getInstance().uploadMultipart(getActivity(), jobFilePath, uuid, false);
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(-1, -1);  //<span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在还没有构建View 之前无法取得View的度宽。 </span><span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在此之前我们必须选 measure 一下. </span><br style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void submittedFileInfosChanged() {
        submissionAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(jobDetailFragBinding.mySubmission);
    }

    @Override
    public void showSubmitJobMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
