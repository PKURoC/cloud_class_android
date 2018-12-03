package com.vontroy.pku_ss_cloud_class.storage.crypto.states;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.databinding.StatesFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 2017/11/15.
 */

public class StatesFragment extends Fragment implements StatesContract.View, TabHost.TabContentFactory {
    private static final String DEFAULT_ATTRIBUTE = "school:pku,academy:computer,籍贯:北京,age:130";
//    private static final String DEFAULT_ATTRIBUTE = "学校:北京大学,专业:软件工程,课程:应用密码学,年级:2015级";

    private StatesFragBinding statesFragmentBinding;
    private String attributesJsonArray;
    private View mRootView;
    private String sid;
    private String jsonPK;
    private String jsonSK;

    public static StatesFragment newInstance() {
        Bundle args = new Bundle();
        //args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        StatesFragment fileUploadFragment = new StatesFragment();
        fileUploadFragment.setArguments(args);
        return fileUploadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.states_frag, container, false);
        }

        statesFragmentBinding = StatesFragBinding.bind(mRootView);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        jsonPK = sharedPreferences.getString("public_key", "");
        jsonSK = sharedPreferences.getString("secret_key", "");
        sid = sharedPreferences.getString("sid", "");

        if (Strings.isNullOrEmpty(jsonPK)) {
            statesFragmentBinding.pkState.setText("未获取");
            statesFragmentBinding.pkState.setTextColor(Color.BLACK);
        } else {
            statesFragmentBinding.pkState.setText("已获取");
        }

        if (Strings.isNullOrEmpty(jsonSK)) {
            statesFragmentBinding.skState.setText("未获取");
            statesFragmentBinding.skState.setTextColor(Color.BLACK);
        } else {
            statesFragmentBinding.skState.setText("已获取");
        }


        statesFragmentBinding.getPkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetPK().execute();
            }
        });

        statesFragmentBinding.editAttribute.setText(DEFAULT_ATTRIBUTE);

        statesFragmentBinding.skContentTv.setText(jsonSK);

        statesFragmentBinding.getSkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String attributeStr = statesFragmentBinding.editAttribute.getText().toString();
                String[] attributesArray = attributeStr.split(",");
                attributesJsonArray = JSON.toJSONString(attributesArray);
                Log.d("time_state", "get sk begin time: " + System.currentTimeMillis());
                new GetSK().execute();

            }
        });

        statesFragmentBinding.exportSkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String sk_dir = Environment.getExternalStorageDirectory() + "/软微云课堂/skExport/";
                    String sk_file_name = "sk_export.key";
                    FileOutputStream outputStream = new FileOutputStream(sk_dir + sk_file_name);
                    outputStream.write(jsonSK.getBytes());
                    outputStream.close();
                    Toast.makeText(getContext(), "导出密钥成功！存储路径为: " + sk_dir + sk_file_name, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        statesFragmentBinding.importSkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sk_dir = Environment.getExternalStorageDirectory() + "/软微云课堂/skExport/";
                String sk_file_name = "sk_export.key";
                String tmp_sk = readFile(sk_dir + sk_file_name);
                if (Strings.isNullOrEmpty(tmp_sk)) {
                    Toast.makeText(getContext(), "导入sk失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                jsonSK = tmp_sk;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("secret_key", jsonSK);
                editor.apply();
                statesFragmentBinding.skContentTv.setText(jsonSK);
                Toast.makeText(getContext(), "密钥导入成功！", Toast.LENGTH_SHORT).show();
            }
        });

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @SuppressLint("StaticFieldLeak")
    class GetPK extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = ServerInterface.getPKUrl;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功 获得返回的流
                    InputStream is = connection.getInputStream();
                    return convertStreamToString(is);
                } else {
                    //请求失败
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = JSON.parseObject(result);
            jsonPK = jsonObject.get("PK").toString();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("public_key", jsonPK);
            editor.apply();
            statesFragmentBinding.pkState.setText("已获取");
            statesFragmentBinding.pkState.setTextColor(Color.GREEN);
            Toast.makeText(getContext(), "公钥获取成功!", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetSK extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = ServerInterface.getSKUrl;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //数据准备
                String data = "attributes=" + attributesJsonArray + "&sid=" + sid;
                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());

                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功
                    InputStream is = connection.getInputStream();
                    Log.d("time_state", "get sk end time: " + System.currentTimeMillis());
                    return convertStreamToString(is);

                } else {
                    //请求失败
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = JSON.parseObject(result);
            jsonSK = jsonObject.get("SK").toString();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("secret_key", jsonSK);
            editor.apply();
            statesFragmentBinding.skState.setText("已获取");
            statesFragmentBinding.skState.setTextColor(Color.GREEN);
            Toast.makeText(getContext(), "私钥获取成功!", Toast.LENGTH_LONG).show();
        }

    }

    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public String readFile(String fileName) {

        File file = new File(fileName);
        BufferedReader reader = null;
        String str = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            // read line by line until null
            while ((tempString = reader.readLine()) != null) {
                str = tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return str;
    }


    @Override
    public View createTabContent(String s) {
        return null;
    }

    @Override
    public void setPresenter(StatesContract.Presenter presenter) {

    }
}
