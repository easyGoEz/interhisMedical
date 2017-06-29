package com.witnsoft.interhis.setting.myinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by zhengchengpeng on 2017/6/16.
 */

@ContentView(R.layout.fragment_my_expert)
public class MyExpertFragment extends ChildBaseFragment {

    View rootView;

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.ll_edit)
    private AutoScaleLinearLayout llEdit;
    @ViewInject(R.id.tv_edit)
    private TextView tvEdit;
    @ViewInject(R.id.et_my_expert)
    private EditText etMyExpert;


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
    }

    private void init() {

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
        RxView.clicks(llEdit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!etMyExpert.isEnabled()) {
                            // 触发"编辑"动作
                            etMyExpert.setEnabled(true);
                            tvEdit.setText(getResources().getString(R.string.save));
                        } else {
                            // 触发"保存"动作
                            savePersonalIntroduction();
                        }
                    }
                });
    }

    // TODO: 2017/6/14 上传接口，isSuccess模拟上传成功和失败
    private void savePersonalIntroduction() {
        boolean isSuccess = true;
        if (isSuccess) {
            etMyExpert.setEnabled(false);
            Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_LONG).show();
            tvEdit.setText(getResources().getString(R.string.edit));
        } else {
            etMyExpert.setEnabled(true);
        }
    }
}
