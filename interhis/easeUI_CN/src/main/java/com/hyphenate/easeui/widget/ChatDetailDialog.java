package com.hyphenate.easeui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/7/6.
 */

public class ChatDetailDialog {

    private Context context;
    private String userId;
    private String medType;
    private String medNo;
    private String medCount;
    private String medPrice;
    private String name;
    private String acsm;
    private JSONArray jsonArray;

    private AlertDialog ad;

    private TextView tvName;
    private TextView tvSex;
    private TextView tvPatId;
    private TextView tvAiId;
    private TextView tvTime;
    private TextView tvPrice;
    private TextView tvCount;
    private LinearLayout llCount;
    private TextView tvUsage;
    private GridView gvMed;

    private ChatDetailAdapter chatDetailAdapter = null;

    public ChatDetailDialog(Context context, String userId,
                            String medType, String medNo,
                            String medCount, String medPrice,
                            String name, String acsm, JSONArray jsonArray) {
        this.context = context;
        this.userId = userId;
        this.medType = medType;
        this.medNo = medNo;
        this.medCount = medCount;
        this.medPrice = medPrice;
        this.name = name;
        this.acsm = acsm;
        this.jsonArray = jsonArray;
    }

    public void init() {
        // 是否为编辑弹出框
        ad = new AlertDialog.Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.dialog_chat_detail);
        tvName = (TextView) window.findViewById(R.id.tv_name);
        tvSex = (TextView) window.findViewById(R.id.tv_sex);
        tvPatId = (TextView) window.findViewById(R.id.tv_pat_id);
        tvAiId = (TextView) window.findViewById(R.id.tv_aiid);
        tvTime = (TextView) window.findViewById(R.id.tv_time);
        tvPrice = (TextView) window.findViewById(R.id.tv_price);
        tvCount = (TextView) window.findViewById(R.id.tv_count);
        llCount = (LinearLayout) window.findViewById(R.id.ll_count);
        tvUsage = (TextView) window.findViewById(R.id.tv_usage);
        gvMed = (GridView) window.findViewById(R.id.gv_med);
        initUi();
    }

    private void initUi() {
        setText(tvName, name);
        setText(tvSex, "");
        setText(tvPatId, userId);
        setText(tvAiId, "");
        setText(tvTime, "");
        setText(tvUsage, acsm);
//        setText(tvPrice, convert(medPrice));
        if (!TextUtils.isEmpty(medPrice)) {
            tvPrice.setText(convert(medPrice) + "元");
        } else {
            tvPrice.setText("");
        }
        if (!TextUtils.isEmpty(medType)) {
            if ("中药".equals(medType)) {
                gvMed.setNumColumns(GridView.AUTO_FIT);
                llCount.setVisibility(View.VISIBLE);
                setText(tvCount, medCount + "付");
            } else {
                llCount.setVisibility(View.GONE);
                gvMed.setNumColumns(1);
            }
        } else {
            llCount.setVisibility(View.GONE);
        }
        chatDetailAdapter = new ChatDetailAdapter(context, jsonArray, medType);
        gvMed.setAdapter(chatDetailAdapter);
    }

    private void setText(TextView tv, String str) {
        if (!TextUtils.isEmpty(str)) {
            tv.setText(str);
        } else {
            tv.setText("");
        }
    }

    static String convert(String value) {
        try {
            long l1 = Math.round(Double.parseDouble(value) * 100);
            double ret = l1 / 100.00;
            return String.valueOf(ret);
        } catch (Exception e) {
            return "";
        }
    }
}
