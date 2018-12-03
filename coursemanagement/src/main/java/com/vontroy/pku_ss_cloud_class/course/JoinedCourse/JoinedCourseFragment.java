package com.vontroy.pku_ss_cloud_class.course.JoinedCourse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.JoinedCourseAdapter;
import com.vontroy.pku_ss_cloud_class.course.detail.CourseDetailActivity;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-22.
 */

public class JoinedCourseFragment extends Fragment implements JoinedCourseContract.View {
    private JoinedCourseContract.Presenter mPresenter;

    public static JoinedCourseFragment newInstance() {
        return new JoinedCourseFragment();
    }

    private View mRootView;
    private ListView course_list;
    private ArrayList<CourseInfo> courseList;
    private JoinedCourseAdapter adapter;
    private ViewGroup parent;

    private SharedPreferences getParams;
    private String token;
    private String sid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.joined_course_frag, container, false);

        initData();

        return mRootView;
    }

    public void initData() {

        course_list = (ListView) mRootView.findViewById(R.id.joined_course_list);

        getParams = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        token = getParams.getString("token", "");
        parent = (ViewGroup) mRootView.getParent();
        sid = getParams.getString("sid", "");

        if (parent != null) {
            parent.removeView(mRootView);
        }
        courseList = new ArrayList<>();

        adapter = new JoinedCourseAdapter(this.getActivity(), courseList);
        adapter.setOnDropClickListener(new JoinedCourseAdapter.OnDropClickListener() {
            @Override
            public void dropClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("提示").setMessage("再次加入需要重新审核, 确定退课?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Map dropCourseParams = new HashMap();
                                dropCourseParams.put("id", String.valueOf(courseList.get(position).getCourseId()));
                                dropCourseParams.put("token", token);
                                dropCourseParams.put("sid", sid);
                                mPresenter.DropCourse(dropCourseParams);
                            }
                        })
                        .setNegativeButton("取消", null).show();

            }
        });
        course_list.setAdapter(adapter);
        course_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                Bundle bundle = new Bundle();

                CourseInfo courseInfo = courseList.get(i);

                bundle.putString("course_id", courseInfo.getCourseId());
                bundle.putString("course_name", courseInfo.getCourseName());
                bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Map getMyCoursesParams = new HashMap();
        getMyCoursesParams.put("token", token);
        getMyCoursesParams.put("sid", sid);
        mPresenter.setCourseList(courseList);
        mPresenter.getMyCourses(getMyCoursesParams);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull JoinedCourseContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void listDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void SuccessDropCourse() {
        Log.d("debug", "drop course success");
//        initData();
        courseList.clear();
        Map getMyCoursesParams = new HashMap();
        getMyCoursesParams.put("token", token);
        getMyCoursesParams.put("sid", sid);
        mPresenter.getMyCourses(getMyCoursesParams);
        Toast.makeText(getActivity(), "退课成功!", Toast.LENGTH_SHORT).show();
    }
}