package com.vontroy.pku_ss_cloud_class.about.feedback;

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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.FeedBackFragBinding;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 2017/2/16.
 */

public class FeedBackFragment extends Fragment implements FeedBackContract.View, TabHost.TabContentFactory {
    private FeedBackFragBinding feedBackFragBinding;
    private FeedBackContract.Presenter mPresenter;

    public static FeedBackFragment newInstance() {
        return new FeedBackFragment();
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
        View root = inflater.inflate(R.layout.feed_back_frag, container, false);

        feedBackFragBinding = FeedBackFragBinding.bind(root);
        feedBackFragBinding.submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String token = getToken.getString("token", "");
                String sid = getToken.getString("sid", "");
                String feedBackContent = feedBackFragBinding.feedbackContent.getText().toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", sid);
                params.put("token", token);
                params.put("message", feedBackContent);
                mPresenter.addFeedBack(params);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setPresenter(@NonNull FeedBackContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }

    @Override
    public void submitFeedBackSuccess() {
        Toast.makeText(getActivity(), "提交成功, 谢谢您的反馈!", Toast.LENGTH_SHORT).show();
    }
}
