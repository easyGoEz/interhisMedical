package com.witnsoft.interhis.setting.myincome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.utils.ui.ItemSettingRight;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/13.
 */

@ContentView(R.layout.fragment_my_income)
public class MyIncomeFragment extends ChildBaseFragment {
    View rootView;

    // 提现入口
    @ViewInject(R.id.rl_to_cash)
    private ItemSettingRight rlToCash;
    // 我的银行卡入口
    @ViewInject(R.id.rl_bank_card)
    private ItemSettingRight rlBankCard;
    // 收入账单入口
    @ViewInject(R.id.rl_bill)
    private ItemSettingRight rlBill;

    // 账户余额
    @ViewInject(R.id.tv_balance)
    private TextView tvBalance;
    // 我的总收入
    @ViewInject(R.id.tv_income)
    private TextView tvIncome;
    // 本月金额
    @ViewInject(R.id.tv_month)
    private TextView tvMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = x.view().inject(this, inflater, container);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initClick();
        init();
    }

    private void init() {
        // 提现
        rlToCash.setTvTitle(getResources().getString(R.string.to_cash));
        // 银行卡
        rlBankCard.setTvTitle(getResources().getString(R.string.my_bank_card), false);
        // 收入账单
        rlBill.setTvTitle(getResources().getString(R.string.income_bill));
        tvBalance.setText("1500");
        tvIncome.setText("3000");
        tvMonth.setText("1000");
    }

    private void initClick() {
        RxView.clicks(rlToCash)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        toWithdrawCrash();
                    }
                });
        RxView.clicks(rlBankCard)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        toMyBankCard();
                    }
                });
        RxView.clicks(rlBill)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        toIncomeBill();
                    }
                });
    }

    private void toWithdrawCrash() {
        WithdrawCrashFragment introductionFragment = new WithdrawCrashFragment();
        pushFragment(introductionFragment, null, true);
    }

    private void toMyBankCard() {
        MyBankCardFragment myBankCardFragment = new MyBankCardFragment();
        pushFragment(myBankCardFragment, null, true);
    }

    private void toIncomeBill() {
        InComeBillFragment inComeBillFragment = new InComeBillFragment();
        pushFragment(inComeBillFragment, null, true);
    }
}
