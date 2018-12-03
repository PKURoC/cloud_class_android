package com.vontroy.pku_ss_cloud_class.network;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.data.LoginResult;
import com.vontroy.pku_ss_cloud_class.data.RegResult;
import com.vontroy.pku_ss_cloud_class.data.Student;

import java.util.Map;

import rx.Observable;

/**
 * Created by LinkedME06 on 16/10/28.
 */

public interface ServerApi {

    /**
     * 通用的单次数据请求调用方法
     *
     * @param requestTag 请求标签，用于当前页面销毁时关闭对应标签的请求
     * @param method     请求方式，GET 或者 POST方式
     * @param url        请求地址
     * @param param      请求参数
     * @param clazz      返回的实体类
     * @param <T>        返回的实体类
     * @return
     */
    <T> Observable<T> common(String requestTag, int method, String url, Map<String, String> param, Class<T> clazz);

    Observable<RegResult> reg(String requestTag, @NonNull Student student);

    Observable<LoginResult> login(String requestTag, @NonNull Student student);

}
