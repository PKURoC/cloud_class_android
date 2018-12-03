package com.vontroy.pku_ss_cloud_class;

import android.app.Application;
import android.util.Log;

import com.android.volley.toolbox.VolleySingleton;
import com.facebook.drawee.backends.pipeline.Fresco;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化volley
        VolleySingleton.init(this);
        Fresco.initialize(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        UploadService.HTTP_STACK = new OkHttpStack(getOkHttpClient());
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .cache(null)
                .build();
    }
}
