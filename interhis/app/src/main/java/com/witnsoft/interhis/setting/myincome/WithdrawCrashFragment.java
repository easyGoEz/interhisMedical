package com.witnsoft.interhis.setting.myincome;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.setting.ChildBaseFragment;
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

    @ViewInject(R.id.et_count)
    private EditText etCount;
    @ViewInject(R.id.tv_my_balance)
    private TextView tvMyBalance;
    @ViewInject(R.id.tv_withdraw_way)
    private TextView tvWithDrawWay;

    private View rootView;

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;

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
        RxView.clicks(llBack)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        finishFragment();
                    }
                });
    }

    private void init() {
        // 提现微信账号
        String weChat = "1234567890";
        tvWithDrawWay.setText(String.format(getString(R.string.we_chat_change), weChat));
        // 余额
        String balanceCount = "1100.00";
        tvMyBalance.setText(String.format(getString(R.string.my_balance), balanceCount));

    }

    private void initCountEdit() {
        etCount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        etCount.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int length = dest.toString().substring(index).length();
                    if (length == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});
    }

}
