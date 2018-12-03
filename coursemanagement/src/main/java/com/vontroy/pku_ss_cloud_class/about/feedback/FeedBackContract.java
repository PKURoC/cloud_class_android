package com.vontroy.pku_ss_cloud_class.about.feedback;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;

import java.util.Map;

/**
 * Created by vontroy on 2017/2/16.
 */

public class FeedBackContract {
    interface View extends BaseView<Presenter> {

        void submitFeedBackSuccess();
    }

    interface Presenter extends BasePresenter {

        void addFeedBack(@NonNull Map<String, String> params);
    }
}
