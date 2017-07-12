package com.witnsoft.interhis.setting.myhistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/7/12.
 */

@ContentView(R.layout.fragment_my_history_detail)
public class MyHistoryDetailFragment extends ChildBaseFragment {

    private View rootView;
    private Map<String, String> detailMap = new HashMap<>();

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.iv_head)
    private CircleImageView ivHead;
    @ViewInject(R.id.tv_name)
    private TextView tvName;
    @ViewInject(R.id.tv_sex)
    private TextView tvSex;
    @ViewInject(R.id.tv_age)
    private TextView tvAge;
    @ViewInject(R.id.tv_time)
    private TextView tvTime;
    @ViewInject(R.id.tv_ask_type)
    private TextView tvAskType;
    @ViewInject(R.id.tv_is_pay)
    private TextView tvIsPay;
    @ViewInject(R.id.tv_fee)
    private TextView tvFee;
    @ViewInject(R.id.tv_brief_introduction)
    private TextView tvBriefIntroduction;
    @ViewInject(R.id.tv_brief_detail)
    private TextView tvBriefDetail;
    @ViewInject(R.id.ll_introduction)
    private AutoScaleLinearLayout llIntroduction;
    @ViewInject(R.id.ll_detail)
    private AutoScaleLinearLayout llDetail;

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
        SerializableMap map = (SerializableMap) getArguments().get("history_detail");
        if (null != map) {
            detailMap = map.getMap();
            if (!TextUtils.isEmpty(detailMap.get(""))) {
                Glide.with(getActivity())
                        .load(detailMap.get(""))
                        .error(R.drawable.touxiang)
                        .into(ivHead);
            }
            // 姓名
            setText(tvName, detailMap.get("PATNAME"));
            // 性别
            setText(tvSex, detailMap.get("PATSEXNAME"));
            // 年龄
            String age;
            if (!TextUtils.isEmpty(detailMap.get("PATNL"))) {
                if (detailMap.get("PATNL").contains(getActivity().getResources().getString(R.string.age))) {
                    age = detailMap.get("PATNL");
                } else {
                    age = detailMap.get("PATNL") + getActivity().getResources().getString(R.string.age);
                }
            } else {
                age = "";
            }
            setText(tvAge, detailMap.get("PATNL"));
            // 创建时间
            setText(tvTime, detailMap.get("CREATETIME"));
            // 是否初诊
            if (!TextUtils.isEmpty(detailMap.get("WZMD"))) {
                if ("first".equals(detailMap.get("WZMD"))) {
                    setText(tvAskType, detailMap.get("WZMD"));
                } else if ("more".equals(detailMap.get("WZMD"))) {
                    setText(tvAskType, detailMap.get("WZMD"));
                }
            }
            // 是否付费
            if (!TextUtils.isEmpty(detailMap.get("PAYFLAG"))) {
                if ("y".equals(detailMap.get("PAYFLAG"))) {
                    setText(tvIsPay, getActivity().getResources().getString(R.string.has_pay));
                } else if ("n".equals(detailMap.get("PAYFLAG"))) {
                    setText(tvIsPay, getActivity().getResources().getString(R.string.ever_pay));
                }
            }
            // 付款金额
            setText(tvFee, detailMap.get("ASKFEE"));
            // 疾病名称
            if(!TextUtils.isEmpty(detailMap.get("JBMC"))){
                llIntroduction.setVisibility(View.VISIBLE);
                setText(tvBriefIntroduction, detailMap.get("JBMC"));
            }else {
                llIntroduction.setVisibility(View.GONE);
            }
            // 疾病明细
            if(!TextUtils.isEmpty(detailMap.get("JBMX"))){
                llDetail.setVisibility(View.VISIBLE);
                setText(tvBriefDetail, detailMap.get("JBMX"));
            }else {
                llDetail.setVisibility(View.GONE);
            }
        }
    }

    private void setText(TextView tv, String str) {
        if (!TextUtils.isEmpty(str)) {
            tv.setText(str);
        } else {
            tv.setText("");
        }
    }
}
