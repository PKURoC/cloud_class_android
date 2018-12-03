package com.vontroy.pku_ss_cloud_class.curriculum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.course.detail.CourseDetailActivity;
import com.vontroy.pku_ss_cloud_class.data.CourseArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseResult;
import com.vontroy.pku_ss_cloud_class.databinding.CurriculumFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

public class CurriculumFragment extends Fragment {


    public static CurriculumFragment newInstance() {
        return new CurriculumFragment();
    }

    private CurriculumFragBinding curriculumFragBinding;
    private View mRootView;

    private ArrayList<CourseInfo> courseInfos;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");

        curriculumFragBinding = curriculumFragBinding.inflate(inflater, container, false);
        courseInfos = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("sid", sid);

        getMyCourses(params);

        return curriculumFragBinding.getRoot();
    }

    public void getMyCourses(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(CurriculumFragment.class.getSimpleName(), Request.Method.GET, ServerInterface.getMyCourses, params, CourseArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<CourseArrayResult>() {
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
                    public void onNext(CourseArrayResult courseArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(courseArrayResult.getCode())) {
                            for (CourseResult courseResult : courseArrayResult.getData()) {
                                if (!Strings.isNullOrEmpty(courseResult.getClassroom())) {
                                    CourseInfo courseInfo = new CourseInfo("", "");
                                    courseInfo.setCourseId(courseResult.getId());

                                    String courseName = courseResult.getName();
                                    String[] courseNameSegs = courseName.split("[\\d\\s]+");

                                    courseInfo.setCourseName(courseName);
                                    courseInfo.setCourseRealName(courseNameSegs[courseNameSegs.length - 1]);
                                    courseInfo.setStudentNum(courseResult.getNums());
                                    courseInfo.setCourseTeacher(courseResult.getTeacher());
                                    courseInfo.setCourseIntroduction(courseResult.getAbout());
                                    courseInfo.setOther(courseResult.getOther());
                                    courseInfo.setClassroom(courseResult.getClassroom());
                                    courseInfo.setOwnerId(courseResult.getOwnerid());
                                    courseInfo.setClassTime(courseResult.getClasstime());
                                    courseInfo.setClassTime2(courseResult.getClasstime2());
                                    courseInfos.add(courseInfo);
                                }

                            }

                            for (final CourseInfo courseInfo : courseInfos) {
                                String classTime = courseInfo.getClassTime();
                                String classTime2 = courseInfo.getClassTime2();

                                int weekFlag = -1;
                                int beginHour = -1;
                                int beginMin = -1;
                                int endHour = -1;
                                int endMin = -1;

                                if (!Strings.isNullOrEmpty(classTime)) {
                                    weekFlag = Integer.valueOf(classTime.substring(0, 1));
                                    beginHour = Integer.valueOf(classTime.substring(1, 3));
                                    beginMin = Integer.valueOf(classTime.substring(3, 5));
                                    endHour = Integer.valueOf(classTime.substring(5, 7));
                                    endMin = Integer.valueOf(classTime.substring(7, 9));
                                }

                                int weekFlag2 = -1;
                                int beginHour2 = -1;
                                int beginMin2 = -1;
                                int endHour2 = -1;
                                int endMin2 = -1;
                                if (!Strings.isNullOrEmpty(classTime2)) {
                                    weekFlag2 = Integer.valueOf(classTime2.substring(0, 1));
                                    beginHour2 = Integer.valueOf(classTime2.substring(1, 3));
                                    beginMin2 = Integer.valueOf(classTime2.substring(3, 5));
                                    endHour2 = Integer.valueOf(classTime2.substring(5, 7));
                                    endMin2 = Integer.valueOf(classTime2.substring(7, 9));
                                }

                                if (!Strings.isNullOrEmpty(classTime)) {
                                    switch (weekFlag) {
                                        case 1: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course1A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course1P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course1E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 2: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course2A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course2P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course2E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 3: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course3A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course3P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course3E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 4: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course4A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course4P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course4E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 5: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course5A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course5P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course5E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 6: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course6A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course6P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course6E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 7: {
                                            if (beginHour < 12) {
                                                curriculumFragBinding.course7A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour >= 12 && beginHour < 17) {
                                                curriculumFragBinding.course7P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course7E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (!Strings.isNullOrEmpty(classTime2)) {
                                    switch (weekFlag2) {
                                        case 1: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course1A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course1P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course1E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course1E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course1E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 2: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course2A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course2P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course2E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course2E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course2E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 3: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course3A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course3P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course3E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course3E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course3E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 4: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course4A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course4P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course4E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course4E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course4E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 5: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course5A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course5P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course5E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course5E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course5E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 6: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course6A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course6P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course6E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course6E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course6E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }

                                        case 7: {
                                            if (beginHour2 < 12) {
                                                curriculumFragBinding.course7A.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7A.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7A.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else if (beginHour2 >= 12 && beginHour2 < 17) {
                                                curriculumFragBinding.course7P.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7P.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7P.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                curriculumFragBinding.course7E.setText(courseInfo.getCourseRealName());
                                                curriculumFragBinding.course7E.setTextColor(Color.BLUE);
                                                curriculumFragBinding.course7E.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                        Bundle bundle = new Bundle();

                                                        bundle.putString("course_id", courseInfo.getCourseId());
                                                        bundle.putString("course_name", courseInfo.getCourseName());
                                                        bundle.putString("course_teacher", courseInfo.getCourseTeacher());
                                                        bundle.putString("course_introduction", courseInfo.getCourseIntroduction());

                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                });
    }
}
