package com.vontroy.pku_ss_cloud_class.user_account.reset_password;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.ResetpwdFragBinding;
import com.vontroy.pku_ss_cloud_class.utils.Decrypt;

import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 2017-02-24.
 */

public class ResetPwdFragment extends Fragment implements ResetPwdContract.View {
    private ResetpwdFragBinding resetpwdFragBinding;
    private ResetPwdContract.Presenter mPresenter;

    public static ResetPwdFragment newInstance() {
        return new ResetPwdFragment();
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
        final View root = inflater.inflate(R.layout.resetpwd_frag, container, false);

        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(root.getContext());
        final String token = getParams.getString("token", "");
        final String sid = getParams.getString("sid", "");

        resetpwdFragBinding = ResetpwdFragBinding.bind(root);

        resetpwdFragBinding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originPwd = resetpwdFragBinding.originPwd.getText().toString();
                String newPwd = resetpwdFragBinding.newPassword.getText().toString();
                String retypedPwd = resetpwdFragBinding.retypedNewPwd.getText().toString();
                if (Strings.isNullOrEmpty(originPwd)) {
                    Toast.makeText(getActivity(), "原始密码不能为空!", Toast.LENGTH_LONG).show();
                } else if (Strings.isNullOrEmpty(newPwd)) {
                    Toast.makeText(getActivity(), "新密码不能为空!", Toast.LENGTH_LONG).show();
                } else if (Strings.isNullOrEmpty(retypedPwd)) {
                    Toast.makeText(getActivity(), "请确认新密码!", Toast.LENGTH_LONG).show();
                } else if (!newPwd.equals(retypedPwd)) {
                    Toast.makeText(getActivity(), "两次输入的新密码不一致!", Toast.LENGTH_LONG).show();
                } else {
                    Map<String, String> params = new HashMap<>();
                    params.put("sid", sid);
                    params.put("token", token);
                    params.put("password", Decrypt.MD5(originPwd));
                    params.put("newpassword", Decrypt.MD5(newPwd));

                    mPresenter.resetPassword(params);
                }
            }
        });

        return root;
    }

    @Override
    public void setPresenter(@NonNull ResetPwdContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void resetPwdSuccess() {
        Toast.makeText(getActivity(), "重置密码成功!", Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    @Override
    public void resetPwdFailed(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}
