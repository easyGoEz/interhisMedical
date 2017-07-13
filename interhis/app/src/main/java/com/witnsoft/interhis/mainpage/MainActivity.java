package com.witnsoft.interhis.mainpage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.util.NetUtils;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.updatemodel.ChuFangChinese;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.adapter.PatAdapter;
import com.witnsoft.interhis.rightpage.RightMainFragment;
import com.witnsoft.interhis.setting.SettingActivity;
import com.witnsoft.interhis.tool.Application;
import com.witnsoft.interhis.utils.AppUtils;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ConnectionUtils;
import com.witnsoft.interhis.utils.PermissionUtil;
import com.witnsoft.libinterhis.base.BaseActivity;
import com.witnsoft.libinterhis.utils.LogUtils;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleFrameLayout;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.LoginRequest;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by zhengchengpeng on 2017/5/12.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static LogUtils logUtils = LogUtils.getLog();

    private static final String TN_DOC_INFO = "F27.APP.01.01";
    private static final String TN_COUNT = "F27.APP.01.05";
    private static final String TN_PAT_LIST = "F27.APP.01.02";
    private static final String TN_VISIT = "F27.APP.01.03";
    private static final String DOC_ID = "docid";
    private static final String DATA = "DATA";
    private static final String ROWSPERPAGE = "rowsperpage";
    private static final String PAGE_NO = "pageno";
    private static final String ORDER_COLUMN = "ordercolumn";
    private static final String ORDER_TYPE = "ordertype";
    private static final String WORK_FLAG = "workflag";
    private static final String LOGOUT = "logout";
    private static final String LOGIN_NAME = "LOGINNAME";
    private static final String ERRO_MSG = "errmsg";
    private static final int PAGE_COUNT = 10;

    private final class ErrCode {
        private static final String ErrCode_200 = "200";
        private static final String ErrCode_504 = "504";
    }

    private final class DocInfoResponseKey {
        private static final String DOC_NAME = "docname";
        private static final String ZYDJ = "zydj";
        private static final String PJFS = "pjfs";
        private static final String JZL = "jzl";
        private static final String SSYYMC = "ssyymc";
        private static final String SSKB1MC = "sskb1mc";
        private static final String PHOTO_URL = "photourl";
    }

    private final class CountResponseKey {
        private static final String DD = "dd";
        private static final String JZL = "jzl";
        private static final String BRSR = "brsr";
        private static final String LJSR = "ljsr";
    }

    private PatAdapter patAdapter = null;
    private Gson gson;
    private String docId = "";
    private List<Map<String, String>> dataChatList = new ArrayList();

    private String docName = "";
    private String docLevel = "";
    private String docHospName = "";
    private String docDept = "";
    private String headImg = "";

    // 下拉刷新
    @ViewInject(R.id.sl_refresh_view)
    private SwipeRefreshLayout slRefresh;
    // 联系人优化页
    @ViewInject(R.id.tv_no_content)
    private TextView tvNoContact;
    // 医生头像
    @ViewInject(R.id.fragment_doctor_image)
    private CircleImageView ivHead;
    // 患者列表
    @ViewInject(R.id.rv_pat_view)
    private RecyclerView recyclerView;
    // 医生姓名
    @ViewInject(R.id.tv_doctor_name)
    private TextView tvDocName;
    // 医生职称
    @ViewInject(R.id.tv_doctor_duties)
    private TextView tvDocDuties;
    // 医生评分
    @ViewInject(R.id.tv_doctor_grade)
    private TextView tvDocGrade;
    // 接诊量
    @ViewInject(R.id.tv_doctor_number)
    private TextView tvDocNum;
    // 医生所在医院，科室
    @ViewInject(R.id.tv_hospital)
    private TextView tvHosp;
    // 等待人数
    @ViewInject(R.id.tv_pats_waiting)
    private TextView tvPatWaiting;
    // 累计人人数
    @ViewInject(R.id.tv_pats_all)
    private TextView tvPatAll;
    // 本日收入
    @ViewInject(R.id.tv_daily_income)
    private TextView tvDailyIncome;
    // 累计收入
    @ViewInject(R.id.tv_all_income)
    private TextView tvAllIncome;
    //医生信息
    @ViewInject(R.id.doctor_message)
    private AutoScaleLinearLayout llDocMessage;
    //医生接诊数量
    @ViewInject(R.id.doctor_count)
    private AutoScaleLinearLayout doctor_number;
    //出诊按钮
    @ViewInject(R.id.doctor_visit)
    private Button btnVisit;
    // 退出登录／收工
    @ViewInject(R.id.btn_take_rest)
    private Button btnTakeRest;
    // 右侧fragment container
    @ViewInject(R.id.fl_helper)
    private AutoScaleFrameLayout flHelper;

    private OkHttpClient okHttpClient;
    private Handler handler = new Handler(Looper.getMainLooper());

    private static final String TN_UPDATE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(MainActivity.this);
        mProgressDialog = getProgressDialog();
        init();
        initClick();
        callUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (2 == resultCode) {
            // 设置上传头像成功返回刷新头像
            int isRefresh = data.getIntExtra(SettingActivity.UPDATE_IMG, -1);
            if (1 == isRefresh) {
                String img;
                if (!TextUtils.isEmpty(ThriftPreUtils.getHeadImg(MainActivity.this))) {
                    img = ThriftPreUtils.getHeadImg(MainActivity.this);
                    Glide.with(MainActivity.this)
                            .load(img)
                            .error(R.drawable.touxiang)
                            .into(ivHead);
                    headImg = img;
                }
//                callDocInfoApi(false);
            }
        }
    }

    private void init() {
        gson = new Gson();
        slRefresh.setEnabled(false);
        // 接收新消息通知广播
        receiver = new RefreshFriendListBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(BROADCAST_REFRESH_LIST));
        //注册一个监听连接状态的listener，监听环信账号登录状态
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        int notificationAction = getIntent().getIntExtra("Notification", -1);
        Log.e("MainActivity", "notification test in fragment = " + String.valueOf(notificationAction));
        if (1 == notificationAction) {
            // 通过点击通知进入
            isVisiting = true;
        }
        if (!isVisiting) {
            // 不在出诊状态
            setBtnRest();
        } else {
            // 出诊中
            setBtnVisiting();
        }
        docId = ThriftPreUtils.getDocId(this);
        callDocInfoApi(true);
        callCountApi(true);
    }

    private void initClick() {
        btnVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 出诊
                callVisitApi();
//                    getChatList();
//                callPatListApi();
            }
        });
        llDocMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra(SettingActivity.DOC_NAME, docName);
                intent.putExtra(SettingActivity.DOC_LEVEL, docLevel);
                intent.putExtra(SettingActivity.DOC_HOSP, docHospName);
                intent.putExtra(SettingActivity.DOC_DEPT, docDept);
                intent.putExtra(SettingActivity.DOC_HEAD, headImg);
                startActivityForResult(intent, 12);
            }
        });
        btnTakeRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVisiting) {
                    // 退出登录
                    callLogoutApi();
                } else {
                    // 收工
                    callVisitApi();
                }
            }
        });
    }

    /**
     * F27.APP.01.01 查询医生详细信息
     */
    private void callDocInfoApi(boolean isProgress) {
        OTRequest otRequest = new OTRequest(this);
        // DATA
        DataModel data = new DataModel();
        data.setParam(DOC_ID, docId);
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN(TN_DOC_INFO);

        NetTool.getInstance().startRequest(false, isProgress, this, null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        if (null != response.get(DATA)) {
                            Map<String, String> data = (Map<String, String>) response.get(DATA);
                            // 医生姓名
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.DOC_NAME))) {
                                docName = data.get(DocInfoResponseKey.DOC_NAME);
                                tvDocName.setText(data.get(DocInfoResponseKey.DOC_NAME));
                            }
                            // 医生职称
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.ZYDJ))) {
                                docLevel = data.get(DocInfoResponseKey.ZYDJ);
                                tvDocDuties.setText(data.get(DocInfoResponseKey.ZYDJ));
                            }
                            // 医生评分
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.PJFS))) {
                                tvDocGrade.setText(data.get(DocInfoResponseKey.PJFS));
                            }
                            // 接诊量
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.JZL))) {
                                tvDocNum.setText(data.get(DocInfoResponseKey.JZL));
                            }
                            // 医生所在医院，科室
                            String hosp = "";
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.SSYYMC))) {
                                docHospName = data.get(DocInfoResponseKey.SSYYMC);
                                hosp = data.get(DocInfoResponseKey.SSYYMC);
                            }
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.SSKB1MC))) {
                                docDept = data.get(DocInfoResponseKey.SSKB1MC);
                                hosp = hosp + " " + data.get(DocInfoResponseKey.SSKB1MC);
                            }
                            if (!TextUtils.isEmpty(hosp)) {
                                tvHosp.setText(hosp);
                            }
                            // 医生头像
                            if (!TextUtils.isEmpty(data.get(DocInfoResponseKey.PHOTO_URL))) {
                                Glide.with(MainActivity.this)
                                        .load(data.get(DocInfoResponseKey.PHOTO_URL))
                                        .error(R.drawable.touxiang)
                                        .into(ivHead);
                                headImg = data.get(DocInfoResponseKey.PHOTO_URL);
                                ThriftPreUtils.putHeadImg(MainActivity.this, headImg);
                            }
                            // 医生擅长
                            if (!TextUtils.isEmpty(data.get("sc"))) {
                                ThriftPreUtils.putDocExpert(MainActivity.this, data.get("sc"));
                            }
                        }
                    }
