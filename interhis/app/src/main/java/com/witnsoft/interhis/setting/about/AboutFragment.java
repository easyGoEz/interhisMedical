package com.witnsoft.interhis.setting.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.base.ChildBaseFragment;
import com.witnsoft.interhis.utils.AppUtils;
import com.witnsoft.interhis.utils.ui.ItemSettingRight;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/13.
 */

@ContentView(R.layout.fragment_about)
public class AboutFragment extends ChildBaseFragment {
    View rootView;

    @ViewInject(R.id.rl_imprint)
    private ItemSettingRight rlImprint;
    @ViewInject(R.id.tv_version_name)
    private TextView tvVersionName;

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
        rlImprint.setTvTitle(getActivity().getResources().getString(R.string.imprint));
        final String pkgVersionName = AppUtils.getAppVersionName(getActivity());
        String fileName = String.format(getString(R.string.about_version_name), pkgVersionName);
        if (!TextUtils.isEmpty(fileName)) {
            tvVersionName.setText(fileName);
        }
    }

    private void initClick() {
        RxView.clicks(rlImprint)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        toImprint();
                    }
                });
    }

    private void toImprint() {
        ImPrintFragment imPrintFragment = new ImPrintFragment();
        pushFragment(imPrintFragment, null, true);
    }
}
