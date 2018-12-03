package com.vontroy.pku_ss_cloud_class.my_message;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.MsgDetailItemAdapter;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-11-21.
 */

public class MyMsgDetailActivity extends BaseActivity {
    private ListView detail_msg_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_msg_detail_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的消息");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        detail_msg_list = (ListView) findViewById(R.id.msg_detail);

        ArrayList<MyMsgInfo> myMsgInfos = new ArrayList<>();

        myMsgInfos.add(new MyMsgInfo("天上掉馅饼了", "掉了一个大馅饼, 就是没有馅!!!!!!\n啊啊啊啊啊啊啊啊啊\n啊啊的快点快点快点快点快点看", "read", "今天 10:23"));
        myMsgInfos.add(new MyMsgInfo("学校被炸了", "用两个二踢脚一个窜天猴炸的!!!!!!", "read", "11-20 09:22"));
        myMsgInfos.add(new MyMsgInfo("大清亡了", "康师傅推翻了封建帝制!!!!!!", "read", "11-19 23:33"));
        myMsgInfos.add(new MyMsgInfo("地震了", "房子塌了!!!!!!", "read", "11-18 18:22"));
        myMsgInfos.add(new MyMsgInfo("挂了", "因为地震才挂的!!!!!!", "read", "11-17 10:11"));
        myMsgInfos.add(new MyMsgInfo("了", "了了了了了了!!!!!!", "read", "11-16 01:21"));

        MsgDetailItemAdapter adapter = new MsgDetailItemAdapter(this, myMsgInfos);

        detail_msg_list.setAdapter(adapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
