package com.vontroy.pku_ss_cloud_class.course.homework.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.JobAdapter;
import com.vontroy.pku_ss_cloud_class.course.homework.detail.JobDetailActivity;
import com.vontroy.pku_ss_cloud_class.databinding.HomeworkFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.JobDetailInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class HomeworkFragment extends Fragment implements HomeworkContract.View, TabHost.TabContentFactory {
    private HomeworkFragBinding homeworkFragBinding;
    private HomeworkContract.Presenter mPresenter;
    private JobAdapter finishedJobAdapter;
    private JobAdapter unfinishedJobAdapter;

    private ArrayList<JobDetailInfo> finishedJobDetailInfos;
    private ArrayList<JobDetailInfo> unfinishedJobDetailInfos;

    public static HomeworkFragment newInstance() {
        return new HomeworkFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String token = getParams.getString("token", "");
        String sid = getParams.getString("sid", "");

        Bundle bundle = getArguments();
        final String cid = bundle.getString("course_id");

        finishedJobDetailInfos = new ArrayList<>();
        unfinishedJobDetailInfos = new ArrayList<>();

        finishedJobAdapter = new JobAdapter(this.getActivity(), finishedJobDetailInfos);

        homeworkFragBinding.homeworkListFinished.setAdapter(finishedJobAdapter);
        setListViewHeightBasedOnChildren(homeworkFragBinding.homeworkListFinished);

        homeworkFragBinding.homeworkListFinished.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);

                Bundle jobDetailBundle = new Bundle();
                JobDetailInfo finished = finishedJobDetailInfos.get(i);
                finished.setCourseid(cid);
                jobDetailBundle.putSerializable("jobDetailInfo", finished);

                intent.putExtras(jobDetailBundle);

                startActivity(intent);
            }
        });


        unfinishedJobAdapter = new JobAdapter(this.getActivity(), unfinishedJobDetailInfos);
        homeworkFragBinding.homeworkListUnfinished.setAdapter(unfinishedJobAdapter);
        setListViewHeightBasedOnChildren(homeworkFragBinding.homeworkListUnfinished);

        homeworkFragBinding.homeworkListUnfinished.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), JobDetailActivity.class);

                Bundle jobDetailBundle = new Bundle();
                JobDetailInfo unfinished = unfinishedJobDetailInfos.get(i);
                unfinished.setCourseid(cid);
                jobDetailBundle.putSerializable("jobDetailInfo", unfinished);

                intent.putExtras(jobDetailBundle);

                startActivity(intent);
            }
        });

        Map<String, String> params = new HashMap<>();

        params.put("cid", bundle.getString("course_id"));
        params.put("sid", sid);
        params.put("token", token);

        if (!finishedJobDetailInfos.isEmpty()) {
            finishedJobDetailInfos.clear();
        }
        if (!unfinishedJobDetailInfos.isEmpty()) {
            unfinishedJobDetailInfos.clear();
        }

        mPresenter.setFinishedJobDetailInfos(finishedJobDetailInfos);
        mPresenter.setUnfinishedJobDetailInfos(unfinishedJobDetailInfos);
        mPresenter.getJobs(params, bundle.getString("course_name"));
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.homework_frag, container, false);
        homeworkFragBinding = HomeworkFragBinding.bind(root);

        return root;
    }

    /***
     * 动态设置listview的高度
     *
     * @param listView
     */
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
    public void setPresenter(@NonNull HomeworkContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void listDataChanged() {
        finishedJobAdapter.notifyDataSetChanged();
        if (finishedJobDetailInfos.isEmpty() || "NONE".equals(finishedJobDetailInfos.get(0).getType())) {
            homeworkFragBinding.homeworkListFinished.setEnabled(false);
        } else {
            homeworkFragBinding.homeworkListFinished.setEnabled(true);
        }
        setListViewHeightBasedOnChildren(homeworkFragBinding.homeworkListFinished);

        unfinishedJobAdapter.notifyDataSetChanged();
        if (unfinishedJobDetailInfos.isEmpty() || "NONE".equals(unfinishedJobDetailInfos.get(0).getType())) {
            homeworkFragBinding.homeworkListUnfinished.setEnabled(false);
        } else {
            homeworkFragBinding.homeworkListUnfinished.setEnabled(true);
        }
        setListViewHeightBasedOnChildren(homeworkFragBinding.homeworkListUnfinished);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}
