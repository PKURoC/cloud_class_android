package com.vontroy.pku_ss_cloud_class.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.about.AboutActivity;
import com.vontroy.pku_ss_cloud_class.adapter.ProfileItemAdapter;
import com.vontroy.pku_ss_cloud_class.course.JoinedCourse.JoinedCourseActivity;
import com.vontroy.pku_ss_cloud_class.databinding.ProfileFragmentBinding;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.entry.ProfileInfo;
import com.vontroy.pku_ss_cloud_class.my_message.MyMsgActivity;
import com.vontroy.pku_ss_cloud_class.user_account.login.LoginActivity;
import com.vontroy.pku_ss_cloud_class.user_account.register.RegActivity;
import com.vontroy.pku_ss_cloud_class.user_account.reset_password.ResetPwdActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class ProfileFragment extends Fragment implements ProfileContract.View {
    private ProfileFragmentBinding profileFragmentBinding;
    private ProfileContract.Presenter mPresenter;
    private boolean loginState = false;
    private ProfileItemAdapter adapter;
    private ArrayList<CourseInfo> courseInfos;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileFragmentBinding = ProfileFragmentBinding.inflate(inflater, container, false);
        initData();
        return profileFragmentBinding.getRoot();
    }

    private void initData() {
        final ArrayList<ProfileInfo> profileInfos = new ArrayList<>();
        profileInfos.add(new ProfileInfo("我的消息"));
        profileInfos.add(new ProfileInfo("关于软件"));

        final SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");
        String nick = getToken.getString("nick", "");

        Log.d("dddd", "onCreateView: " + nick);
        loginState = !Strings.isNullOrEmpty(token);
        if (loginState) {
            profileFragmentBinding.nickName.setText(nick);
            profileFragmentBinding.updateNickName.setVisibility(View.VISIBLE);
            profileInfos.add(new ProfileInfo("修改密码"));
            profileInfos.add(new ProfileInfo("退出登录"));
        } else {
            profileFragmentBinding.updateNickName.setVisibility(View.INVISIBLE);
            profileInfos.add(new ProfileInfo("用户注册"));
            profileInfos.add(new ProfileInfo("用户登录"));
        }

        adapter = new ProfileItemAdapter(this.getActivity(), profileInfos);
        profileFragmentBinding.infoItem.setAdapter(adapter);

        profileFragmentBinding.infoItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = getToken.getString("token", "");
                if (i == 0) {
                    if (Strings.isNullOrEmpty(token)) {
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
                        Intent intent = new Intent(getActivity(), MyMsgActivity.class);
                        startActivity(intent);
                    }
                } else if (i == 1) {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    Bundle bundle = new Bundle();

                    ProfileInfo profileInfo = profileInfos.get(i);

                    bundle.putString("profile_item", profileInfo.getItemName());

                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (i == 2 && !loginState) {
                    Intent intent = new Intent(getActivity(), RegActivity.class);
                    startActivity(intent);
                } else if (i == 3 && !loginState) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 10001);
                } else if (i == 2 && loginState) {
                    Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                    startActivity(intent);
                } else if (i == 3 && loginState) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("退出登录")
                            .setMessage("确定退出?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("dddd", "onClick: " + getToken.getString("token", ""));
                                    getToken.edit().clear().commit();
                                    Log.d("dddd", "onClickBtn: " + getToken.getString("token", ""));
                                    loginState = false;
                                    profileFragmentBinding.nickName.setText("请登录查看更多信息");
                                    profileFragmentBinding.updateNickName.setVisibility(View.GONE);
//                                    profileFragmentBinding.updateProfile.setVisibility(View.INVISIBLE);
                                    profileInfos.clear();
                                    profileInfos.add(new ProfileInfo("我的消息"));
                                    profileInfos.add(new ProfileInfo("关于软件"));

                                    if (loginState) {
                                        profileInfos.add(new ProfileInfo("修改密码"));
                                        profileInfos.add(new ProfileInfo("退出登录"));
                                    } else {
                                        profileInfos.add(new ProfileInfo("用户注册"));
                                        profileInfos.add(new ProfileInfo("用户登录"));
                                    }
                                    listDataChanged();
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();

                }
            }
        });

        profileFragmentBinding.joinedCourseTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinedCourseActivity.class);
                startActivity(intent);
            }
        });

//        profileFragmentBinding.updateProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
//                startActivity(intent);
//            }
//        });

        profileFragmentBinding.updateNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputNewNick = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("输入新昵称").setIcon(R.drawable.user_account).setView(inputNewNick).setNegativeButton("取消", null);
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, String> modifyNickParams = new HashMap<String, String>();
                        modifyNickParams.put("sid", sid);
                        modifyNickParams.put("token", token);
                        modifyNickParams.put("newnick", inputNewNick.getText().toString());
                        mPresenter.modifyNick(modifyNickParams);
                    }
                });
                builder.show();
            }
        });

        courseInfos = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("sid", sid);
        params.put("token", token);
        mPresenter.setCourseInfos(courseInfos);
        mPresenter.getMyCourses(params);
    }

    @Override
    public void setPresenter(ProfileContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void listDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getCourseInfosSuccess() {
        profileFragmentBinding.joinedCourseTotal.setText(String.valueOf(courseInfos.size()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if ((requestCode == 10001)) {
                initData();
            }
        }
    }

    @Override
    public void updateNickSuccess(String nick) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nick", nick);
        editor.commit();
        profileFragmentBinding.nickName.setText(nick);
    }
}