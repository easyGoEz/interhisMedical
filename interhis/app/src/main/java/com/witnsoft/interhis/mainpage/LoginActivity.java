package com.witnsoft.interhis.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.base.BaseActivity;
import com.witnsoft.libinterhis.utils.ClearEditText;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libnet.model.LoginRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by liyan on 2017/5/15.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    private static final long THROTTLE_TIME = 500;
    private static final String LOGIN = "login";
    private static final String ERR_MSG = "errmsg";

    @ViewInject(R.id.btn_login)
    private Button btnLogin;
    @ViewInject(R.id.et_userName)
    private ClearEditText etUserName;
    @ViewInject(R.id.et_user_password)
    private ClearEditText etUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
        // 登录
        initClick(this.btnLogin, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                login();
            }
        });
    }

    private void init() {
        // 显示缓存用户名
        if (!TextUtils.isEmpty(ThriftPreUtils.getLoginName(LoginActivity.this))) {
            etUserName.setText(ThriftPreUtils.getLoginName(LoginActivity.this));
        }
    }

    private void login() {
        if (TextUtils.isEmpty(etUserName.getText().toString())) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_user_name_tip),
                    Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(etUserPassword.getText().toString())) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_user_password_tip),
                    Toast.LENGTH_LONG).show();
        } else {
            callLoginApi(etUserName.getText().toString(), etUserPassword.getText().toString());
        }
    }

    // 医生登录
    private void callLoginApi(final String name, final String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(name);
        request.setPassword(password);
        request.setReqType(LOGIN);
        NetTool.getInstance().startRequest(true, true, LoginActivity.this, request, null, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                // 登录成功将用户名存本地
                ThriftPreUtils.putLoginName(LoginActivity.this, name);
                ThriftPreUtils.putLoginPassword(LoginActivity.this, password);
                if ("200".equals(resultCode)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    if (null != response.get(ERR_MSG)) {
                        try {
                            Toast.makeText(LoginActivity.this,
                                    String.valueOf(response.get(ERR_MSG)), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initClick(View view, Action1<Void> action1) {
        RxView.clicks(view)
                .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(action1);
    }

}
