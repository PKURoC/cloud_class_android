package com.vontroy.pku_ss_cloud_class.user_account.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.databinding.LoginFragBinding;
import com.vontroy.pku_ss_cloud_class.user_account.register.RegActivity;
import com.vontroy.pku_ss_cloud_class.utils.Decrypt;

import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/10/27.
 */

public class LoginFragment extends Fragment implements LoginContract.View {

    private LoginFragBinding loginFragBinding;
    private LoginContract.Presenter mPresenter;

    public static LoginFragment newInstance() {
        return new LoginFragment();
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.login_frag, container, false);

        loginFragBinding = LoginFragBinding.bind(root);

        loginFragBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText sid = (EditText) root.findViewById(R.id.name);
                EditText pwd = (EditText) root.findViewById(R.id.password);
                final Student student = new Student();
                student.setSid(sid.getText().toString());
                student.setPassword(pwd.getText().toString());

                Log.d("ppp", student.getSid() + " " + student.getPassword());
                loginFragBinding.setStudent(student);
                mPresenter.login(loginFragBinding.getStudent());
            }
        });

        loginFragBinding.register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegActivity.class);
                startActivity(intent);
            }
        });

        loginFragBinding.forgetPwd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                EditText inputStudentNumber = new EditText(root.getContext());
//                EditText inputNewPwd = new EditText(root.getContext());
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View dialogView = layoutInflater.inflate(R.layout.forgot_pwd_edittext_dialog, null);

                final EditText inputStudentNumber = (EditText) dialogView.findViewById(R.id.forgot_pwd_sid);
                final EditText inputNewPwd = (EditText) dialogView.findViewById(R.id.forgot_pwd_new_pwd);

                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                builder.setTitle("忘记密码");
                builder.setView(dialogView);
                builder.setPositiveButton("找回密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newPwd = inputNewPwd.getText().toString();
                        String sid = inputStudentNumber.getText().toString();

                        Map<String, String> forgotPwdParams = new HashMap<String, String>();
                        forgotPwdParams.put("sid", sid);
                        forgotPwdParams.put("password", Decrypt.MD5(newPwd));

                        mPresenter.forgotPwd(forgotPwdParams);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        return root;
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void success() {
        Log.d("debug", "login success");
        Toast.makeText(getActivity(), "登录成功!", Toast.LENGTH_SHORT).show();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setToken(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();
    }

    @Override
    public void setSid(String sid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sid", sid);
        editor.commit();
    }

    @Override
    public void setNick(String nick) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nick", nick);
        editor.commit();
    }

    @Override
    public void resetPwdSuccess(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("忘记密码")
                .setMessage(msg)
                .setPositiveButton("知道了", null)
                .show();
    }

    @Override
    public void fail(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
    }
}
