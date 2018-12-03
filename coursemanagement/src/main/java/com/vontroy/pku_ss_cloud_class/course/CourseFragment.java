package com.vontroy.pku_ss_cloud_class.course;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.CourseFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/11/9.
 */

public class CourseFragment extends Fragment implements CourseContract.View {

    private CourseFragBinding courseFragBinding;
    private CourseContract.Presenter mPresenter;

    public static CourseFragment newInstance() {
        return new CourseFragment();
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

        View root = inflater.inflate(R.layout.course_frag, container, false);

        Bundle bundle = this.getArguments();
        final CourseInfo courseInfo = (CourseInfo) bundle.getSerializable("course_info");

        final SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        final String token = getParams.getString("token", "");
        final String sid = getParams.getString("sid", "");

        courseFragBinding = CourseFragBinding.bind(root);

        if (courseInfo.isSelectedCurrently()) {
            courseFragBinding.selectCourse.setVisibility(GONE);
            courseFragBinding.exitCourse.setVisibility(View.VISIBLE);
        } else {
            courseFragBinding.selectCourse.setVisibility(View.VISIBLE);
            courseFragBinding.exitCourse.setVisibility(GONE);
        }

        courseFragBinding.selectCourse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Map params = new HashMap();
                params.put("id", courseInfo.getCourseId());
                params.put("sid", sid);
                params.put("token", token);

                mPresenter.JoinCourse(params);
            }
        });

        courseFragBinding.exitCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("提示").setMessage("再次加入需要重新审核, 确定退课?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Map dropCourseParams = new HashMap();
                                dropCourseParams.put("id", courseInfo.getCourseId());
                                dropCourseParams.put("token", token);
                                dropCourseParams.put("sid", sid);
                                mPresenter.DropCourse(dropCourseParams);
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        courseFragBinding.courseName.setText(courseInfo.getCourseName());
        courseFragBinding.teacher.setText(courseInfo.getCourseTeacher());
        courseFragBinding.courseIntroduction.setText(courseInfo.getCourseIntroduction());
        courseFragBinding.studentNum.setText(String.valueOf(courseInfo.getStudentNum()));

        String classTimeStr = "";

        String courseTime = courseInfo.getClassTime();
        if (!Strings.isNullOrEmpty(courseTime)) {
            int weekVal = Integer.valueOf(courseTime.substring(0, 1));
            String weekDay = "";
            switch (weekVal) {
                case 1:
                    weekDay = "每周一";
                    break;
                case 2:
                    weekDay = "每周二";
                    break;
                case 3:
                    weekDay = "每周三";
                    break;
                case 4:
                    weekDay = "每周四";
                    break;
                case 5:
                    weekDay = "每周五";
                    break;
                case 6:
                    weekDay = "每周六";
                    break;
                case 7:
                    weekDay = "每周日";
                    break;
            }
            String beginHour = courseTime.substring(1, 3);
            String beginMin = courseTime.substring(3, 5);
            String endHour = courseTime.substring(5, 7);
            String endMin = courseTime.substring(7, 9);
            classTimeStr = weekDay + " " + beginHour + ":" + beginMin + "-" + endHour + ":" + endMin;
        }

        String courseTime2 = courseInfo.getClassTime2();
        if (!Strings.isNullOrEmpty(courseTime2)) {
            int weekVal2 = Integer.valueOf(courseTime2.substring(0, 1));
            String weekDay2 = "";
            switch (weekVal2) {
                case 1:
                    weekDay2 = "每周一";
                    break;
                case 2:
                    weekDay2 = "每周二";
                    break;
                case 3:
                    weekDay2 = "每周三";
                    break;
                case 4:
                    weekDay2 = "每周四";
                    break;
                case 5:
                    weekDay2 = "每周五";
                    break;
                case 6:
                    weekDay2 = "每周六";
                    break;
                case 7:
                    weekDay2 = "每周日";
                    break;
            }
            String beginHour2 = courseTime2.substring(1, 3);
            String beginMin2 = courseTime2.substring(3, 5);
            String endHour2 = courseTime2.substring(5, 7);
            String endMin2 = courseTime2.substring(7, 9);
            classTimeStr += ("    " + weekDay2 + " " + beginHour2 + ":" + beginMin2 + "-" + endHour2 + ":" + endMin2);
        }

        courseFragBinding.courseTime.setText(classTimeStr);


        courseFragBinding.coursePosition.setText(courseInfo.getClassroom());
        courseFragBinding.courseOther.setText(courseInfo.getOther());

        if ("2488dbbe86da4d8ea523518d021c51bc".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("陈尧");
            courseFragBinding.taEmail1.setText("fengyuncy@pku.edu.cn");

            courseFragBinding.ta2.setVisibility(View.VISIBLE);
            courseFragBinding.taName2.setText("郭政");
            courseFragBinding.taEmail2.setText("gzhengkitty@126.com");

            courseFragBinding.ta3.setVisibility(View.VISIBLE);
            courseFragBinding.taName3.setText("邱鸿淼");
            courseFragBinding.taEmail3.setText("hm_qiu@qq.com");

            courseFragBinding.ta4.setVisibility(View.VISIBLE);
            courseFragBinding.taName4.setText("戴维");

            courseFragBinding.ta5.setVisibility(GONE);
            courseFragBinding.taEmail4.setText("weidai@pku.edu.cn");
        } else if ("c00c1222c0704893925f6ecf1461aa7d".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("李聪");
            courseFragBinding.taEmail1.setText("544171266@qq.com");

            courseFragBinding.ta2.setVisibility(GONE);
            courseFragBinding.ta3.setVisibility(GONE);
            courseFragBinding.ta4.setVisibility(GONE);
            courseFragBinding.ta5.setVisibility(GONE);
        } else if ("11abb84641c645c0a12117c0a2157140".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("邱玉钦");
            courseFragBinding.taEmail1.setText("qiuyuqin@pku.edu.cn");
            courseFragBinding.ta2.setVisibility(GONE);
            courseFragBinding.ta3.setVisibility(GONE);
            courseFragBinding.ta4.setVisibility(GONE);
            courseFragBinding.ta5.setVisibility(GONE);
        } else if ("82ebed7253fc4d2a858a57cf57326454".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("冯新宇");
            courseFragBinding.taEmail1.setText("fengxinyu@pku.edu.cn");
            courseFragBinding.ta2.setVisibility(GONE);
            courseFragBinding.ta3.setVisibility(GONE);
            courseFragBinding.ta4.setVisibility(GONE);
            courseFragBinding.ta5.setVisibility(GONE);
        } else if ("056962237c964c0788ec1f62baeae43a".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("张胜军");
            courseFragBinding.taEmail1.setText("zhangshengjun@pku.edu.cn");
            courseFragBinding.ta2.setVisibility(GONE);
            courseFragBinding.ta3.setVisibility(GONE);
            courseFragBinding.ta4.setVisibility(GONE);
            courseFragBinding.ta5.setVisibility(GONE);
        } else if ("44a0b92f3c8f4c46b18b07a11018467f".equals(courseInfo.getCourseId())) {
            courseFragBinding.ta1.setVisibility(View.VISIBLE);
            courseFragBinding.taName1.setText("待定");
            courseFragBinding.taEmail1.setText("暂无");
            courseFragBinding.ta2.setVisibility(GONE);
            courseFragBinding.ta3.setVisibility(GONE);
            courseFragBinding.ta4.setVisibility(GONE);
            courseFragBinding.ta5.setVisibility(GONE);
        }

        setCourseImage(courseInfo.getCourseId());

        return root;
    }

    @Override
    public void setPresenter(@NonNull CourseContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void successJoinCourse() {
        courseFragBinding.selectCourse.setVisibility(GONE);
        courseFragBinding.exitCourse.setVisibility(View.VISIBLE);
        Log.d("debug", "add course success");
        Toast.makeText(getActivity(), "选课成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void successDropCourse() {
        courseFragBinding.selectCourse.setVisibility(View.VISIBLE);
        courseFragBinding.exitCourse.setVisibility(GONE);
        Toast.makeText(getActivity(), "退课成功!", Toast.LENGTH_SHORT).show();
    }

    public void setCourseImage(String courseId) {
        switch (courseId) {
            case "056962237c964c0788ec1f62baeae43a":  //网络规划与设计
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg1);
                break;
            case "11abb84641c645c0a12117c0a2157140":  //操作系统虚拟化
            case "82ebed7253fc4d2a858a57cf57326454":
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg2);
                break;
            case "44a0b92f3c8f4c46b18b07a11018467f":  //hadoop
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg3);
                break;
            case "c00c1222c0704893925f6ecf1461aa7d":  //高性能并行程序设计
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg4);
                break;
            case "2488dbbe86da4d8ea523518d021c51bc":  //素质教育
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg5);
                break;
            default:
                courseFragBinding.courseImg.setBackgroundResource(R.drawable.course_bg);
        }
    }
}
