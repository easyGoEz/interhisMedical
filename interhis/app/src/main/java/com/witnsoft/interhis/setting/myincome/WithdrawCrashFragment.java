package com.witnsoft.interhis.setting.myincome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.base.ChildBaseFragment;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

@ContentView(R.layout.fragment_withdraw_crash)
public class WithdrawCrashFragment extends ChildBaseFragment {

    @ViewInject(R.id.btn_withdraw)
    private Button btnWithDraw;
    @ViewInject(R.id.et_count)
    private EditText etCount;
    @ViewInject(R.id.tv_my_balance)
    private TextView tvMyBalance;
    @ViewInject(R.id.tv_withdraw_way)
    private TextView tvWithDrawWay;
    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.tv_withdraw_all)
    private TextView tvWithdrawAll;
    @ViewInject(R.id.ll_tip)
    private AutoScaleLinearLayout llTip;
    @ViewInject(R.id.tv_tip)
    private TextView tvTip;

    private View rootView;

    // 余额
    private String balanceCount = "1100.05";

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
        initCountEdit();
        init();
        initClick();
    }

    private void initClick() {
        // 返回
        RxView.clicks(llBack)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        finishFragment();
                    }
                });
        // 全部提现
        RxView.clicks(tvWithdrawAll)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(balanceCount)) {
                            double balanceCountDb = 0.0;
                            try {
                                balanceCountDb = Double.parseDouble(balanceCount);
                            } catch (Exception e) {
                                balanceCountDb = 0.0;
                            }
                            if (0 < balanceCountDb) {
                                etCount.setText(balanceCount);
                                llTip.setVisibility(View.GONE);
                                btnWithDraw.setEnabled(true);
                            } else {
                                llTip.setVisibility(View.VISIBLE);
                                btnWithDraw.setEnabled(false);
                            }
                        }
                    }
                });
    }

    private void init() {
        btnWithDraw.setEnabled(false);
        // 提现微信账号
        String weChat = "1234567890";
        tvWithDrawWay.setText(String.format(getString(R.string.we_chat_change), weChat));
        tvMyBalance.setText(String.format(getString(R.string.my_balance), balanceCount));
    }

    private void initCountEdit() {
        etCount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etCount.setText(s);
                        etCount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etCount.setText(s);
                    etCount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etCount.setText(s.subSequence(0, 1));
                        etCount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String count = etCount.getText().toString();
                if (!TextUtils.isEmpty(count)) {
                    if (count.trim().substring(0).equals(".")) {
                        count = "0";
                    }
                    try {
                        if (Double.parseDouble(count) > Double.parseDouble(balanceCount)) {
                            inputCountBeyond();
                        } else {
                            inputCountNormal();
                        }
                    } catch (Exception e) {
                        inputCountError();
                    }
                } else {
                    llTip.setVisibility(View.GONE);
                    btnWithDraw.setEnabled(false);
                }
            }
        });
    }

    // 输入金额异常处理
    private void inputCountError() {
        llTip.setVisibility(View.VISIBLE);
        tvTip.setText(getActivity().getResources().getString(R.string.count_error));
        btnWithDraw.setEnabled(false);
    }

    // 输入金额超额处理
    private void inputCountBeyond() {
        llTip.setVisibility(View.VISIBLE);
        tvTip.setText(getActivity().getResources().getString(R.string.withdraw_tip));
        btnWithDraw.setEnabled(false);
    }

    // 出入金额正常处理
    private void inputCountNormal() {
        llTip.setVisibility(View.GONE);
        btnWithDraw.setEnabled(true);
    }

}
