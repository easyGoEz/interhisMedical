package com.witnsoft.interhis.setting.myhistory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.mainpage.LoginActivity;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.setting.myhistory.model.SerializableMap;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
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
    private String aiid;

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
    // 诊断
    @ViewInject(R.id.ll_diagnosis)
    private AutoScaleLinearLayout llDiagnosis;
    @ViewInject(R.id.tv_diagnosis_tip)
    private TextView tvDiagnosisTip;
    // 处方药
    @ViewInject(R.id.ll_med)
    private AutoScaleLinearLayout llMed;
    @ViewInject(R.id.tv_med_tip)
    private TextView tvMedTip;

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
        final SerializableMap map = (SerializableMap) getArguments().get("history_detail");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                freshTopUi(map);
            }
        });
        callHistory();
    }

    private void freshTopUi(SerializableMap map) {
        if (null != map) {
            detailMap = map.getMap();
            aiid = detailMap.get("AIID");
            if (!TextUtils.isEmpty(detailMap.get("PATPHOTOURL"))) {
                Glide.with(getActivity())
                        .load(detailMap.get("PATPHOTOURL"))
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
            setText(tvAge, age);
            // 创建时间
            setText(tvTime, detailMap.get("CREATETIME"));
            // 是否初诊
            if (!TextUtils.isEmpty(detailMap.get("WZMD"))) {
                if ("first".equals(detailMap.get("WZMD"))) {
                    setText(tvAskType, getActivity().getResources().getString(R.string.first_ask));
                } else if ("more".equals(detailMap.get("WZMD"))) {
                    setText(tvAskType, getActivity().getResources().getString(R.string.more_ask));
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
            if (!TextUtils.isEmpty(detailMap.get("JBMC"))) {
                llIntroduction.setVisibility(View.VISIBLE);
                setText(tvBriefIntroduction, detailMap.get("JBMC"));
            } else {
                llIntroduction.setVisibility(View.GONE);
            }
            // 疾病明细
            if (!TextUtils.isEmpty(detailMap.get("JBMX"))) {
                llDetail.setVisibility(View.VISIBLE);
                setText(tvBriefDetail, detailMap.get("JBMX"));
            } else {
                llDetail.setVisibility(View.GONE);
            }
        }
    }

    /**
     * F27.APP.01.15 查看处方，诊断历史记录
     */
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            AutoScaleLinearLayout.LayoutParams.WRAP_CONTENT, AutoScaleLinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams matchParams = new LinearLayout.LayoutParams(
            AutoScaleLinearLayout.LayoutParams.MATCH_PARENT, AutoScaleLinearLayout.LayoutParams.WRAP_CONTENT);

    private void callHistory() {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("aiid", aiid);
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.15");

        NetTool.getInstance().startRequest(false, true, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        if (null != response) {
                            final Map<String, Object> data = (Map<String, Object>) response.get("DATA");
                            if (null != data) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 处方
                                        if (null != data.get("chufang")) {
                                            List<Map<String, Object>> listMed = (List<Map<String, Object>>) data.get("chufang");
                                            if (null != listMed && 0 < listMed.size()) {
                                                llMed.setVisibility(View.VISIBLE);
                                                tvMedTip.setVisibility(View.VISIBLE);
                                                llMed.removeAllViews();
                                                for (Map<String, Object> map : listMed) {
                                                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_history_detail_med, null, false);
                                                    matchParams.setMargins(0, px2dip(getActivity(), 5), 0, 0);
                                                    view.setLayoutParams(matchParams);
                                                    // 药方
                                                    TextView tvMedContent = (TextView) view.findViewById(R.id.tv_med_content);
                                                    // 时间
                                                    TextView tvMedTime = (TextView) view.findViewById(R.id.tv_med_time);
                                                    // 中西药区分
                                                    TextView tvMedType = (TextView) view.findViewById(R.id.tv_med_type);
                                                    // 处方号
                                                    TextView tvMedNo = (TextView) view.findViewById(R.id.tv_med_no);
                                                    // 数量
                                                    TextView tvMedCount = (TextView) view.findViewById(R.id.tv_med_count);
                                                    AutoScaleLinearLayout llMedCount = (AutoScaleLinearLayout) view.findViewById(R.id.ll_med_count);
                                                    // 价格
                                                    TextView tvMedPrice = (TextView) view.findViewById(R.id.tv_med_price);
                                                    setText(tvMedTime, String.valueOf(map.get("cftime")));
                                                    setText(tvMedNo, String.valueOf(map.get("cfh")));
                                                    setText(tvMedPrice, String.valueOf(map.get("cfje")));
                                                    boolean isChinese = true;
                                                    if (!TextUtils.isEmpty(String.valueOf(map.get("cflb")))) {
                                                        if ("chinese".equals(String.valueOf(map.get("cflb")))) {
                                                            tvMedType.setText("中药");
                                                            llMedCount.setVisibility(View.VISIBLE);
                                                            setText(tvMedCount, String.valueOf(map.get("cfmxs")));
                                                            isChinese = true;
                                                        } else {
                                                            tvMedType.setText("西药");
                                                            llMedCount.setVisibility(View.INVISIBLE);
                                                            isChinese = false;
                                                        }
                                                    }
                                                    if (null != map.get("chufangmx")) {
                                                        List<Map<String, String>> list = (List<Map<String, String>>) map.get("chufangmx");
                                                        if (null != list && 0 < list.size()) {
                                                            String medContent = "";
                                                            for (Map<String, String> mapDetail : list) {
                                                                if (isChinese) {
                                                                    // 中药
                                                                    medContent = medContent + mapDetail.get("ymc") + "  " + mapDetail.get("yje") + "元："
                                                                            + mapDetail.get("ydj") + "元" + strWithSign(mapDetail.get("ysl")) + "\n";
                                                                } else {
                                                                    // 西药
                                                                    medContent = medContent + mapDetail.get("ymc") + "（" + mapDetail.get("yggmc") + "）  "
                                                                            + mapDetail.get("yje") + "元" + "：" + mapDetail.get("ydj") + "元"
                                                                            + strWithDay(mapDetail.get("ysl")) + "\n";
                                                                }
                                                            }
                                                            setText(tvMedContent, medContent);
                                                        }
                                                    }
                                                    llMed.addView(view);
                                                }
                                            }
                                        }
                                        // 诊断
                                        if (null != data.get("zdxx")) {
                                            List<Map<String, String>> listDiagnosis = (List<Map<String, String>>) data.get("zdxx");
                                            if (null != listDiagnosis && 0 < listDiagnosis.size()) {
                                                llDiagnosis.setVisibility(View.VISIBLE);
                                                tvDiagnosisTip.setVisibility(View.VISIBLE);
                                                llDiagnosis.removeAllViews();
                                                for (Map<String, String> map : listDiagnosis) {
                                                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_diagnosis, null, false);
                                                    view.setLayoutParams(params);
                                                    TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
                                                    TextView tvText = (TextView) view.findViewById(R.id.tv_text);
                                                    setText(tvText, map.get("zdsm"));
                                                    setText(tvTime, map.get("optime"));
                                                    llDiagnosis.addView(view);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    private String strWithDay(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.contains("天")) {
                return "×" + str;
            } else {
                return "×" + str + "天";
            }
        } else {
            return "";
        }
    }

    private String strWithSign(String str) {
        if (!TextUtils.isEmpty(str)) {
            return "×" + str;
        } else {
            return "";
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
