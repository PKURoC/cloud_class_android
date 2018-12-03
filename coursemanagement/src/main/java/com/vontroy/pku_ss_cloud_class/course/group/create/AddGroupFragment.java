package com.vontroy.pku_ss_cloud_class.course.group.create;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.AddGroupFragBinding;

import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddGroupFragment extends Fragment implements AddGroupContract.View, TabHost.TabContentFactory {
    private AddGroupFragBinding addGroupFragBinding;
    private AddGroupContract.Presenter mPresenter;

    public static AddGroupFragment newInstance() {
        return new AddGroupFragment();
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
        View root = inflater.inflate(R.layout.add_group_frag, container, false);
        final Bundle bundle = this.getArguments();

        addGroupFragBinding = AddGroupFragBinding.bind(root);
        addGroupFragBinding.addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String token = getToken.getString("token", "");
                String sid = getToken.getString("sid", "");

                Map params = new HashMap();
                params.put("token", token);
                params.put("sid", sid);
                EditText groupName = (EditText) getActivity().findViewById(R.id.group_name);
                EditText groupIntroduction = (EditText) getActivity().findViewById(R.id.group_introduction);
                params.put("name", groupName.getText().toString());
                params.put("about", groupIntroduction.getText().toString());

                String courseId = bundle.getString("course_id");
                params.put("cid", courseId);

                mPresenter.addGroup(params);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setPresenter(@NonNull AddGroupContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }

    @Override
    public void createGroupSuccess() {
        Toast.makeText(getActivity(), "创建分组成功!", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

}
