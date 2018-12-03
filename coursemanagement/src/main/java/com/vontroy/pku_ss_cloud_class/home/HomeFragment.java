package com.vontroy.pku_ss_cloud_class.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.synnapps.carouselview.ImageListener;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.CourseNotificationAdapter;
import com.vontroy.pku_ss_cloud_class.adapter.MyMsgItemAdapter;
import com.vontroy.pku_ss_cloud_class.course.JoinedCourse.JoinedCourseActivity;
import com.vontroy.pku_ss_cloud_class.course.add.AddCourseActivity;
import com.vontroy.pku_ss_cloud_class.databinding.HomeFragmentBinding;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;
import com.vontroy.pku_ss_cloud_class.my_message.MyMsgActivity;
import com.vontroy.pku_ss_cloud_class.user_account.login.LoginActivity;
import com.vontroy.pku_ss_cloud_class.user_account.register.RegActivity;
import com.vontroy.pku_ss_cloud_class.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeFragmentBinding homeFragmentBinding;
    private CourseNotificationAdapter courseNotificationAdapter;
    private MyMsgItemAdapter newestMsgAdapter;
    private ArrayList<MyMsgInfo> myMsgInfos;
    private ArrayList<CourseInfo> courseInfos;
    private String token;
    private String sid;

    private final String SPRING_SEMESTER_BEGIN_DATE = "02-20";
    private final String AUTUMN_SEMESTER_BEGIN_DATE = "09-12";

    int[] sampleImages = {R.drawable.front_1, R.drawable.front_2, R.drawable.front_3, R.drawable.front_4};


    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
    private HomeContract.Presenter mPresenter;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = getToken.getString("token", "");
        sid = getToken.getString("sid", "");

        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        homeFragmentBinding.courseView.setPageCount(sampleImages.length);
        homeFragmentBinding.currentDate.setText(Utils.getDate() + " " + Utils.getWeekOfDate());

        homeFragmentBinding.courseView.setImageListener(imageListener);

        courseInfos = new ArrayList<>();

        homeFragmentBinding.addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
            }
        });

        homeFragmentBinding.joinedCourse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO debug
                if (Strings.isNullOrEmpty(token) && false) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("错误").setMessage("请先登录")
                            .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getContext(), LoginActivity.class);
                                    startActivityForResult(intent, 10001);
                                }
                            })
                            .setNegativeButton("注册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getContext(), RegActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    Intent intent = new Intent(getActivity(), JoinedCourseActivity.class);
                    startActivity(intent);
                }
            }
        });

        homeFragmentBinding.moreMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyMsgActivity.class);
                startActivity(intent);
            }
        });

        if (!Strings.isNullOrEmpty(token)) {
            homeFragmentBinding.homeLoginLayout.setVisibility(GONE);
            homeFragmentBinding.homeRegisterLayout.setVisibility(GONE);
        } else {
            homeFragmentBinding.homeLoginIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 10001);

                }
            });

            homeFragmentBinding.homeRegisterIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), RegActivity.class);
                    startActivity(intent);
                }
            });
        }

        setHomePageInfos();
        return homeFragmentBinding.getRoot();
    }

    private void setHomePageInfos() {
        Map<String, String> params = new HashMap<>();
        params.put("sid", sid);
        params.put("token", token);

        Map<String, String> getRecentParams = new HashMap<>();
        getRecentParams.put("sid", sid);
        getRecentParams.put("token", token);

        courseNotificationAdapter = new CourseNotificationAdapter(getActivity(), courseInfos);
        homeFragmentBinding.courseNotification.setAdapter(courseNotificationAdapter);
        setListViewHeightBasedOnChildren(homeFragmentBinding.courseNotification);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        int sidYear = 0;
        if (!Strings.isNullOrEmpty(sid)) {
            sidYear = Integer.valueOf(20 + sid.substring(0, 2));
        }

        int gradeVal = year - sidYear;
        if (month > 7) {
            ++gradeVal;
        }

        String gradeStr = "";
        switch (gradeVal) {
            case 1:
                gradeStr = "研一";
                break;
            case 2:
                gradeStr = "研二";
                break;
            case 3:
                gradeStr = "研三";
                break;
            case 4:
                gradeStr = "研四";
                break;
            case 5:
                gradeStr = "研五";
                break;
            default:
                break;
        }
        homeFragmentBinding.grade.setText(gradeStr);

        String semesterStr = "";
        if (month > 7 && month < 2) {
            semesterStr = "秋季学期";
        } else {
            semesterStr = "春季学期";
        }
        homeFragmentBinding.semester.setText(semesterStr);

        String detailBeginDate = "";
        if (month >= 8) {
            detailBeginDate = year + "-" + AUTUMN_SEMESTER_BEGIN_DATE;
        } else if (month < 2) {
            detailBeginDate = (year - 1) + "-" + AUTUMN_SEMESTER_BEGIN_DATE;
        } else {
            detailBeginDate = year + "-" + SPRING_SEMESTER_BEGIN_DATE;
        }

        int weekVal = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int days = Utils.daysBetween(simpleDateFormat.parse(detailBeginDate), new Date());
            if (days >= 0) {
                weekVal = days / 7;
            } else {
                weekVal = -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String weekStr = "";
        if (weekVal >= 0) {
            weekStr = "第 " + (weekVal + 1) + " 周";
        } else {
            weekStr = "学期未开始";
        }
        homeFragmentBinding.week.setText(weekStr);


        myMsgInfos = new ArrayList<>();

        newestMsgAdapter = new MyMsgItemAdapter(getActivity(), myMsgInfos, token, sid);
        homeFragmentBinding.newestMsg.setAdapter(newestMsgAdapter);
        setListViewHeightBasedOnChildren(homeFragmentBinding.newestMsg);

        mPresenter.setCourseInfos(courseInfos);
        mPresenter.getMyCourses(params);

        mPresenter.setRecentMsgs(myMsgInfos);
        mPresenter.getRecent(getRecentParams);

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

    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void markAllMsgAsHasReadSuccess() {
        newestMsgAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(homeFragmentBinding.newestMsg);
        Toast.makeText(getContext(), "操作成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void markSingleMsgAsHasReadSuccess() {
        newestMsgAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(homeFragmentBinding.newestMsg);
        Toast.makeText(getContext(), "操作成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getRecentMsgSuccess() {
        newestMsgAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(homeFragmentBinding.newestMsg);
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
    public void courseDataChanged() {
        courseNotificationAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(homeFragmentBinding.courseNotification);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if ((requestCode == 10001)) {
                SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
                homeFragmentBinding.homeLoginLayout.setVisibility(GONE);
                homeFragmentBinding.homeRegisterLayout.setVisibility(GONE);
                token = getToken.getString("token", "");
                sid = getToken.getString("sid", "");
                setHomePageInfos();
            }
        }
    }
}