//                    callPatApi();
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    /**
     * F27.APP.01.05 获得统计值
     */
    private void callCountApi(boolean isProgress) {
        OTRequest otRequest = new OTRequest(this);
        // DATA
        DataModel data = new DataModel();
        data.setParam(DOC_ID, docId);
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN(TN_COUNT);

        NetTool.getInstance().startRequest(false, isProgress, this, null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        doctor_number.setVisibility(View.VISIBLE);
                        Map<String, String> data = (Map<String, String>) response.get(DATA);
                        // 等待人数
                        if (!TextUtils.isEmpty(data.get(CountResponseKey.DD))) {
                            tvPatWaiting.setText(data.get(CountResponseKey.DD));
                        }
                        // 累计人数
                        if (!TextUtils.isEmpty(data.get(CountResponseKey.JZL))) {
                            tvPatAll.setText(data.get(CountResponseKey.JZL));
                        }
                        // 本日收入
                        if (!TextUtils.isEmpty(data.get(CountResponseKey.BRSR))) {
                            tvDailyIncome.setText(data.get(CountResponseKey.BRSR));
                        }
                        // 累计收入
                        if (!TextUtils.isEmpty(data.get(CountResponseKey.LJSR))) {
                            tvAllIncome.setText(data.get(CountResponseKey.LJSR));
                        }
                    }
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    private int pageNo = 1;
    private List<Map<String, String>> respList;
    // 记录点击位置
    private int checkedPosition = -1;
    private FragmentTransaction fragmentManager;

    /**
     * F27.APP.01.02 查询问诊人员列表
     */
    private void callPatListApi(boolean isProgress) {
        OTRequest otRequest = new OTRequest(this);
        // DATA
        final DataModel data = new DataModel();
        data.setParam(DOC_ID, docId);
        data.setParam(ORDER_COLUMN, "paytime");
        data.setParam(ORDER_TYPE, "asc");
        data.setParam(ROWSPERPAGE, String.valueOf(PAGE_COUNT));
        //分页
        data.setParam(PAGE_NO, String.valueOf(pageNo));
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN(TN_PAT_LIST);

        NetTool.getInstance().startRequest(false, isProgress, this, null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(final Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnVisit.setEnabled(false);
                            if (null != response) {
                                respList = new ArrayList<Map<String, String>>();
                                respList = (List<Map<String, String>>) response.get(DATA);
                                if (null != respList && 0 < respList.size()) {
                                    if (1 == pageNo) {
                                        // 如果是第一页，表示重新加载数据
                                        dataChatList.clear();
                                        for (int i = 0; i < respList.size(); i++) {
                                            dataChatList.add(respList.get(i));
                                        }
//                                        dataChatList = respList;
                                    } else {
                                        // 不是第一页，表示分页加载
                                        for (int i = 0; i < respList.size(); i++) {
                                            dataChatList.add(respList.get(i));
                                        }
                                    }
                                    // 记录点击位置
                                    if (-1 < checkedPosition) {
                                        try {
                                            dataChatList.get(checkedPosition).put("color", "changed");
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                            Log.e(TAG, "!!!!ArrayIndexOutOfBoundsException in checkedPosition");
                                        }
                                    }
                                    // 获取未读消息
                                    setDataChatListUnRead();
                                }

                                if (null != dataChatList && 0 < dataChatList.size()) {
                                    tvNoContact.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                } else if (1 == pageNo) {
                                    // 保证是第一次加载时的判断，分页加载不显示优化页
                                    tvNoContact.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                                if (null == patAdapter) {
                                    patAdapter = new PatAdapter(MainActivity.this, dataChatList, R.layout.fragment_doctor_recycleview_item);
                                    patAdapter.setFootViewId(R.layout.activity_load_footer);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setAdapter(patAdapter);
                                }
                                patAdapter.setCanNotReadBottom(false);
                                patAdapter.setOnRecyclerViewBottomListener(new ComRecyclerAdapter.OnRecyclerViewBottomListener() {
                                    @Override
                                    public void OnBottom() {
                                        Log.d(TAG, "OnBottom: out");
                                        if (respList != null && respList.size() == PAGE_COUNT) {
                                            slRefresh.setEnabled(false);
                                            slRefresh.setRefreshing(true);
                                            patAdapter.setCanNotReadBottom(true);
                                            Log.d(TAG, "OnBottom: in");
                                            pageNo++;
                                            callPatListApi(true);
                                        }
                                    }
                                });
                                patAdapter.setOnItemClickListener(new ComRecyclerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onClick(View v, int position) {
                                        checkedPosition = position;
                                        for (int i = 0; i < dataChatList.size(); i++) {
                                            // 设置点击变色
                                            dataChatList.get(i).put("color", "unchanged");
                                        }
                                        // 设置已读
                                        dataChatList.get(position).put("readNo", "0");
                                        if (!TextUtils.isEmpty(dataChatList.get(position).get("ACCID"))) {
                                            try {
                                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(dataChatList.get(position).get("ACCID"));
                                                //指定会话消息未读数清零
                                                conversation.markAllMessagesAsRead();
                                            } catch (Exception e) {

                                            }
                                        }
                                        dataChatList.get(position).put("color", "changed");
                                        patAdapter.notifyDataSetChanged();
                                        Log.e(TAG, "onClick: " + dataChatList.get(position).get("AIID"));
                                        //启动会话列表
                                        try {
                                            RightMainFragment rightMainFragment = new RightMainFragment();
                                            Bundle bundle = new Bundle();
                                            // 问诊id
                                            bundle.putString("aiid", dataChatList.get(position).get("AIID"));
                                            bundle.putString("userName", EaseConstant.EXTRA_USER_ID);
                                            // 环信聊天唯一标识
                                            bundle.putString("userId", dataChatList.get(position).get("ACCID"));
                                            bundle.putString("type", EaseConstant.EXTRA_CHAT_TYPE);
                                            bundle.putInt("single", EaseConstant.CHATTYPE_SINGLE);
                                            // 医生头像
                                            bundle.putString("img_doc", headImg);
                                            // 患者头像
                                            bundle.putString("img_pat", dataChatList.get(position).get("PHOTOURL"));
                                            // 患者姓名
                                            bundle.putString("pat_name", dataChatList.get(position).get("PATNAME"));
                                            // 性别
                                            bundle.putString("pat_sex_name", dataChatList.get(position).get("PATSEXNAME"));
                                            // 病人id
                                            bundle.putString("pat_id", dataChatList.get(position).get("PATID"));
                                            rightMainFragment.setArguments(bundle);
                                            fragmentManager = getSupportFragmentManager().beginTransaction();
//                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentManager.replace(R.id.fl_helper, rightMainFragment, MainActivity.class.getSimpleName());
                                            fragmentManager.addToBackStack(null);
                                            fragmentManager.commitAllowingStateLoss();
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                            Log.e(TAG, "!!!!!!!!!!!!!ArrayIndexOutOfBoundsException in freshUi()");
                                        }
                                    }
                                });
                                patAdapter.notifyDataSetChanged();
                                slRefresh.setRefreshing(false);
                                slRefresh.setEnabled(true);
                                refreshRecyclerView();
                                // 会话列表变化时调用统计接口刷新统计数值
                                callCountApi(false);
                            }
                            Log.e(TAG, "!!!!!chatList done");
                        }
                    });
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                slRefresh.setRefreshing(false);
                slRefresh.setEnabled(true);
            }
        });
    }

    // 是否出诊状态
    private boolean isVisiting = false;

    /**
     * F27.APP.01.03 出诊／收工／离开
     */
    private void callVisitApi() {
        OTRequest otRequest = new OTRequest(this);
        // DATA
        DataModel data = new DataModel();
        if (!isVisiting) {
            // 出诊
            data.setParam(WORK_FLAG, "online");
        } else {
            // 收工
            data.setParam(WORK_FLAG, "offline");
        }
        data.setParam(DOC_ID, docId);
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN(TN_VISIT);

        NetTool.getInstance().startRequest(false, true, this, null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        if (!isVisiting) {
                            // 出诊
                            isVisiting = true;
                            setBtnVisiting();
                            chatLogin();
                        } else {
                            // 收工
                            // 退出环信聊天
                            chatLogout();
                        }
                    }
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    private RefreshFriendListBroadcastReceiver receiver;
    private static final String BROADCAST_REFRESH_LIST = "broadcastRefreshList";
    private static final String MESSAGE_USER_NAME = "messageUserName";

    /**
     * 接收application发来的广播，更新好友列表
     */
    private class RefreshFriendListBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "!!!!!!!!!!!!doctorFragment has received message");
            if (Application.BROADCAST_REFRESH_LIST.equals(intent.getAction())) {
                ArrayList<String> fromList = intent.getStringArrayListExtra("user_from");
                boolean isRefresh = true;
                if (null != fromList && 0 < fromList.size()) {
                    for (String from : fromList) {
                        if (null != dataChatList && 0 < dataChatList.size()) {
                            for (Map<String, String> map : dataChatList) {
                                if (map.get("ACCID").equalsIgnoreCase(from)) {
                                    isRefresh = false;
                                }
                            }
                        } else {
                            isRefresh = false;
                        }
                    }
                }
                // 判断当前列表中没有该患者再去调用接口，减少请求服务器次数
                if (isRefresh) {
                    callPatListApi(false);
                }
                if (null != patAdapter) {
                    setDataChatListUnRead();
                    // 获取未读消息
                    patAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void setDataChatListUnRead() {
        if (null != dataChatList && 0 < dataChatList.size()) {
            for (int i = 0; i < dataChatList.size(); i++) {
                int unReadNumber = 0;
                try {
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(dataChatList.get(i).get("ACCID"));
                    unReadNumber = conversation.getUnreadMsgCount();
                    Log.e(TAG, "unReadNumber = " + String.valueOf(unReadNumber));
                } catch (Exception e) {
                    unReadNumber = 0;
                }
                dataChatList.get(i).put("readNo", String.valueOf(unReadNumber));
            }
        }
    }

    /**
     * 医生登出
     */
    private void callLogoutApi() {
        LoginRequest request = new LoginRequest();
        request.setReqType(LOGOUT);
        NetTool.getInstance().startRequest(true, true, this, request, null, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if ("200".equals(resultCode)) {
                    // 登出成功
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (null != response.get(ERRO_MSG)) {
                        try {
                            Toast.makeText(MainActivity.this,
                                    String.valueOf(response.get(ERRO_MSG)), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.logout_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 下拉刷新
     */
    private void refreshRecyclerView() {
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        slRefresh.setEnabled(false);
                        slRefresh.setRefreshing(false);
//                        getChatList();
                        pageNo = 1;
                        dataChatList.clear();
                        patAdapter = null;
                        callPatListApi(false);
                    }
                }, 600);
            }
        });
    }

    /**
     * 环信登录
     */
    private void chatLogin() {
        Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!chat =   " + docId + " and = " + ThriftPreUtils.getLoginPassword(this));
        EMClient.getInstance().login(docId, ThriftPreUtils.getLoginPassword(this), new EMCallBack() {
            //        EMClient.getInstance().login("ceshi", "111111", new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("onSuccess: ", "登录成功");
                // 获取患者列表
                pageNo = 1;
                dataChatList.clear();
                patAdapter = null;
                callPatListApi(true);
            }

            @Override
            public void onError(int i, String s) {
                Log.e("onError: ", i + " " + s + "登录失败");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isVisiting = false;
                        setBtnRest();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.chat_failed), Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 环信登出
     */
    private void chatLogout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Log.e(TAG, "chat logout success");
                        isVisiting = false;
                        setBtnRest();
                        dataChatList.clear();
                        pageNo = 1;
                        if (null != patAdapter) {
                            patAdapter.notifyDataSetChanged();
                        }
                        checkedPosition = -1;
                        tvNoContact.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        slRefresh.setEnabled(false);
                        slRefresh.setRefreshing(true);
//                        fragmentManager = getSupportFragmentManager().beginTransaction();
//                        fragmentManager = getFragmentManager().beginTransaction();
//                        // 出栈所有fragment
//                        fragmentManager.popBackStack(null, 1);
                        getSupportFragmentManager().popBackStack(null, 1);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.chat_logout_failed), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.chat_has_been_tick_out), Toast.LENGTH_LONG).show();
                        chatLogout();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                        } else {
                            //当前网络不可用，请检查网络设置
                        }

                    }
                }
            });
        }
    }

    /**
     * 出诊状态底部按钮处理
     */
    private void setBtnVisiting() {
        btnVisit.setText(getResources().getString(R.string.visiting));
        btnVisit.setTextColor(getResources().getColor(R.color.visit_blue));
        btnVisit.setEnabled(false);
        btnTakeRest.setText(getResources().getString(R.string.take_rest));
    }

    /**
     * 休息状态底部按钮处理
     */
    private void setBtnRest() {
        btnVisit.setText(getResources().getString(R.string.visit));
        btnVisit.setTextColor(getResources().getColor(R.color.colorWhite));
        btnVisit.setEnabled(true);
        btnTakeRest.setText(getResources().getString(R.string.logout));
    }

    private void callUpdate() {
        Log.d(TAG, "update running");
        final String url = "https://zy.renyibao.com/ver.json";
        okHttpClient = (new OkHttpClient.Builder()).retryOnConnectionFailure(true).connectTimeout(5L, TimeUnit.SECONDS)
                .cache(new Cache(Environment.getExternalStorageDirectory(), 10485760L)).build();
        Request MEDIA_TYPE_MARKDOWN = (new okhttp3.Request.Builder()).url(url).build();
        okHttpClient.newCall(MEDIA_TYPE_MARKDOWN).enqueue(new Callback() {
            public void onFailure(Call call, final IOException e) {
                MainActivity.this.handler.post(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d(TAG, "update connect failed");
                            }
                        });
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String resp = response.body().string();
                        if (!TextUtils.isEmpty(resp)) {
                            MainActivity.this.handler.post(new Runnable() {
                                public void run() {
                                    HashMap mapObj = new HashMap();
                                    Gson gson = new Gson();
                                    Map<String, String> map = (Map<String, String>) gson.fromJson(resp, mapObj.getClass());
                                    if (null != map) {
                                        Log.d(TAG, "update resp returned");
//                                        Double ver = 0.0;
//                                        if (null != map.get("VERSION")) {
//                                            try {
//                                                ver = Double.parseDouble(map.get("VERSION"));
//                                            } catch (NumberFormatException e) {
//                                                ver = 0.0;
//                                            } catch (Exception e) {
//                                                ver = 0.0;
//                                            }
//                                        }
                                        // 服务器请求版本号
                                        final String newVersionCode = map.get("VERSION");
                                        // 本地versionCode
                                        final long pkgVerCode = AppUtils.getAppVersion(MainActivity.this);
                                        // 本地versionName
                                        final String pkgVersionName = AppUtils.getAppVersionName(MainActivity.this);
                                        Log.d(TAG, "newVersionCode = " + String.valueOf(newVersionCode)
                                                + " and pkgVersionCode = " + String.valueOf(pkgVerCode)
                                                + "and pkgVersionName = " + pkgVersionName);
                                        // 更新地址
                                        String urlAdr = "";
                                        if (!TextUtils.isEmpty(map.get("APPURL"))) {
                                            urlAdr = map.get("APPURL");
                                        }
                                        final String downloadUrl = urlAdr;
                                        // TODO: 2017/6/15 测试下载地址
//                                        final String downloadUrl = "http://business.cdn.qianqian.com/cms/BaiduMusic-pcwebapphomedown1.apk";
                                        String apkName = "";
                                        if (!TextUtils.isEmpty(map.get("APKNAME"))) {
                                            apkName = map.get("APKNAME");
                                        }
                                        final String newApkName = apkName;
//                                        if (newVersionCode - pkgVerCode > 0) {
                                        if (!newVersionCode.equals(pkgVersionName)) {
                                            // 版本更新提示
                                            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                                            builder.setTitle(getString(R.string.update_title));
                                            builder.setCancelable(false);
                                            builder.setMessage(getString(R.string.update_txt));
                                            builder.setPositiveButton(getString(R.string.update_ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
//                                                    String fileName = String.format(getString(R.string.apk_name), newVersionName);
                                                    String fileName = newApkName;
                                                    String filePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
                                                    String fileAbsolutePath = Environment.getExternalStorageDirectory() + filePath;
                                                    File file = new File(fileAbsolutePath);
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }

                                                    if (ConnectionUtils.isWifi(MainActivity.this)) {
                                                        downloadNewApp(downloadUrl, fileName, false);
                                                    } else {
                                                        // 如果当前是3G或4G等移动网络提示用户
                                                        showMobileNetworkAlert(downloadUrl, fileName);
                                                    }
                                                }
                                            });
                                            builder.setNegativeButton(getString(R.string.update_cancel),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            builder.create().show();
                                            Log.d(TAG, "update alertDialog show");
                                        }
                                    }

                                }
                            });
                        }
                    } catch (IOException var4) {
                        ;
                    }
                }

            }
        });
    }

    void showMobileNetworkAlert(final String url, final String fileName) {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.alert_dialog_title));
        builder.setMessage(getString(R.string.network_is_mobile));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.later_wifi_download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downloadNewApp(url, fileName, false);
            }
        });
        builder.setNegativeButton(getString(R.string.network_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downloadNewApp(url, fileName, true);
            }
        });
        builder.create().show();
    }

    private long mDownloadId;
    private boolean isCompleted;
    private DownloadManager manager;

    /**
     * 下载apk
     */
    public void downloadNewApp(String url, String fileName, boolean canMobileNetworkDownload) {
        try {
            String filePath = getApplicationContext().getFilesDir().getAbsolutePath();

            manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
            down.setTitle(fileName);
            if (canMobileNetworkDownload) {
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            } else {
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            }
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            down.setVisibleInDownloadsUi(true);

            //6.0系统强开权限
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            PermissionUtil.requestPermission(MainActivity.this, PERMISSIONS_STORAGE, 1);

            createFolder(filePath);
            if (hasSDCard()) {
                down.setDestinationInExternalPublicDir(filePath, fileName);
            }
//            manager.enqueue(down);
            mDownloadId = manager.enqueue(down);
            // 使用RxJava对下载信息进行轮询，500毫秒一次
            Observable.interval(300, 500, TimeUnit.MILLISECONDS)
                    .takeUntil(new Func1<Long, Boolean>() {
                        @Override
                        public Boolean call(Long aLong) {
                            return isCompleted;
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            checkDownloadStatus();
                        }
                    });
        } catch (Exception e) {
            showDownloadFailedDialog();
        }
    }

    private ProgressDialog mProgressDialog;

    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadId);//筛选下载任务，传入任务ID，可变参数
        Cursor cursor = manager.query(query);
        if (cursor.moveToFirst()) {
            long downloadedBytes = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            long totalBytes = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            mProgressDialog.setMax(((int) (totalBytes / 1024)));
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    mProgressDialog.setProgress(((int) (downloadedBytes / 1024)));
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    isCompleted = true;
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
            }
        }
        cursor.close();
    }

    private void createFolder(String url) {
        File folder = new File(url);
        if (!(folder.exists() && folder.isDirectory())) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }

    public boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private void showDownloadFailedDialog() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.download_fail_title));
        builder.setMessage(getString(R.string.download_fail_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                toWebsite();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void toWebsite() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(getString(R.string.share_title_url));
        intent.setData(content_url);
        startActivity(intent);
    }

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressNumberFormat("%1d KB/%2d KB");
        progressDialog.setMessage(getString(R.string.updating_progress_tittle));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), getString(R.string.quit), Toast.LENGTH_SHORT).show();

            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
