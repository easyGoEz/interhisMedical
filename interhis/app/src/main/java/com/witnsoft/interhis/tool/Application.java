package com.witnsoft.interhis.tool;


import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.mainpage.MainActivity;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/5/12.
 */


public class Application extends MultiDexApplication {

    private static Application app = null;
    private EMMessageListener mMessageListener;
    public static final String BROADCAST_REFRESH_LIST = "broadcastRefreshList";
    private static final String MESSAGE_USER_NAME = "messageUserName";

    public static synchronized Application getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        HisDbManager.attachTo(this);
        app = this;
        init();
        registerMessageListener();

    }

    private void init() {
        //环信聊天初始化
        EaseUI.getInstance().init(this, null, MainActivity.class);
        EMClient.getInstance().setDebugMode(true);
    }


    public void registerMessageListener() {
        mMessageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> list) {
                Log.e("MainActivity", "!!!!!!!!!!!!########");
//                for (EMMessage message : list) {
//                    if (!EaseUI.getInstance().hasForegroundActivies()) {
//                        EaseUI.getInstance().getNotifier().onNewMsg(message);
//                    }
//
//                }
                if (!EaseUI.getInstance().hasForegroundActivies()) {
                    // 推送
                    EaseUI.getInstance().getNotifier().onNewMesg(list);
                }
                ArrayList<String> from = new ArrayList<>();
                for (EMMessage message : list) {
                    from.add(message.getFrom());
                }
                // 收到新消息，发通知给doctorFragment
                // 接收到新消息，将username发送广播通知DoctorFragment刷新列表
                // 检查推送返回list数据，发通知给doctorFragment，检查是否更新会话列表请求接口，否则全部都是notify未读消息数，减少请求服务器次数
                Intent intent = new Intent();
                intent.putStringArrayListExtra("user_from", from);
                intent.setAction(BROADCAST_REFRESH_LIST);
                sendBroadcast(intent);

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }
}