package com.witnsoft.interhis.setting.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.mainpage.LoginActivity;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

@ContentView(R.layout.fragment_intrduction)
public class IntroductionFragment extends ChildBaseFragment {

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.ll_edit)
    private AutoScaleLinearLayout llEdit;
    @ViewInject(R.id.tv_edit)
    private TextView tvEdit;
    @ViewInject(R.id.et_introduction)
    private EditText etIntroduction;

    private View rootView;

    private String docId;

    private final class ErrCode {
        private static final String ErrCode_200 = "200";
        private static final String ErrCode_504 = "504";
    }

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
                        if (!etIntroduction.isEnabled()) {
                            // 触发"编辑"动作
                            etIntroduction.setEnabled(true);
                            tvEdit.setText(getResources().getString(R.string.save));
                        } else {
                            // 触发"保存"动作
                            callSavePersonalIntroduction();
                        }
                    }
                });
    }

    private void init() {
        docId = ThriftPreUtils.getDocId(getActivity());
    }

    /**
     * F27.APP.01.09 保存个人简介
     */
    private void callSavePersonalIntroduction() {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("docid", docId);
        // 个人简介接口标识
        data.setParam("modcol", "gerenjianjie");
        // 简介内容
        data.setParam("modcontent", etIntroduction.getText().toString());
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.09");

        NetTool.getInstance().startRequest(false, true, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    etIntroduction.setEnabled(false);
                    Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_LONG).show();
                    tvEdit.setText(getResources().getString(R.string.edit));
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    etIntroduction.setEnabled(true);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                etIntroduction.setEnabled(true);
            }
        });
    }
}
