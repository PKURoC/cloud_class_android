package com.vontroy.pku_ss_cloud_class.my_message;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.MyMsgItemAdapter;
import com.vontroy.pku_ss_cloud_class.data.MsgResult;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 16-11-17.
 */

public class MyMsgActivity extends BaseActivity {
    private ListView msg_list;
    private ArrayList<MyMsgInfo> historyMsgs;
    private MyMsgItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(this);
        String token = getParams.getString("token", "");
        String sid = getParams.getString("sid", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_msg_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的消息");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        msg_list = (ListView) findViewById(R.id.my_msg_list);

        historyMsgs = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("sid", sid);

        getHistoryMsg(params);

        adapter = new MyMsgItemAdapter(this, historyMsgs, token, sid);

        msg_list.setAdapter(adapter);
//        msg_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    Intent intent = new Intent(MyMsgActivity.this, MyMsgDetailActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }

    public void getHistoryMsg(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(MyMsgActivity.class.getSimpleName(), Request.Method.GET, ServerInterface.getHistoryMsg, params, MsgResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<MsgResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(MsgResult msgResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(msgResult.getCode())) {
                            try {
                                JSONObject result = new JSONObject(msgResult.getData().toString());
                                Iterator<String> keys = result.keys();

                                if (!keys.hasNext()) {
                                    MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有历史消息");
                                    myMsgInfo.setState("N");

                                    historyMsgs.add(myMsgInfo);
                                } else {
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        String msg = result.getString(key);

                                        String msgContent;
                                        String msgType;
                                        String msgTime;

                                        try {
                                            JSONObject jsonObject = new JSONObject(msg);
                                            msgTime = jsonObject.getString("time");
                                            msgType = jsonObject.getString("type");
                                            msgContent = jsonObject.getString("value");
                                        } catch (Exception e) {
                                            msgTime = "";
                                            msgType = "default";
                                            msgContent = msg;
                                        }

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

                                        Date date = new Date(Long.valueOf(msgTime));
                                        Date today = new Date();
                                        Date yesterday = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
                                        String dateStr = sdf.format(date);
                                        String todayStr = sdf.format(today);
                                        String yesterdayStr = sdf.format(yesterday);
                                        String currentTime = sdfTime.format(date);
                                        String[] timeArray = currentTime.split("#");
                                        String timeHMS = timeArray[1];

                                        String resultDate;

                                        MyMsgInfo myMsgInfo = new MyMsgInfo("", "");
                                        myMsgInfo.setAccurateTime(msgTime);

                                        String titleStr = "";
                                        switch (msgType) {
                                            case "joingroup":
                                                titleStr = "小组消息";
                                                break;
                                            case "newjob":
                                                titleStr = "作业提醒";
                                                break;
                                        }

                                        myMsgInfo.setTitle(titleStr);
                                        myMsgInfo.setContent(msgContent);
                                        if (todayStr.equals(dateStr)) {
                                            resultDate = "今天 " + timeHMS;
                                        } else if (yesterdayStr.equals(dateStr)) {
                                            resultDate = "昨天 " + timeHMS;
                                        } else {
                                            resultDate = dateStr + " " + timeHMS;
                                        }
                                        myMsgInfo.setTime(resultDate);
                                        myMsgInfo.setNotifyId(key);
                                        myMsgInfo.setState("H");

                                        historyMsgs.add(myMsgInfo);
                                    }
                                    Collections.sort(historyMsgs, new SortByTime());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有历史消息");
                            myMsgInfo.setState("N");
                            historyMsgs.add(myMsgInfo);
                        }
                        adapter.notifyDataSetChanged();
                    }

                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            MyMsgInfo msg1 = (MyMsgInfo) o1;
            MyMsgInfo msg2 = (MyMsgInfo) o2;
            return (msg1.getAccurateTime().compareTo(msg2.getAccurateTime())) * -1;
        }
    }
}
