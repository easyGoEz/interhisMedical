package com.witnsoft.interhis.tool;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.mainpage.MainActivity;

import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.witnsoft.interhis.db.HisDbManager.TAG;

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
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
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

    /*
     * 在此对Activity的生命周期事件进行集中处理
     */
    public List<WeakReference<Activity>> getActivityList() {
        return actList;
    }

    private List<WeakReference<Activity>> actList = new ArrayList<WeakReference<Activity>>();

    private class ActivityLifecycleCallbacksImpl implements
            Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity,
                                      Bundle savedInstanceState) {
            Log.d(TAG,
                    "in onActivityCreated(), activity=" + activity.getClass());
            actList.add(new WeakReference<Activity>(activity));
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG,
                    "in onActivityStarted(), activity=" + activity.getClass());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG,
                    "in onActivityResumed(), activity=" + activity.getClass());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG,
                    "in onActivityPaused(), activity=" + activity.getClass());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG,
                    "in onActivityStopped(), activity=" + activity.getClass());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.d(TAG, "in onActivitySaveInstanceState(), activity="
                    + activity.getClass());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG,
                    "in onActivityDestroyed(), activity=" + activity.getClass());
            int length = actList.size();
            int delPos = -1;
            for (int i = 0; i < length; i++) {
                WeakReference<Activity> wf = actList.get(i);
                if (null != wf) {
                    Activity act = wf.get();
                    if ((null != act) && (act.equals(activity))) {
                        delPos = i;
                        Log.e(TAG, "in onActivityDestroyed(), delPos=" + delPos
                                + ", activity=" + activity.getClass());
                    }
                }
            }
            if (-1 < delPos) {
                actList.remove(delPos);
            }
        }
    }

    ;
}