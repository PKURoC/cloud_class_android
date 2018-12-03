package com.vontroy.pku_ss_cloud_class.course.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.StorageListAdapter;
import com.vontroy.pku_ss_cloud_class.databinding.CourseDetailFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/11/10.
 */

public class CourseDetailFragment extends Fragment implements CourseDetailContract.View, TabHost.TabContentFactory {
    private CourseDetailFragBinding courseDetailFragBinding;
    private CourseDetailContract.Presenter mPresenter;
    private ListView courseWareList;
    private ListView courseDocsList;
    private ArrayList<StorageInfo> coursedatas;
    private ArrayList<StorageInfo> coursewares;
    private StorageListAdapter courseWareAdapter;
    private StorageListAdapter courseDataAdapter;

    public static CourseDetailFragment newInstance() {
        return new CourseDetailFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.course_detail_frag, container, false);
        courseDetailFragBinding = CourseDetailFragBinding.bind(root);

        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");

        Bundle bundle = getArguments();
        String cid = bundle.getString("course_id");
        String courseName = bundle.getString("course_name");

        coursewares = new ArrayList<>();
        coursedatas = new ArrayList<>();

        courseWareAdapter = new StorageListAdapter(this.getActivity(), coursewares, "all");
        courseDataAdapter = new StorageListAdapter(this.getActivity(), coursedatas, "all");

        courseWareList = (ListView) root.findViewById(R.id.courseware);
        courseDocsList = (ListView) root.findViewById(R.id.coursedocs);

        courseWareList.setAdapter(courseWareAdapter);
        setListViewHeightBasedOnChildren(courseWareList);

        courseDocsList.setAdapter(courseDataAdapter);
        setListViewHeightBasedOnChildren(courseDocsList);

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("sid", sid);
        params.put("id", cid);

        mPresenter.setCourseDatas(coursedatas);
        mPresenter.setCourseWares(coursewares);
        mPresenter.getCourseDocs(params, courseName, cid);

        return root;
    }

    @Override
    public void listDataChanged() {
        courseWareAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(courseWareList);
        courseDataAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(courseDocsList);
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
    public void setPresenter(@NonNull CourseDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}

