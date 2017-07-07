package com.witnsoft.interhis.setting;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.setting.about.AboutFragment;
import com.witnsoft.interhis.setting.myhistory.MyHistoryFragment;
import com.witnsoft.interhis.setting.myincome.MyIncomeFragment;
import com.witnsoft.interhis.setting.myinfo.MyInfoFragment;
import com.witnsoft.interhis.utils.ui.HisKeyboardView;
import com.witnsoft.libinterhis.base.BaseActivity;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libinterhis.utils.ui.AutoScaleRelativeLayout;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/13.
 */

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements MyInfoFragment.CallBackPathImg {

    public static final String DOC_NAME = "docName";
    public static final String DOC_LEVEL = "docLevel";
    public static final String DOC_HOSP = "docHosp";
    public static final String DOC_DEPT = "docDept";
    public static final String DOC_HEAD = "docHead";
    public static final String UPDATE_IMG = "updateImg";

    private String docName = "";
    private String docLevel = "";
    private String docHospName = "";
    private String docDept = "";
    private String docHeadImg = "";
    private int isRefresh = -1;

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    // 我的信息
    @ViewInject(R.id.rl_my_info)
    private AutoScaleRelativeLayout rlMyInfo;
    // 我的问诊记录
    @ViewInject(R.id.rl_my_history)
    private AutoScaleRelativeLayout rlMyHistory;
    // 我的收入
    @ViewInject(R.id.rl_my_income)
    private AutoScaleRelativeLayout rlMyIncome;
    // 关于
    @ViewInject(R.id.rl_about)
    private AutoScaleRelativeLayout rlAbout;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initCLick();
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(UPDATE_IMG, isRefresh);
        setResult(2, intent);
        finish();
    }

    private void initCLick() {
        // 返回
        RxView.clicks(llBack)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent();
                        intent.putExtra(UPDATE_IMG, isRefresh);
                        setResult(2, intent);
                        finish();
                    }
                });
        // 我的信息
        RxView.clicks(rlMyInfo)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        MyInfoFragment myInfoFragment = new MyInfoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(DOC_NAME, docName);
                        bundle.putString(DOC_LEVEL, docLevel);
                        bundle.putString(DOC_HOSP, docHospName);
                        bundle.putString(DOC_DEPT, docDept);
                        myInfoFragment.setArguments(bundle);
                        setChecked(myInfoFragment, rlMyInfo, rlAbout, rlMyHistory, rlMyIncome);
                    }
                });
        // 我的问诊记录
        RxView.clicks(rlMyHistory)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        MyHistoryFragment myHistoryFragment = new MyHistoryFragment();
                        setChecked(myHistoryFragment, rlMyHistory, rlAbout, rlMyInfo, rlMyIncome);
                    }
                });
        // 我的收入
        RxView.clicks(rlMyIncome)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        MyIncomeFragment myIncomeFragment = new MyIncomeFragment();
                        setChecked(myIncomeFragment, rlMyIncome, rlAbout, rlMyInfo, rlMyHistory);
                    }
                });
        // 关于
        RxView.clicks(rlAbout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        AboutFragment aboutFragment = new AboutFragment();
                        setChecked(aboutFragment, rlAbout, rlMyIncome, rlMyInfo, rlMyHistory);
                    }
                });
    }

    private void init() {
        docName = getIntent().getStringExtra(DOC_NAME);
        docLevel = getIntent().getStringExtra(DOC_LEVEL);
        docHospName = getIntent().getStringExtra(DOC_HOSP);
        docDept = getIntent().getStringExtra(DOC_DEPT);
        docHeadImg = getIntent().getStringExtra(DOC_HEAD);
        fragmentManager = getFragmentManager();
        MyInfoFragment myInfoFragment = new MyInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DOC_NAME, docName);
        bundle.putString(DOC_LEVEL, docLevel);
        bundle.putString(DOC_HOSP, docHospName);
        bundle.putString(DOC_DEPT, docDept);
        bundle.putString(DOC_HEAD, docHeadImg);
        myInfoFragment.setArguments(bundle);
        setChecked(myInfoFragment, rlMyInfo, rlAbout, rlMyHistory, rlMyIncome);
    }

    private void setChecked(Fragment fragment, RelativeLayout llChecked, RelativeLayout ll1, RelativeLayout ll2, RelativeLayout ll3) {
        llChecked.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        ll1.setBackgroundColor(getResources().getColor(R.color.background_divider_color));
        ll2.setBackgroundColor(getResources().getColor(R.color.background_divider_color));
        ll3.setBackgroundColor(getResources().getColor(R.color.background_divider_color));
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_content, fragment);
        ft.commit();
    }

    @Override
    public void setIsRefresh(int isRefresh) {
        // 头像上传成功的回调
        this.isRefresh = isRefresh;
    }
}
