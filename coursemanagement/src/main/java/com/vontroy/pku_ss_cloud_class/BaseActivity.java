package com.vontroy.pku_ss_cloud_class;

import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.VolleySingleton;

/**
 * Created by LinkedME06 on 16/10/26.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getName();

    @Override
    protected void onStop() {
        super.onStop();
        //停止当前页面的所有请求
        VolleySingleton.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return ((String) request.getTag()).contains(TAG);
            }
        });
    }

}
