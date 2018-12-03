package com.vontroy.pku_ss_cloud_class.user_account.register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.databinding.RegFragBinding;
import com.vontroy.pku_ss_cloud_class.user_account.login.LoginActivity;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-14.
 */

public class RegFragment extends Fragment implements RegContract.View {
    private RegFragBinding regFragBinding;
    private RegContract.Presenter mPresenter;

    public static RegFragment newInstance() {
        return new RegFragment();
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
        final View root = inflater.inflate(R.layout.reg_frag, container, false);
        regFragBinding = RegFragBinding.bind(root);

        regFragBinding.reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText sid = (EditText) root.findViewById(R.id.name);
                EditText pwd = (EditText) root.findViewById(R.id.password);
                EditText retypedPwd = (EditText) root.findViewById(R.id.retyped_password);
                EditText nick = (EditText) root.findViewById(R.id.nick);

                final Student student = new Student();
                student.setSid(sid.getText().toString());
                student.setPassword(pwd.getText().toString());
                student.setRetypedPassword(retypedPwd.getText().toString());
                student.setNick(nick.getText().toString());

                Log.d("ppp", student.getSid() + " | " + student.getPassword() + " | " + student.getNick());

                regFragBinding.setStudent(student);
                mPresenter.reg(regFragBinding.getStudent());
            }
        });

        regFragBinding.toLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void setPresenter(@NonNull RegContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void success() {
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("验证链接已发送到您的邮箱, 请前往您的北京大学邮件系统(学号@pku.edu.cn)进行激活!")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .show();
//        Snackbar.make(getView(), "注册成功!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void regFail(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }
}
