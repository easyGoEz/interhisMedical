package com.hyphenate.easeui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.ChatDetailDialog;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by liyan on 2017/5/9.
 */

public class MyEaseChatRowProject extends EaseChatRow {

    private TextView tvUserName, tvYaofangType, tvYaofangNum, tvYaoNum, tvYaofangPrice, tvSearchContent;
    private ImageView ivYaofangIv;

    public MyEaseChatRowProject(Context context, EMMessage message, int position, BaseAdapter adapter, String imgDoc, String imgPat) {
        super(context, message, position, adapter, imgDoc, imgPat);
        // TODO Auto-generated constructor stub
    }

    /**
     * 注入布局a
     */
    @Override
    protected void onInflateView() {
        // TODO Auto-generated method stub
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_project
                : R.layout.ease_row_sent_project, this);
    }

    /**
     * 寻找id
     */
    @Override
    protected void onFindViewById() {
        tvUserName = (TextView) findViewById(R.id.tv_yaofang_name);
        tvYaofangType = (TextView) findViewById(R.id.tv_yaofang_type);
        ivYaofangIv = (ImageView) findViewById(R.id.iv_yaofang);
        tvYaofangNum = (TextView) findViewById(R.id.tv_yaofang_num);
        tvYaoNum = (TextView) findViewById(R.id.tv_yao_num);
        tvYaofangPrice = (TextView) findViewById(R.id.tv_yaofang_price);
        tvSearchContent = (TextView) findViewById(R.id.tv_searchcontent);
//        searchContent.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
    }

    /**
     * 刷新列表视图
     */
    @Override
    protected void onUpdateView() {
        // TODO Auto-generated method stub
        adapter.notifyDataSetChanged();
    }

    static double convert(double value) {
        long l1 = Math.round(value * 100);
        double ret = l1 / 100.00;
        return ret;
    }

    /**
     * 显示消息和位置等属性
     */
    @Override
    protected void onSetUpView() {

        if (message.getBooleanAttribute("yaofang", true)) {
            userId = message.getStringAttribute("userName", null);
            yaofangType = message.getStringAttribute("yaofangType", null);
            aiid = message.getStringAttribute("aiid", null);
            yaoNum = message.getStringAttribute("yaoNum", null);
            yaofangPrice = message.getStringAttribute("yaofangPrice", null);
            name = message.getStringAttribute("name", null);
            acsm = message.getStringAttribute("acsm", null);
            patSexName = message.getStringAttribute("pat_sex_name", null);
            patId = message.getStringAttribute("pat_id", null);
            acid = message.getStringAttribute("acid", null);
            try {
                medJson = message.getJSONArrayAttribute("med_json");
            } catch (Exception e) {

            }

            this.tvUserName.setText("您给" + message.getStringAttribute("name", "患者") + "开了一个");
            this.tvYaofangType.setText(yaofangType + "处方");
            this.tvYaofangNum.setText("药方号：" + acid);
            if ("中药".equals(yaofangType)) {
                this.tvYaoNum.setVisibility(VISIBLE);
                this.tvYaoNum.setText("共" + yaoNum + "付");
            } else {
                this.tvYaoNum.setVisibility(GONE);
            }
            double price = 0.0;
            try {
                price = Double.parseDouble(yaofangPrice);
            } catch (Exception e) {
                price = 0.0;
            }
            price = convert(price);
            this.tvYaofangPrice.setText(String.valueOf(price) + "元");

        }

    }

    private String userId;
    private String yaofangType;
    private String aiid;
    private String yaoNum;
    private String yaofangPrice;
    private String name;
    // 用法用量
    private String acsm;
    // 处方明细
    private JSONArray medJson;
    private String patSexName;
    private String patId;
    private String acid;

    /**
     * 点击气泡
     */
    @Override
    protected void onBubbleClick() {
        // 处方详细
        final ChatDetailDialog chatDetailDialog
                = new ChatDetailDialog(activity, patId, yaofangType, aiid, yaoNum, yaofangPrice, name, acsm, patSexName, medJson);
        chatDetailDialog.init();
    }

}
