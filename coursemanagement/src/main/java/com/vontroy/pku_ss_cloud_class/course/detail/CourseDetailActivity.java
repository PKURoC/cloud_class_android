package com.vontroy.pku_ss_cloud_class.course.detail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.course.group.home.GroupFragment;
import com.vontroy.pku_ss_cloud_class.course.group.home.GroupPresenter;
import com.vontroy.pku_ss_cloud_class.course.group.list.GroupListFragment;
import com.vontroy.pku_ss_cloud_class.course.group.list.GroupListPresenter;
import com.vontroy.pku_ss_cloud_class.course.homework.main.HomeworkFragment;
import com.vontroy.pku_ss_cloud_class.course.homework.main.HomeworkPresenter;
import com.vontroy.pku_ss_cloud_class.data.GroupArrayResult;
import com.vontroy.pku_ss_cloud_class.data.GroupDetailResult;
import com.vontroy.pku_ss_cloud_class.data.GroupResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by LinkedME06 on 16/11/10.
 */

public class CourseDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("课程详情");

        final Bundle bundle = this.getIntent().getExtras();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.cd_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int id = tab.getPosition();
                switchFragment(id, bundle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String courseId = bundle.getString("course_id");
        String courseName = bundle.getString("course_name");
        String courseTeacher = bundle.getString("course_teacher");

        ImageView joinedCourseImg = (ImageView) findViewById(R.id.joined_course_detail_img);
        joinedCourseImg.setBackgroundResource(R.drawable.course_bg);

        TextView joinedCourseName = (TextView) findViewById(R.id.joined_course_name);
        joinedCourseName.setText(courseName);

        TextView joinedCourseTeacher = (TextView) findViewById(R.id.joined_course_teacher);
        joinedCourseTeacher.setText(courseTeacher);

        switch (courseId) {
            case "056962237c964c0788ec1f62baeae43a":  //网络规划与设计
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg1);
                break;
            case "11abb84641c645c0a12117c0a2157140":  //操作系统虚拟化
            case "82ebed7253fc4d2a858a57cf57326454":
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg2);
                break;
            case "44a0b92f3c8f4c46b18b07a11018467f":  //hadoop
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg3);
                break;
            case "c00c1222c0704893925f6ecf1461aa7d":  //高性能并行程序设计
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg4);
                break;
            case "2488dbbe86da4d8ea523518d021c51bc":  //素质教育
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg5);
                break;
            default:
                joinedCourseImg.setBackgroundResource(R.drawable.course_bg);
        }

        switchFragment(tabLayout.getSelectedTabPosition(), bundle);
    }

    private void switchFragment(int id, Bundle bundle) {
        //TODO getContext
        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = getParams.getString("token", "");
        String sid = getParams.getString("sid", "");
        bundle.putString("sid", sid);

        if (id == 0) {
            CourseDetailFragment courseDetailFragment = CourseDetailFragment.newInstance();

            courseDetailFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    courseDetailFragment, R.id.contentFrame);
            // Create the presenter
            new CourseDetailPresenter(TAG, ServerImp.getInstance(), courseDetailFragment, SchedulerProvider.getInstance());
        } else if (id == 1) {
            String courseId = bundle.getString("course_id");

            Map<String, String> params = new HashMap<>();

            params.put("cid", courseId);
            params.put("token", token);
            params.put("sid", sid);

            getMyGroup(params, bundle);
        } else if (id == 2) {
            HomeworkFragment homeworkFragment = HomeworkFragment.newInstance();

            homeworkFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), homeworkFragment, R.id.contentFrame);
            new HomeworkPresenter(TAG, ServerImp.getInstance(), homeworkFragment, SchedulerProvider.getInstance());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getMyGroup(final Map<String, String> params, final Bundle bundle) {
        Subscription subscription = ServerImp.getInstance()
                .common(CourseDetailActivity.class.getSimpleName(), Request.Method.GET, ServerInterface.getMyGroup, params, GroupDetailResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<GroupDetailResult>() {
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
                    public void onNext(GroupDetailResult groupArrayResult) {
                        Log.d("ddd", "onNext: ");
                        Map<String, String> getGroupsParams = new HashMap<String, String>();
                        getGroupsParams.put("cid", bundle.getString("course_id"));
                        getGroupsParams.put("sid", bundle.getString("sid"));

                        if (groupArrayResult.getCode().equals("0") && groupArrayResult.getData() != null) {
                            GroupFragment groupFragment = GroupFragment.newInstance();

                            JsonObject jsonObject = groupArrayResult.getData();
                            JsonArray memberArray = groupArrayResult.getMembers();

                            ArrayList<Student> members = new ArrayList<Student>();

                            for (int i = 0; i < memberArray.size(); ++i) {
                                JsonObject member = memberArray.get(i).getAsJsonObject();
                                Student student = new Student();
                                student.setNick(member.get("nick").getAsString());
                                student.setSid(member.get("sid").getAsString());
                                members.add(student);
                            }

                            String gid = jsonObject.get("id").getAsString();
                            bundle.putString("group_name", jsonObject.get("name").getAsString());
                            bundle.putString("gid", gid);
                            bundle.putString(gid + "_invitation", jsonObject.get("invitation").getAsString());
                            bundle.putSerializable("member", members);

                            groupFragment.setArguments(bundle);
                            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), groupFragment, R.id.contentFrame);
                            new GroupPresenter(TAG, ServerImp.getInstance(), groupFragment, SchedulerProvider.getInstance());
                        } else {
                            params.put("cid", bundle.getString("course_id"));
                            getGroups(params, bundle);
                        }
                    }

                });
    }

    public void getGroups(Map<String, String> params, final Bundle bundle) {
        Subscription subscription = ServerImp.getInstance()
                .common(CourseDetailActivity.class.getSimpleName(), Request.Method.GET, ServerInterface.getGroups, params, GroupArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<GroupArrayResult>() {
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
                    public void onNext(GroupArrayResult groupArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (groupArrayResult.getCode().equals("0")) {
                            ArrayList<GroupResult> groupResults = groupArrayResult.getData();
                            ArrayList<GroupInfo> groupInfos = new ArrayList<GroupInfo>();

                            for (GroupResult groupResult : groupResults) {
                                GroupInfo groupInfo = new GroupInfo("", "");
                                groupInfo.setGroupId(groupResult.getId());
                                groupInfo.setGroupName(groupResult.getName());
                                groupInfo.setGroupIntroduction(groupResult.getAbout());
                                groupInfo.setCourseId(groupResult.getCourseid());
                                groupInfo.setOwnerName(groupResult.getOwnername());
                                groupInfos.add(groupInfo);
                            }

                            bundle.putSerializable("groupInfos", groupInfos);

                            GroupListFragment groupListFragment = GroupListFragment.newInstance();
                            groupListFragment.setArguments(bundle);
                            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), groupListFragment, R.id.contentFrame);
                            new GroupListPresenter(TAG, ServerImp.getInstance(), groupListFragment, SchedulerProvider.getInstance());
                        }
                    }

                });
    }

}

