package com.witnsoft.interhis.setting.myincome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.setting.myhistory.model.SerializableMap;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/7/19.
 */

@ContentView(R.layout.fragment_income_bill_detail)
public class IncomeBillDetailFragment extends ChildBaseFragment {

    private View rootView;
    private Map<String, String> detailMap = new HashMap<>();

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
    }

    private void init() {
        final SerializableMap map = (SerializableMap) getArguments().get("income_bill");
    }
}
