package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
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

public class MyMsgItemAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<MyMsgInfo> msgList;
    private String token;
    private String sid;

    public MyMsgItemAdapter(Context context, ArrayList<MyMsgInfo> myMsgInfos) {
        mContext = context;
        this.msgList = myMsgInfos;
    }

    public MyMsgItemAdapter(Context context, ArrayList<MyMsgInfo> myMsgInfos, String token, String sid) {
        mContext = context;
        this.msgList = myMsgInfos;
        this.token = token;
        this.sid = sid;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int i) {
        return msgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyMsgItemAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.my_msg_list_item, parent, false);
            holder = new MyMsgItemAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.msg_title = (TextView) convertView.findViewById(R.id.msg_title);
            holder.msg_content = (TextView) convertView.findViewById(R.id.msg_content);
            holder.msg_time = (TextView) convertView.findViewById(R.id.msg_time);
            holder.msg_top_layout = (LinearLayout) convertView.findViewById(R.id.top_layout);
            holder.msg_bottom_layout = (LinearLayout) convertView.findViewById(R.id.bottom_layout);
            holder.msg_pic = (SimpleDraweeView) convertView.findViewById(R.id.msg_pic);
            holder.tag_as_has_read = (TextView) convertView.findViewById(R.id.tag_as_has_read);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (MyMsgItemAdapter.ViewHolder) convertView.getTag();
        }

        final MyMsgInfo currentMsgInfo = msgList.get(position);

        if ("N".equals(currentMsgInfo.getState())) {
            holder.msg_top_layout.setVisibility(View.GONE);
            holder.msg_bottom_layout.setMinimumHeight(holder.msg_pic.getHeight());
            holder.msg_content.setText(currentMsgInfo.getContent());
        } else if ("R".equals(currentMsgInfo.getState())) {
            holder.msg_title.setText(currentMsgInfo.getTitle());
            holder.msg_content.setText(currentMsgInfo.getContent());
            holder.msg_time.setText(currentMsgInfo.getTime());
            holder.tag_as_has_read.setVisibility(View.VISIBLE);
            holder.tag_as_has_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("sid", sid);
                    params.put("notifyid", currentMsgInfo.getNotifyId());

                    hasRead(params);
                }
            });
        } else if ("H".equals(currentMsgInfo.getState())) {
            holder.msg_title.setText(currentMsgInfo.getTitle());
            holder.msg_content.setText(currentMsgInfo.getContent());
            holder.msg_time.setText(currentMsgInfo.getTime());
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView msg_title;
        TextView msg_content;
        TextView msg_time;
        TextView tag_as_has_read;
        LinearLayout msg_top_layout;
        LinearLayout msg_bottom_layout;
        SimpleDraweeView msg_pic;
    }

    public void getRecent(Map params) {
        msgList.clear();
        Subscription subscription = ServerImp.getInstance()
                .common(MyMsgItemAdapter.class.getSimpleName(), Request.Method.GET, ServerInterface.getRecent, params, MsgResult.class)
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
                                    MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有未读消息");
                                    myMsgInfo.setState("N");

                                    msgList.add(myMsgInfo);
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
                                        myMsgInfo.setTitle(msgType);
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
                                        myMsgInfo.setState("R");

                                        msgList.add(myMsgInfo);
                                    }
                                    Collections.sort(msgList, new SortByTime());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapterRefresh();
                        } else {
                            MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有未读消息");
                            myMsgInfo.setState("N");
                            msgList.add(myMsgInfo);
                        }
                        adapterRefresh();
                    }

                });
    }

    public void hasRead(Map<String, String> params) {
        final Map<String, String> getNewListParams = new HashMap<>();
        getNewListParams.put("sid", params.get("sid"));
        getNewListParams.put("token", params.get("token"));

        Subscription subscription = ServerImp.getInstance()
                .common(MyMsgItemAdapter.class.getSimpleName(), Request.Method.GET, ServerInterface.hasRead, params, BaseResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<BaseResult>() {
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
                    public void onNext(BaseResult baseResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(baseResult.getCode())) {
                            Toast.makeText(mContext, "操作成功!", Toast.LENGTH_SHORT).show();
                            getRecent(getNewListParams);
                        }

                    }

                });
    }

    private void adapterRefresh() {
        notifyDataSetChanged();
    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            MyMsgInfo msg1 = (MyMsgInfo) o1;
            MyMsgInfo msg2 = (MyMsgInfo) o2;
            return (msg1.getAccurateTime().compareTo(msg2.getAccurateTime())) * -1;
        }
    }
}
