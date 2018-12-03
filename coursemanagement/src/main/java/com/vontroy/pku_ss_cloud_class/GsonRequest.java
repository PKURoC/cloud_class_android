package com.vontroy.pku_ss_cloud_class;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lipeng on 2016 6-16.
 */
public class GsonRequest<T> extends Request<T> {
    private Map<String, String> param;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;


    /**
     * 默认的get方法请求
     *
     * @see #GsonRequest(int, String, Map, Class, Map, Response.Listener, Response.ErrorListener)
     */
    public GsonRequest(String url, Map<String, String> param, Class<T> clazz,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(url, param, clazz, null, listener, errorListener);
    }

    /**
     * @param method 传入请求方法
     * @see #GsonRequest(int, String, Map, Class, Map, Response.Listener, Response.ErrorListener)
     */
    public GsonRequest(int method, String url, Map<String, String> param, Class<T> clazz,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, param, clazz, null, listener, errorListener);
    }

    public GsonRequest(String url, Map<String, String> param, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, param, clazz, headers, listener, errorListener);
    }

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, Map<String, String> param, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.param = removeNullAndEmpty(param);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }

    /**
     * 去除map中NULL或者空字符串的数据项
     *
     * @param param
     * @return
     */
    private Map<String, String> removeNullAndEmpty(Map<String, String> param) {
        if (param == null) {
            return null;
        }
        Map<String, String> formatParam = new HashMap<>();
        Set<Map.Entry<String, String>> entry = param.entrySet();
        for (Map.Entry<String, String> format : entry) {
            if (!TextUtils.isEmpty(format.getValue())) {
                formatParam.put(format.getKey(), format.getValue());
            }
        }
        return formatParam;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            Gson gson = new Gson();
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d("debug", "parseNetworkResponse: " + json);
            return Response.success(gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return param;
    }
}
