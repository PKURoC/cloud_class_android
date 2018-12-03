package com.vontroy.pku_ss_cloud_class.network;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.VolleySingleton;
import com.vontroy.pku_ss_cloud_class.GsonRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by LinkedME06 on 16/10/28.
 */

public class ServerData<T> {


    public ServerData() {
    }

    protected T getServerData(String requestTag, int method, String url, Map<String, String> param, Class<T> clazz) throws ExecutionException, InterruptedException {
        RequestFuture<T> future = RequestFuture.newFuture();

        if (method == Request.Method.GET && !param.isEmpty()) {
            url += addParameters(param);
            param.clear();
        }
        GsonRequest<T> gsonRequest = new GsonRequest<T>(method, url, param, clazz, future, future);
        gsonRequest.setTag(requestTag);
        //添加请求任务
        VolleySingleton.getInstance().addToRequestQueue(gsonRequest);
        return future.get();
    }

    private String addParameters(Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }
            return "?" + encodedParams.toString().substring(0, encodedParams.length() - 1);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "UTF-8", uee);
        }
    }

}
