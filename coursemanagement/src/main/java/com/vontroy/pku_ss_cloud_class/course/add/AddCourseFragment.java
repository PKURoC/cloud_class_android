package com.vontroy.pku_ss_cloud_class.course.add;

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

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.CourseListAdapter;
import com.vontroy.pku_ss_cloud_class.course.CourseActivity;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddCourseFragment extends Fragment implements AddCourseContract.View {
    private AddCourseContract.Presenter mPresenter;

    public static AddCourseFragment newInstance() {
        return new AddCourseFragment();
    }

    private View mRootView;
    private ListView course_list;
    private ArrayList<CourseInfo> courseList;
    private CourseListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");

        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.activity_add_course, container, false);
        }
        course_list = (ListView) mRootView.findViewById(R.id.course_list);

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        final ArrayList<CourseInfo> joinedCourses = new ArrayList<>();

        courseList = new ArrayList<>();
        adapter = new CourseListAdapter(this.getActivity(), courseList);
        course_list.setAdapter(adapter);
        course_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                Bundle bundle = new Bundle();

                CourseInfo courseInfo = courseList.get(i);
                String currentCourseId = courseInfo.getCourseId();
                courseInfo.setSelectedCurrently(false);
                for (CourseInfo joinedCourseInfo : joinedCourses) {
                    if (currentCourseId.equals(joinedCourseInfo.getCourseId())) {
                        courseInfo.setSelectedCurrently(true);
                        break;
                    }
                }

                bundle.putSerializable("course_info", courseInfo);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mPresenter.setJoinedCourseInfos(joinedCourses);
        Map<String, String> params = new HashMap<>();
        params.put("sid", sid);
        params.put("token", token);
        mPresenter.getMyCourses(params);

        mPresenter.setCourseList(courseList);
        mPresenter.subscribe();
        return mRootView;
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
    public void setPresenter(@NonNull AddCourseContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void listDataChanged() {
        adapter.notifyDataSetChanged();
    }
}
